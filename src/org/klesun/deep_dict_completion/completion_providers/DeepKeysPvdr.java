package org.klesun.deep_dict_completion.completion_providers;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PySubscriptionExpression;
import com.jetbrains.python.psi.impl.PyStringLiteralExpressionImpl;
import icons.PythonIcons;
import org.jetbrains.annotations.NotNull;
import org.klesun.deep_dict_completion.DeepType;
import org.klesun.deep_dict_completion.helpers.FuncCtx;
import org.klesun.deep_dict_completion.helpers.MultiType;
import org.klesun.deep_dict_completion.helpers.SearchContext;
import org.klesun.lang.Lang.L;
import org.klesun.lang.Opt;

import java.util.HashSet;
import java.util.Set;

import static org.klesun.lang.Lang.list;
import static org.klesun.lang.Lang.opt;
import static org.klesun.lang.Lang.toCast;

public class DeepKeysPvdr extends CompletionProvider<CompletionParameters>
{
    protected void addCompletions(
        @NotNull CompletionParameters parameters,
        ProcessingContext processingContext,
        @NotNull CompletionResultSet result
    ) {
        SearchContext search = new SearchContext().setDepth(30);
        FuncCtx funcCtx = new FuncCtx(search, list());

        L<String> keys = opt(parameters.getPosition().getParent())
            .fap(toCast(PyStringLiteralExpressionImpl.class))
            .map(lit -> lit.getParent())
            .fap(toCast(PySubscriptionExpression.class))
            .map(sub -> sub.getOperand())
            .map(var -> {
                MultiType mt = funcCtx.findExprType(var);
                return mt.getKeyNames();
            })
            .def(list());

        keys.map(key -> LookupElementBuilder.create(key)
                .bold()
                .withIcon(PythonIcons.Python.PropertyGetter)
                .withTypeText("IAnime"))
            .map((look, i) -> PrioritizedLookupElement.withPriority(look, 3000 - i))
            .fch(result::addElement);

        Set<String> suggested = new HashSet<>(list(
            keys.map(k -> "'" + k + "'"),
            keys.map(k -> "\"" + k + "\"")
        ).fap(a -> a));

        result.runRemainingContributors(parameters, otherSourceResult -> {
            // remove dupe buil-in suggestions
            LookupElement lookup = otherSourceResult.getLookupElement();
            if (!suggested.contains(lookup.getLookupString())) {
                result.addElement(lookup);
            }
        });
    }
}
