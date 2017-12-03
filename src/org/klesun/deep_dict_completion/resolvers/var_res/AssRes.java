package org.klesun.deep_dict_completion.resolvers.var_res;

import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyAssignmentStatement;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import com.jetbrains.python.psi.PySubscriptionExpression;
import org.klesun.deep_dict_completion.Assign;
import org.klesun.deep_dict_completion.helpers.IFuncCtx;
import org.klesun.deep_dict_completion.helpers.MultiType;
import org.klesun.lang.Lang;
import org.klesun.lang.Opt;
import org.klesun.lang.Tls;

import java.util.List;

/**
 * provides functions to collect keys of an assignment and to
 * join multiple assignments into an array structure type
 */
public class AssRes extends Lang
{
    private IFuncCtx ctx;

    public AssRes(IFuncCtx ctx)
    {
        this.ctx = ctx;
    }

    // null in key chain means index (when it is number or variable, not named key)
    private Opt<T2<List<String>, S<MultiType>>> collectKeyAssignment(PyAssignmentStatement ass)
    {
        Opt<PySubscriptionExpression> nextKeyOpt = opt(ass.getLeftHandSideExpression())
            .fap(toCast(PySubscriptionExpression.class));

        List<String> reversedKeys = list();

        while (nextKeyOpt.has()) {
            PySubscriptionExpression nextKey = nextKeyOpt.def(null);

            String name = opt(nextKey.getIndexExpression())
                .fap(toCast(PyStringLiteralExpression.class))
                .map(t -> t.getStringValue())
                .def(null);
            reversedKeys.add(name);

            nextKeyOpt = opt(nextKey.getOperand())
                .fap(toCast(PySubscriptionExpression.class));
        }

        List<String> keys = list();
        for (int i = reversedKeys.size() - 1; i >= 0; --i) {
            keys.add(reversedKeys.get(i));
        }

        return opt(ass.getAssignedValue())
            .fap(toCast(PyExpression.class))
            .map(value -> T2(keys, S(() -> {
                MultiType mt = ctx.findExprType(value);
                return mt;
            })));
    }

    private static Opt<PyAssignmentStatement> findParentAssignment(PsiElement caretVar) {
        return opt(caretVar.getParent())
            .fap(parent -> Opt.fst(list(
                Tls.cast(PySubscriptionExpression.class, parent)
                    .fap(acc -> findParentAssignment(acc)),
                Tls.cast(PyAssignmentStatement.class, parent)
                    .flt(ass -> opt(ass.getLeftHandSideExpression())
                        .map(assVar -> caretVar.isEquivalentTo(assVar))
                        .def(false))
            )));
    }

    /**
     * @param varRef - `$var` reference or `$this->field` reference
     */
    public Opt<Assign> collectAssignment(PsiElement varRef, Boolean didSurelyHappen)
    {
        return findParentAssignment(varRef)
            .fap(ass -> collectKeyAssignment(ass)
                .map(tup -> new Assign(tup.a, tup.b, didSurelyHappen, ass)));
    }
}
