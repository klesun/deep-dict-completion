package org.klesun.deep_dict_completion.resolvers;

import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.klesun.deep_dict_completion.*;
import org.klesun.deep_dict_completion.helpers.IFuncCtx;
import org.klesun.deep_dict_completion.helpers.MultiType;
import org.klesun.deep_dict_completion.resolvers.var_res.AssRes;
import org.klesun.lang.Lang;
import org.klesun.lang.Opt;
import org.klesun.lang.Tls;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VarRes extends Lang
{
    final private IFuncCtx ctx;

    public VarRes(IFuncCtx ctx)
    {
        this.ctx = ctx;
    }

    /**
     * extends type with the key assignment information
     * @TODO: make it immutable! Don't rely on side effects... if you remove Tls.onDemand() it will not work
     */
    private static void addAssignment(L<S<MultiType>> dest, Assign assign, boolean overwritingAssignment)
    {
        if (assign.keys.size() == 0) {
            if (assign.didSurelyHappen && overwritingAssignment) dest.clear();
            dest.add(assign.assignedType);
        } else {
            if (dest.fap(g -> g.get().types).size() == 0) {
                dest.add(Tls.onDemand(() -> new MultiType(list(new DeepType(assign.psi)))));
            }
            String nextKey = assign.keys.remove(0);
            dest.fap(g -> g.get().types).fch(type -> {
                if (nextKey == null) {
                    // index key
                    L<S<MultiType>> getters = list(Tls.onDemand(() -> new MultiType(L(type.indexTypes))));
                    addAssignment(getters, assign, false);
                    type.indexTypes = getters.fap(g -> g.get().types);
                } else {
                    // associative key
                    if (!type.keys.containsKey(nextKey)) {
                        type.addKey(nextKey, assign.psi);
                    }
                    addAssignment(type.keys.get(nextKey).getTypeGetters(), assign, true);
                }
            });
            assign.keys.add(0, nextKey);
        }
    }

    /**
     * this function should be rewritten so that it did
     * not demand the type of actual variable, cuz it
     * causes recursion, side effects... such nasty stuff
     */
    private static List<DeepType> assignmentsToTypes(List<Assign> assignments)
    {
        L<S<MultiType>> resultTypes = list();

        // assignments are supposedly in chronological order
        assignments.forEach(ass -> addAssignment(resultTypes, ass, true));

        return resultTypes.fap(g -> g.get().types);
    }

    /**
     * does same thing as variable::multiResolve(), but apparently multiResolve may trigger
     * global index for some reason, causing Contract Violation in Type Provider
     *
     * @return L<PsiElement> - all references of this variable in the function
     * this list may include Parameter and Variable instances, I guess simple definition
     * would be: everything that starts with ${varName} in this function scope
     */
    private L<PsiElement> findReferences(PyReferenceExpression variable)
    {
        // if this line is still here when you read this, that means I
        // decided to just do DumbService::isDumb() check in Type Provider
        return L(variable.getReference().multiResolve(false))
            .fop(res -> opt(res.getElement()));
    }

    public List<DeepType> resolve(PyReferenceExpression variable)
    {
        List<Assign> revAsses = list();
        L<PsiElement> references = findReferences(variable)
//            .flt(refPsi -> ScopeFinder.didPossiblyHappen(refPsi, variable))
            ;

        for (int i = references.size() - 1; i >= 0; --i) {
            PsiElement refPsi = references.get(i);
//            boolean didSurelyHappen = ScopeFinder.didSurelyHappen(refPsi, variable);
            boolean didSurelyHappen = false;
            Opt<Assign> assignOpt = Opt.fst(list(opt(null)
                , (new AssRes(ctx)).collectAssignment(refPsi, didSurelyHappen)
//                , assertForeachElement(refPsi)
//                    .map(elTypes -> new Assign(list(), elTypes, didSurelyHappen, refPsi))
//                , assertTupleAssignment(refPsi)
//                    .map(elTypes -> new Assign(list(), elTypes, didSurelyHappen, refPsi))
//                , Tls.cast(ParameterImpl.class, refPsi)
//                    .map(param -> {
//                        S<MultiType> mtg = () -> new ArgRes(ctx).resolveArg(param);
//                        return new Assign(list(), mtg, true, refPsi);
//                    })
            ));
            if (assignOpt.has()) {
                Assign ass = assignOpt.unw();
                revAsses.add(ass);
                if (didSurelyHappen && ass.keys.size() == 0) {
                    // direct assignment, everything before it is meaningless
                    break;
                }
            }
        }

        List<Assign> assignments = list();
        for (int i = revAsses.size() - 1; i >= 0; --i) {
            assignments.add(revAsses.get(i));
        }

        return assignmentsToTypes(assignments);
    }
}
