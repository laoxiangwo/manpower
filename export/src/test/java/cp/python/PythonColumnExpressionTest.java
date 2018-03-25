package cp.python;

import export.model.generic.MapExpressionContext;
import export.model.python.PythonColumnExpression;
import export.model.python.PythonEngine;
import org.python.util.PythonInterpreter;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test that column expressions evaluate correctly in Python
 *
 * Created by shengli on 12/30/15.
 */
public class PythonColumnExpressionTest {

    private PythonEngine pythonEngine;
    private MapExpressionContext exprContext;

    @BeforeClass
    public void setup() {
        System.setProperty("python.import.site", "false");
        pythonEngine = new PythonEngine(new PythonInterpreter());
        Map<String, String> contextMap = new HashMap<>();
        contextMap.put("n", null);
        contextMap.put("x", "3");
        contextMap.put("y", "ABC");
        contextMap.put("z", "DEF");
        contextMap.put("w", "102.12");
        contextMap.put("v", "-34.2");
        exprContext = new MapExpressionContext(contextMap);
    }

    @Test
    public void testEvaluate() throws Exception {
        assertEvaluationOutput("None", null);
        assertEvaluationOutput("2 + 1", "3");
        assertEvaluationOutput("x + 1", "4");
        assertEvaluationOutput("'3' + y", "3ABC");
        assertEvaluationOutput("str(x) + y", "3ABC");
        assertEvaluationOutput("y + z", "ABCDEF");
        assertEvaluationOutput("w + v", "67.92");
        assertEvaluationOutput("w * v", "-3492.5040000000004");
        assertEvaluationOutput("v * w", "-3492.5040000000004");
        assertEvaluationOutput("w * 2", "204.24");
        assertEvaluationOutput("abs(v)", "34.2");
        assertEvaluationOutput("bin(127)", "0b1111111");
        assertEvaluationOutput("bool(1)", "1");
        assertEvaluationOutput("bool(2)", "1");
        assertEvaluationOutput("bool(0)", "0");
        assertEvaluationOutput("divmod(97, 3)[1]", "1");
        assertEvaluationOutput("eval('x + 1')", "4");
        assertEvaluationOutput("hash(y)", "64578");
        assertEvaluationOutput("hex(129)", "0x81");
        assertEvaluationOutput("len(y)", "3");
        assertEvaluationOutput("min(y)", "A");
        assertEvaluationOutput("max(z)", "F");

    }

    private void assertEvaluationOutput(String pythonExpression, String expectedValue) {
        PythonColumnExpression pce = new PythonColumnExpression()
                .setLabel("MyLabel")
                .setColumnNumber(1)
                .setPyEngine(pythonEngine)
                .setExpression(pythonExpression);
        assertThat(pce.evaluate(exprContext)).as(pythonExpression).isEqualTo(expectedValue);

    }
}