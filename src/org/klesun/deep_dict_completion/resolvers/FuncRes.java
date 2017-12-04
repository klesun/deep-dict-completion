package org.klesun.deep_dict_completion.resolvers;

import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyReturnStatement;
import org.klesun.deep_dict_completion.DeepType;
import org.klesun.deep_dict_completion.helpers.IFuncCtx;
import org.klesun.lang.Lang;
import org.klesun.lang.Tls;

public class FuncRes extends Lang
{
    final private IFuncCtx ctx;

    public FuncRes(IFuncCtx ctx)
    {
        this.ctx = ctx;
    }

    public static L<PyReturnStatement> findFunctionReturns(PsiElement funcBody)
    {
        L<PyReturnStatement> result = list();
        for (PsiElement child: funcBody.getChildren()) {
            // anonymous functions
            if (child instanceof PyFunction) continue;

            Tls.cast(PyReturnStatement.class, child)
                .thn(result::add);

            findFunctionReturns(child).forEach(result::add);
        }
        return result;
    }

    public DeepType resolve(PyFunction func)
    {
        DeepType result = new DeepType(func);
        findFunctionReturns(func)
            .map(ret -> ret.getExpression())
            .fop(toCast(PyExpression.class))
            .fch(retVal -> {
                F<IFuncCtx, L<DeepType>> rtGetter =
                    (funcCtx) -> funcCtx.findExprType(retVal).types;
                result.returnTypeGetters.add(rtGetter);
            });
        return result;
    }

}
