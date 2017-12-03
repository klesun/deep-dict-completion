package org.klesun.deep_dict_completion.entry;

import com.intellij.codeInsight.completion.*;
import com.intellij.patterns.PlatformPatterns;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import com.jetbrains.python.psi.PySubscriptionExpression;
import com.jetbrains.python.psi.impl.PyDictCompExpressionImpl;
import org.klesun.deep_dict_completion.completion_providers.*;

/**
 * provides associative array keys autocomplete
 * using declaration inside the initial function
 * that created this array
 */
public class DeepKeysCbtr extends CompletionContributor
{
    public DeepKeysCbtr()
    {
        this.extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .withSuperParent(1, PyStringLiteralExpression.class)
                .withSuperParent(2, PySubscriptionExpression.class)
                ,
            new DeepKeysPvdr()
        );
    }
}
