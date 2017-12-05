package org.klesun.deep_dict_completion.resolvers;

import com.jetbrains.python.psi.PyDictLiteralExpression;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyListLiteralExpression;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import org.klesun.deep_dict_completion.DeepType;
import org.klesun.deep_dict_completion.helpers.IFuncCtx;
import org.klesun.deep_dict_completion.helpers.MultiType;
import org.klesun.lang.Lang;

public class ListRes extends Lang
{
    final private IFuncCtx ctx;

    public ListRes(IFuncCtx ctx)
    {
        this.ctx = ctx;
    }

    public DeepType resolve(PyListLiteralExpression expr)
    {
        L<DeepType> types = L(expr.getElements())
            .fap((elem) -> ctx.findExprType(elem).types);
        DeepType listType = new DeepType(expr);
        listType.indexTypes.addAll(types);
        return listType;
    }
}
