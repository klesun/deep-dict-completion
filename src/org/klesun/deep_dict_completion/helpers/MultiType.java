package org.klesun.deep_dict_completion.helpers;

import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.types.PyType;
import com.jetbrains.python.psi.types.PyUnionType;
import org.klesun.deep_dict_completion.DeepType;
import org.klesun.lang.Lang;
import org.klesun.lang.Tls;

import java.util.HashSet;

/**
 * this data structure represents a list of
 * DeepTypes-s that some variable mya have
 * it's more readable type annotation than L<DeepType>
 *
 * it also probably could give some handy methods
 * like getKey(), elToArr(), arToEl() - all the
 * static functions that take list of typeGetters
 */
public class MultiType extends Lang
{
    static enum REASON {OK, CIRCULAR_REFERENCE, FAILED_TO_RESOLVE, DEPTH_LIMIT, INVALID_PSI}
    public static MultiType CIRCULAR_REFERENCE = new MultiType(L(), REASON.CIRCULAR_REFERENCE);
    public static MultiType INVALID_PSI = new MultiType(L(), REASON.INVALID_PSI);

    private REASON reason;
    final public L<DeepType> types;

    public MultiType(L<DeepType> types, REASON reason)
    {
        this.types = types;
        this.reason = reason;
    }
    public MultiType(L<DeepType> types)
    {
        this(types, REASON.OK);
    }

    public MultiType getEl()
    {
        return new MultiType(types.fap(arrt -> {
            L<DeepType> mixed = L();
            mixed.addAll(arrt.indexTypes);
            arrt.keys.forEach((k,v) -> mixed.addAll(v.getTypes()));
            return mixed;
        }));
    }

    public DeepType getInArray(PsiElement call)
    {
        DeepType result = new DeepType(call);
        result.indexTypes.addAll(types);
        return result;
    }

    public String getStringValue()
    {
        if (types.size() == 1) {
            return types.get(0).stringValue;
        } else {
            return null;
        }
    }

    public boolean hasKey(String keyName)
    {
        return types.flt(t -> t.keys.containsKey(keyName)).size() > 0;
    }

    public L<PsiElement> getKeyPsi(String keyName)
    {
        return list(
            // if keyName is a constant string - return type of this key only
            types.flt(t -> keyName != null)
                .fop(type -> Lang.getKey(type.keys, keyName))
                .map(v -> v.definition),
            // if keyName is a var - return types of all keys
            types.flt(t -> keyName == null)
                .fap(t -> L(t.keys.values()))
                .map(v -> v.definition)
        ).fap(a -> a);
    }

    public MultiType getKey(String keyName)
    {
        return new MultiType(list(
            // if keyName is a constant string - return type of this key only
            types.flt(t -> keyName != null)
                .fop(type -> Lang.getKey(type.keys, keyName))
                .fap(v -> v.getTypes()),
            // if keyName is a var - return types of all keys
            types.flt(t -> keyName == null)
                .fap(t -> L(t.keys.values()))
                .fap(v -> v.getTypes()),
            types.fap(t -> t.indexTypes)
        ).fap(a -> a));
    }

    public MultiType getTupleAt(int index)
    {
        return new MultiType(types.fop(type -> opt(type.tupleTypes.get(index))).fap(v -> v.types));
    }

    public MultiType getReturnType(IFuncCtx ctx)
    {
        L<DeepType> result = types.fap(t -> t.returnTypeGetters.fap(g -> g.apply(ctx)));
        return new MultiType(result);
    }

    public L<String> getKeyNames()
    {
        L<String> names = L();
        HashSet<String> repeations = new HashSet<>();
        types.fap(t -> L(t.keys.keySet())).fch(name -> {
            if (!repeations.contains(name)) {
                repeations.add(name);
                names.add(name);
            }
        });
        return names;
    }

    public String getBriefTypeText()
    {
        L<String> briefTypes = list();
        L<String> keyNames = getKeyNames();
        if (keyNames.size() > 0) {
            briefTypes.add("{" + Tls.implode(", ", keyNames.map(k -> k + ":")) + "}");
        }
        L<String> strvals = types.fop(t -> opt(t.stringValue));
        if (strvals.size() > 0) {
            briefTypes.add(Tls.implode("|", strvals.map(s -> "'" + s + "'")));
        }
        int tupleCnt = types.fap(t -> L(t.tupleTypes.keySet()))
            .rdc((a,b) -> Math.max(a, b), 0);
        if (tupleCnt > 0) {
            briefTypes.add("(" + Tls.implode(", ", Tls.range(0, tupleCnt).map(i -> "a" + i)) + ")");
        }
        if (types.any(t -> t.indexTypes.size() > 0)) {
            briefTypes.add("[...]");
        }
        briefTypes.addAll(types.fop(t -> opt(t.briefType)));
        String fullStr = Tls.implode("|", briefTypes);
        String truncated = Tls.substr(fullStr, 0, 40);
        return truncated.length() == fullStr.length()
            ? truncated : truncated + "...";
    }

    public String toJson()
    {
        return DeepType.toJson(types, 0);
    }
}
