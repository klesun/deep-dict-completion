package org.klesun.deep_dict_completion.resolvers;

import com.jetbrains.python.psi.PyTupleExpression;
import org.klesun.deep_dict_completion.DeepType;
import org.klesun.deep_dict_completion.helpers.IFuncCtx;
import org.klesun.deep_dict_completion.helpers.MultiType;
import org.klesun.lang.Lang;

public class TupRes extends Lang
{
    final private IFuncCtx ctx;

    public TupRes(IFuncCtx ctx)
    {
        this.ctx = ctx;
    }

    public DeepType resolve(PyTupleExpression expr)
    {
        DeepType tupleType = new DeepType(expr);

        // keyed elements
        L<MultiType> tuple = L(expr.getElements())
            .map(elem -> ctx.findExprType(elem));

        tuple.fch((mt, i) -> {
            if (tupleType.tupleTypes.containsKey(i)) {
                mt = new MultiType(list(mt.types, tupleType.tupleTypes.get(i).types).fap(a -> a));
            }
            tupleType.tupleTypes.remove(i);
            tupleType.tupleTypes.put(i, mt);
        });

        return tupleType;
    }
}
