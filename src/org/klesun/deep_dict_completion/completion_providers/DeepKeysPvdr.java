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

import javax.swing.*;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import static org.klesun.lang.Lang.list;
import static org.klesun.lang.Lang.opt;
import static org.klesun.lang.Lang.toCast;

public class DeepKeysPvdr extends CompletionProvider<CompletionParameters>
{
    URL imgURL = getClass().getResource("../icons/deep_16_ruby2.png");
    ImageIcon icon = new ImageIcon(imgURL);

    protected void addCompletions(
        @NotNull CompletionParameters parameters,
        ProcessingContext processingContext,
        @NotNull CompletionResultSet result
    ) {
        SearchContext search = new SearchContext().setDepth(30);
        FuncCtx funcCtx = new FuncCtx(search, list());

        MultiType dictType = opt(parameters.getPosition().getParent())
            .fap(toCast(PyStringLiteralExpressionImpl.class))
            .map(lit -> lit.getParent())
            .fap(toCast(PySubscriptionExpression.class))
            .map(sub -> sub.getOperand())
            .map(var -> funcCtx.findExprType(var))
            .def(MultiType.INVALID_PSI);
        L<String> names = dictType.getKeyNames();

        names.map(key -> LookupElementBuilder.create(key)
                .bold()
                .withIcon(icon)
                .withTypeText(dictType.getKey(key).getBriefTypeText()))
            .map((look, i) -> PrioritizedLookupElement.withPriority(look, 3000 - i))
            .fch(result::addElement);

        Set<String> suggested = new HashSet<>(list(
            names.map(k -> "'" + k + "'"),
            names.map(k -> "\"" + k + "\"")
        ).fap(a -> a));

        result.runRemainingContributors(parameters, otherSourceResult -> {
            // remove dupe built-in suggestions
            LookupElement lookup = otherSourceResult.getLookupElement();
            if (!suggested.contains(lookup.getLookupString())) {
                result.addElement(lookup);
            }
        });
    }
}
