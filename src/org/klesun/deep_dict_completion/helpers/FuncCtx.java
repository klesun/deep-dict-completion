package org.klesun.deep_dict_completion.helpers;

import com.jetbrains.python.psi.PyExpression;
import org.jetbrains.annotations.NotNull;
import org.klesun.lang.Lang;
import org.klesun.lang.Opt;

import java.util.HashMap;

/** a node in called function stack trace with args */
public class FuncCtx extends Lang implements IFuncCtx
{
    final private static boolean USE_CACHING = false;

    final private SearchContext search;
    final private L<Lang.S<MultiType>> argGetters;

    private HashMap<Integer, MultiType> cachedArgs = new HashMap<>();

    public FuncCtx(SearchContext search, L<S<MultiType>> argGetters)
    {
        this.argGetters = argGetters;
        this.search = search;
    }

    public FuncCtx(FuncCtx parentCtx, L<S<MultiType>> argGetters)
    {
        this(parentCtx.search, argGetters);
    }

    public Opt<MultiType> getArg(Integer index)
    {
        return argGetters.gat(index).map(argGetter -> {
            if (!cachedArgs.containsKey(index)) {
                cachedArgs.put(index, argGetter.get());
            }
            return cachedArgs.get(index);
        });
    }

    @NotNull
    public MultiType findExprType(PyExpression expr)
    {
        MultiType result = search.findExprType(expr, this).def(new MultiType(L()));
        return result;
    }

    public IFuncCtx subCtx(Lang.L<Lang.S<MultiType>> args) {
        return new FuncCtx(this, args);
    }

    public int getArgCnt()
    {
        return argGetters.size();
    }

    public SearchContext getSearch()
    {
        return search;
    }
}
