package org.klesun.deep_dict_completion;

import com.intellij.psi.*;
import com.jetbrains.python.psi.*;
import org.klesun.deep_dict_completion.helpers.IFuncCtx;
import org.klesun.deep_dict_completion.resolvers.ArrCtorRes;
import org.klesun.deep_dict_completion.resolvers.TupRes;
import org.klesun.deep_dict_completion.resolvers.VarRes;
import org.klesun.lang.Lang;
import org.klesun.lang.Opt;
import org.klesun.lang.Tls;

/**
 * Provides mechanism to determine expression type.
 * Unlike original jetbrain's type resolver, this
 * includes associative array key information
 */
public class DeepTypeResolver extends Lang
{
    /** @debug */
    public static Opt<L<DeepType>> resolveIn(PsiElement expr, IFuncCtx ctx)
    {
        return Opt.fst(list(
            opt(null) // for coma formatting
            , Tls.cast(PyReferenceExpression.class, expr)
                .map(v -> new VarRes(ctx).resolve(v))
            , Tls.cast(PyDictLiteralExpression.class, expr)
                .map(arr -> list(new ArrCtorRes(ctx).resolve(arr)))
            , Tls.cast(PyParenthesizedExpression.class, expr)
                .map(par -> par.getContainedExpression())
                .fap(toCast(PyTupleExpression.class))
                .map(tup -> list(new TupRes(ctx).resolve(tup)))
//                .map(arr -> list(new ArrCtorRes(ctx).resolve(arr)))
//            , Tls.cast(FunctionReferenceImpl.class, expr)
//                .map(call -> new FuncCallRes(ctx).resolve(call).types)
//            , Tls.cast(ArrayAccessExpressionImpl.class, expr)
//                .map(keyAccess -> new ArrAccRes(ctx).resolve(keyAccess).types)
//            , Tls.cast(FieldReferenceImpl.class, expr)
//                .map(fieldRef -> new FieldRes(ctx).resolve(fieldRef).types)
            , Tls.cast(PyStringLiteralExpression.class, expr)
                .map(lit -> list(new DeepType(lit)))
//            , Tls.cast(PhpExpressionImpl.class, expr)
//                .map(v -> v.getFirstChild())
//                .fap(toCast(FunctionImpl.class))
//                .map(lambda -> list(new ClosRes(ctx).resolve(lambda)))
//            , Tls.cast(PhpExpressionImpl.class, expr)
//                .fap(casted -> opt(casted.getText())
//                    .flt(text -> Tls.regex("^\\d+$", text).has())
//                    .map(Integer::parseInt)
//                    .map(num -> list(new DeepType(casted, num))))
//            // leave rest to MiscRes
//            , new MiscRes(ctx).resolve(expr)
        )).map(ts -> L(ts));
    }
}
