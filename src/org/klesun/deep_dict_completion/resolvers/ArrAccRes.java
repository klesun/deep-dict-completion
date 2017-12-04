package org.klesun.deep_dict_completion.resolvers;

import com.jetbrains.python.psi.PySubscriptionExpression;
import org.klesun.deep_dict_completion.helpers.IFuncCtx;
import org.klesun.deep_dict_completion.helpers.MultiType;
import org.klesun.lang.Lang;

public class ArrAccRes extends Lang
{
    final private IFuncCtx ctx;

    public ArrAccRes(IFuncCtx ctx)
    {
        this.ctx = ctx;
    }

    public MultiType resolve(PySubscriptionExpression keyAccess)
    {
        MultiType mt = opt(keyAccess.getOperand())
            .map(expr -> ctx.findExprType(expr))
            .def(MultiType.INVALID_PSI);

        return opt(keyAccess.getIndexExpression())
            .map(v -> opt(ctx.findExprType(v).getStringValue()))
            .map(keyName -> mt.getKey(keyName.def(null)))
            .def(MultiType.INVALID_PSI);
    }
}
