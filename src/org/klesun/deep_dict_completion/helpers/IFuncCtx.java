package org.klesun.deep_dict_completion.helpers;

import com.jetbrains.python.psi.PyExpression;
import org.jetbrains.annotations.NotNull;
import org.klesun.lang.Lang.L;
import org.klesun.lang.Lang.S;
import org.klesun.lang.Opt;

/**
 * provides arguments passed to the current function
 * and a method to solve inner PSI expressions
 */
public interface IFuncCtx
{
    public Opt<MultiType> getArg(Integer index);
    @NotNull
    public MultiType findExprType(PyExpression expr);
    public IFuncCtx subCtx(L<S<MultiType>> args);
    /** @debug */
    public int getArgCnt();
    public SearchContext getSearch();
}
