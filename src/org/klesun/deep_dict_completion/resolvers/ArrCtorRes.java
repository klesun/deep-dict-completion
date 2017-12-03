package org.klesun.deep_dict_completion.resolvers;

import com.jetbrains.python.psi.PyDictLiteralExpression;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import org.klesun.deep_dict_completion.DeepType;
import org.klesun.deep_dict_completion.helpers.IFuncCtx;
import org.klesun.lang.Lang;

public class ArrCtorRes extends Lang
{
    final private IFuncCtx ctx;

    public ArrCtorRes(IFuncCtx ctx)
    {
        this.ctx = ctx;
    }

    public DeepType resolve(PyDictLiteralExpression expr)
    {
        DeepType arrayType = new DeepType(expr);

        // keyed elements
        L(expr.getElements()).fch((keyRec) -> opt(keyRec.getValue())
            .fap(toCast(PyExpression.class))
            .map(v -> S(() -> ctx.findExprType(v)))
            .thn(getType -> opt(keyRec.getKey())
                .fap(toCast(PyStringLiteralExpression.class))
                .thn(lit -> arrayType.addKey(lit.getStringValue(), keyRec).addType(getType))));

        return arrayType;
    }
}
