package org.klesun.deep_dict_completion.go_to_decl_providers;

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import com.jetbrains.python.psi.PySubscriptionExpression;
import com.jetbrains.python.psi.impl.PyStringLiteralExpressionImpl;
import org.jetbrains.annotations.Nullable;
import org.klesun.deep_dict_completion.helpers.*;
import org.klesun.deep_dict_completion.helpers.SearchContext;
import org.klesun.lang.Lang;
import org.klesun.lang.Tls;

import java.util.*;

/**
 * go to declaration functionality for associative array keys
 */
public class DeepKeysGoToDecl extends Lang implements GotoDeclarationHandler
{
    private static PsiElement truncateOnLineBreak(PsiElement psi)
    {
        PsiElement truncated = psi.getFirstChild();
        while (psi.getText().contains("\n") && truncated != null) {
            psi = truncated;
            truncated = psi.getFirstChild();
        }
        return psi;
    }

    // just treating a symptom. i dunno why duplicates appear - they should not
    private static void removeDuplicates(L<PsiElement> psiTargets)
    {
        Set<PsiElement> fingerprints = new HashSet<>();
        int size = psiTargets.size();
        for (int k = size - 1; k >= 0; --k) {
            if (fingerprints.contains(psiTargets.get(k))) {
                psiTargets.remove(k);
            }
            fingerprints.add(psiTargets.get(k));
        }
    }

    @Nullable
    @Override
    public PsiElement[] getGotoDeclarationTargets(PsiElement psiElement, int i, Editor editor)
    {
        SearchContext search = new SearchContext().setDepth(35);
        IFuncCtx funcCtx = new FuncCtx(search, L());

        L<PsiElement> psiTargets = opt(psiElement.getParent())
            .fap(toCast(PyStringLiteralExpression.class))
            .fap(literal -> Lang.opt(literal.getParent())
                .fap(toCast(PySubscriptionExpression.class))
                .map(sub -> sub.getOperand())
                .map(var -> funcCtx.findExprType(var))
                .map(mt -> mt.getKeyPsi(literal.getStringValue())))
            .def(list());

        removeDuplicates(psiTargets);

        return psiTargets
            .map(psi -> truncateOnLineBreak(psi))
            .toArray(new PsiElement[psiTargets.size()]);
    }

    @Nullable
    @Override
    public String getActionText(DataContext dataContext) {
        // dunno what this does
        return null;
    }
}
