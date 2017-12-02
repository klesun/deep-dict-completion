package org.klesun.deep_dict_completion.completion_providers;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.util.ProcessingContext;
import icons.PythonIcons;
import org.jetbrains.annotations.NotNull;

public class DeepKeysPvdr extends CompletionProvider<CompletionParameters>
{
    protected void addCompletions(
        @NotNull CompletionParameters parameters,
        ProcessingContext processingContext,
        @NotNull CompletionResultSet result
    ) {
        result.addElement(LookupElementBuilder.create("Milky Holmes")
            .bold()
            .withIcon(PythonIcons.Python.PropertyGetter)
            .withTypeText("IAnime"));
    }
}
