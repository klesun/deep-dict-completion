package org.klesun.deep_dict_completion.entry;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.PsiFile;
import com.intellij.ui.awt.RelativePoint;
import com.jetbrains.python.psi.PyExpression;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyStringLiteralExpression;
import com.jetbrains.python.psi.impl.PyFunctionImpl;
import com.jetbrains.python.psi.stubs.PyModuleNameIndex;
import org.klesun.deep_dict_completion.DeepType;
import org.klesun.deep_dict_completion.helpers.FuncCtx;
import org.klesun.deep_dict_completion.helpers.IFuncCtx;
import org.klesun.deep_dict_completion.helpers.MultiType;
import org.klesun.deep_dict_completion.helpers.SearchContext;
import org.klesun.deep_dict_completion.resolvers.FuncRes;
import org.klesun.lang.Opt;
import org.klesun.lang.Tls;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static org.klesun.lang.Lang.*;

public class RunTest extends AnAction
{
    private static Opt<L<PyFunction>> findTestDataPvdrFuncs(PsiFile psiFile)
    {
        L<PyFunction> meths = list();

        L(PyModuleNameIndex.find("deep_dict_completion_unit_test", psiFile.getProject(), false))
            .fch(mod -> meths.addAll(L(mod.getTopLevelFunctions())
                .fop(toCast(PyFunctionImpl.class))
                .flt(m -> opt(m.getName()).map(n -> n.startsWith("provide")).def(false))));

        return meths.size() > 0 ? opt(meths) : opt(null);
    }

    @Override
    public void actionPerformed(AnActionEvent e)
    {
        SearchContext search = new SearchContext().setDepth(30);
        IFuncCtx funcCtx = new FuncCtx(search, L());

        Logger logger = new Logger();
        logger.logMsg("Searching for \"deep_dict_completion_unit_test\" module in project...");
        List<Error> errors = opt(e.getData(LangDataKeys.PSI_FILE))
            .fap(file -> findTestDataPvdrFuncs(file))
            .map(funcs -> funcs
                .fap(func -> FuncRes.findFunctionReturns(func)
                    .map(ret -> ret.getExpression())
                    .fop(toCast(PyExpression.class))
                    .fap(retVal -> funcCtx.findExprType(retVal).types)
                    .fap(ltype -> L(ltype.tupleTypes.values())
                        .fop((rett, i) -> {
                            CaseContext ctx = new CaseContext(logger);
                            ctx.dataProviderName = func.getName();
                            ctx.testNumber = i;
                            return opt(rett.getTupleAt(0))
                                .fap(input -> opt(rett.getTupleAt(1))
                                    .map(output -> ctx.testCase(input, output)));
                        }).fap(v -> v).s
                    ).s
                ).s)
            .els(() -> System.out.println("Failed to find data-providing functions"))
            .def(list());

        logger.logMsg("");
        errors.forEach(logger::logErr);
        logger.logMsg("Done testing with " + errors.size() + " errors and " + logger.sucCnt + " OK-s\n");
        JBPopupFactory.getInstance()
            .createHtmlTextBalloonBuilder("<pre>" + logger.wholeText + "</pre>", MessageType.INFO, null)
            .setFadeoutTime(300 * 1000)
            .createBalloon()
            .show(RelativePoint.fromScreen(new Point(200, 200)), Balloon.Position.atRight);
    }

    private static class CaseContext
    {
        Logger logger;

        String dataProviderName;
        List<String> keyChain = list();
        int testNumber;

        public CaseContext(Logger logger)
        {
            this.logger = logger;
        }

        private List<Error> testCase(MultiType actual, MultiType expected)
            throws AssertionError // in case input does not have some of output keys
        {
            List<Error> errors = list();

            DeepType expectedt = expected.types.get(0);
            L<String> expectedKeyNames = L(expectedt.tupleTypes.values())
                .map(tup -> tup.getStringValue());
            expectedKeyNames.fch((keyName, i) -> {
                if (!actual.hasKey(keyName)) {
                    logger.logErrShort();
                    errors.add(new Error(this, "No such key: '" + keyName + "' at " + i + "-th assertion"));
                } else {
                    logger.logSucShort();
                }
            });
            L<String> extraKeys = Tls.diff(actual.getKeyNames(), expectedKeyNames);
            if (extraKeys.size() > 0) {
                logger.logErrShort();
                errors.add(new Error(this, "Actual output has unexpected keys: " + Tls.implode(", ", extraKeys)));
            }
            return errors;
        }
    }

    private static class Error
    {
        String message;
        String dataProviderName;
        List<String> keyChain;
        int testNumber;

        Error(CaseContext ctx, String msg)
        {
            this.dataProviderName = ctx.dataProviderName;
            this.keyChain = new ArrayList(ctx.keyChain);
            this.testNumber = ctx.testNumber;
            this.message = msg;
        }
    }

    private static class Logger
    {
        String wholeText = "";
        int caret = 0;
        int sucCnt = 0;

        void logMsg(String msg)
        {
            System.out.println(msg);
            wholeText += msg + "\n";
            caret = 0;
        }

        void printWrapped(String text)
        {
            System.out.print(text);
            wholeText += text;
            L<String> lines = L(text.split("/\n/"));
            if (lines.size() > 1) {
                caret = 0;
            }
            caret += lines.lst().unw().length();
            if (caret > 90) {
                logMsg("");
            }
        }

        void logErr(Error err)
        {
            String msg = "Error in " + err.dataProviderName + " #" + err.testNumber + " " +
                L(err.keyChain).rdc((a,b) -> a + ", " + b, "") + " " + err.message;
            logMsg(msg);
        }

        void logErrShort()
        {
            printWrapped("E");
        }

        void logSucShort()
        {
            printWrapped(".");
            ++sucCnt;
        }
    }
}
