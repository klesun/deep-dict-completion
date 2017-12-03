package org.klesun.deep_dict_completion.helpers;

import com.jetbrains.python.psi.PyExpression;
import org.klesun.deep_dict_completion.DeepTypeResolver;
import org.klesun.lang.Lang;
import org.klesun.lang.Opt;

public class SearchContext extends Lang
{
    // parametrized fields
    private int depth = 20;
    private int initialDepth = depth;
    private boolean debug = false;
    // max expressions per single search - guard
    // against memory overflow in circular references
    private int maxExpressions = 20000;
    final public L<PyExpression> psiTrace = L();

    public SearchContext()
    {
    }

    public SearchContext setDepth(int depth)
    {
        this.depth = initialDepth = depth;
        return this;
    }

    public Opt<MultiType> findExprType(PyExpression expr, FuncCtx funcCtx)
    {
        if (depth <= 0) {
            return opt(null);
        } else if (--maxExpressions < 0) {
            /** @debug */
            System.out.println("Expression limit guard reached");
            return opt(null);
        }
        --depth;
        psiTrace.add(expr);

        if (debug) {
            for (int i = 0; i < initialDepth - depth; ++i) {
                System.out.print("| ");
            }
            System.out.println(depth + " " + expr.getText().split("\n")[0] + " " + expr.getClass());
        }

        Opt<MultiType> result = DeepTypeResolver.resolveIn(expr, funcCtx)
            .map(ts -> new MultiType(ts));

        psiTrace.remove(psiTrace.size() - 1);
        ++depth;
        return result;
    }
}
