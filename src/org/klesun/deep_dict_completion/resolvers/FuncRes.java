package org.klesun.deep_dict_completion.resolvers;

import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyReturnStatement;
import org.klesun.deep_dict_completion.DeepType;
import org.klesun.deep_dict_completion.helpers.FuncCtx;
import org.klesun.deep_dict_completion.helpers.IFuncCtx;
import org.klesun.deep_dict_completion.helpers.MultiType;
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

    public MultiType resolve(PyCallExpression funcCall)
    {
        L<PyExpression> args = L(funcCall.getArguments());
        L<S<MultiType>> argGetters = args.map((psi) -> () -> ctx.findExprType(psi));
        IFuncCtx funcCtx = ctx.subCtx(argGetters);
        return opt(funcCall.getCallee())
            .map(ref -> ctx.findExprType(ref))
            .map(mt -> mt.getReturnType(funcCtx))
            .def(MultiType.INVALID_PSI);
    }
}
