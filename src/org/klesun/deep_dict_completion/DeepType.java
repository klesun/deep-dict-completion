package org.klesun.deep_dict_completion;

import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import com.jetbrains.python.psi.types.PyType;
import org.apache.commons.lang.StringUtils;
import org.klesun.deep_dict_completion.helpers.FuncCtx;
import org.klesun.deep_dict_completion.helpers.IFuncCtx;
import org.klesun.deep_dict_completion.helpers.MultiType;
import org.klesun.deep_dict_completion.helpers.SearchContext;
import org.klesun.lang.Lang;
import org.klesun.lang.Tls;

import java.util.*;

/**
 * contains info about associative
 * array key typeGetters among other things
 */
public class DeepType extends Lang
{
    // keys and typeGetters of associative array
    public final LinkedHashMap<String, Key> keys = new LinkedHashMap<>();
    // possible typeGetters of list element
    public L<DeepType> indexTypes = list();
    public LinkedHashMap<Integer, MultiType> tupleTypes = new LinkedHashMap<>();
    // applicable to closures and function names
    // (starting with self::) and [$obj, 'functionName'] tuples
    // slowly migrating returnTypes from constant values to a function
    // list of functions that take arg list and return list of return types
    public final L<F<IFuncCtx, L<DeepType>>> returnTypeGetters = L();
    public final String stringValue;
    public final PsiElement definition;
    public String briefType = null;

    DeepType(PsiElement definition, String stringValue)
    {
        this.definition = definition;
        this.stringValue = stringValue;
    }

    public DeepType(PsiElement definition)
    {
        this(definition, null);
    }

    DeepType(PyStringLiteralExpression lit)
    {
        this(lit, lit.getStringValue());
    }

    public DeepType(PyExpression numPsi, Integer number)
    {
        this(numPsi, "" + number);
    }

    public L<DeepType> getReturnTypes(IFuncCtx ctx)
    {
        L<DeepType> result = returnTypeGetters.fap(g -> g.apply(ctx));
        return result;
    }

    public Key addKey(String name, PsiElement definition)
    {
        Key keyEntry = new Key(name, definition);
        keys.put(keyEntry.name, keyEntry);
        return keyEntry;
    }

    public static class Key
    {
        final public String name;
        final private L<S<MultiType>> typeGetters = L();
        // where Go To Definition will lead
        final public PsiElement definition;

        private Key(String name, PsiElement definition)
        {
            this.name = name;
            this.definition = definition;
        }

        public void addType(S<MultiType> getter)
        {
            typeGetters.add(Tls.onDemand(getter));
        }

        public L<DeepType> getTypes()
        {
            return typeGetters.fap(g -> g.get().types);
        }

        public L<S<MultiType>> getTypeGetters()
        {
            return typeGetters;
        }
    }

    private static String indent(int level)
    {
        return new String(new char[level]).replace("\0", "  ");
    }

    public static String toJson(List<DeepType> types, int level)
    {
        LinkedHashMap<String, List<DeepType>> mergedKeys = new LinkedHashMap<>();
        List<DeepType> indexTypes = list();
        LinkedHashMap<Integer, MultiType> tupleTypes = new LinkedHashMap<>();
        SearchContext search = new SearchContext();
        IFuncCtx funcCtx = new FuncCtx(search, list());
        MultiType returnType = new MultiType(L(types).fap(t -> t.returnTypeGetters.fap(rtg -> rtg.apply(funcCtx))));

        types.forEach(t -> {
            t.keys.forEach((k,v) -> {
                if (!mergedKeys.containsKey(k)) {
                    mergedKeys.put(k, list());
                }
                mergedKeys.get(k).addAll(v.getTypes());
            });
            t.tupleTypes.forEach((k,v) -> {
                if (tupleTypes.containsKey(k)) {
                    v = new MultiType(list(v.types, tupleTypes.get(k).types).fap(a -> a));
                }
                tupleTypes.remove(k);
                tupleTypes.put(k, v);
            });
            t.indexTypes.forEach(indexTypes::add);
        });

        if (mergedKeys.size() > 0) {
            String result = "{\n";
            ++level;
            for (Map.Entry<String, List<DeepType>> e: mergedKeys.entrySet()) {
                result += indent(level) + "\"" + e.getKey() + "\"" + ": " + toJson(e.getValue(), level) + ",\n";
            }
            --level;
            result += indent(level) + "}";
            return result;
        } else if (tupleTypes.size() > 0) {
            String result = "(";
            for (Map.Entry<Integer, MultiType> e: tupleTypes.entrySet()) {
                result += e.getValue().toJson() + ", ";
            }
            result += ")";
            return result;
        } else if (indexTypes.size() > 0) {
            return "[" + toJson(indexTypes, level) + "]";
        } else if (returnType.types.size() > 0) {
            return "() => " + returnType.toJson();
        } else {
            return "\"unknown\"";
        }
    }

    @Override
    public String toString()
    {
        return toJson(list(this), 0);
    }
}
