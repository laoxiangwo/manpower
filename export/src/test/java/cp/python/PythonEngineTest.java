package cp.python;

import export.model.python.PythonEngine;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class PythonEngineTest {

    @Test
    public void testConstructBindingStatement() throws Exception {
        // null
        assertThat(PythonEngine.constructBindingStatement("val", null)).isEqualTo("val = None");

        // strings
        assertThat(PythonEngine.constructBindingStatement("val", "")).isEqualTo("val = ''");
        assertThat(PythonEngine.constructBindingStatement("val", "ABC123")).isEqualTo("val = 'ABC123'");

        // booleans
        assertThat(PythonEngine.constructBindingStatement("val", Boolean.FALSE)).isEqualTo("val = False");
        assertThat(PythonEngine.constructBindingStatement("val", Boolean.TRUE)).isEqualTo("val = True");

        // integers
        assertThat(PythonEngine.constructBindingStatement("val", -12345678)).isEqualTo("val = -12345678");
        assertThat(PythonEngine.constructBindingStatement("val", -1)).isEqualTo("val = -1");
        assertThat(PythonEngine.constructBindingStatement("val", 0)).isEqualTo("val = 0");
        assertThat(PythonEngine.constructBindingStatement("val", 1)).isEqualTo("val = 1");
        assertThat(PythonEngine.constructBindingStatement("val", 12345678)).isEqualTo("val = 12345678");

        // longs
        assertThat(PythonEngine.constructBindingStatement("val", -12345678901L)).isEqualTo("val = -12345678901");
        assertThat(PythonEngine.constructBindingStatement("val", -1L)).isEqualTo("val = -1");
        assertThat(PythonEngine.constructBindingStatement("val", 0L)).isEqualTo("val = 0");
        assertThat(PythonEngine.constructBindingStatement("val", 1L)).isEqualTo("val = 1");
        assertThat(PythonEngine.constructBindingStatement("val", 12345678901L)).isEqualTo("val = 12345678901");

        // floats
        assertThat(PythonEngine.constructBindingStatement("val", -1234.567f)).isEqualTo("val = -1234.567");
        assertThat(PythonEngine.constructBindingStatement("val", -0.1f)).isEqualTo("val = -0.1");
        assertThat(PythonEngine.constructBindingStatement("val", 0.0f)).isEqualTo("val = 0.0");
        assertThat(PythonEngine.constructBindingStatement("val", 0.1f)).isEqualTo("val = 0.1");
        assertThat(PythonEngine.constructBindingStatement("val", 1234.567f)).isEqualTo("val = 1234.567");

        // doubles
        assertThat(PythonEngine.constructBindingStatement("val", -1234.5678d)).isEqualTo("val = -1234.5678");
        assertThat(PythonEngine.constructBindingStatement("val", -0.1d)).isEqualTo("val = -0.1");
        assertThat(PythonEngine.constructBindingStatement("val", 0.0d)).isEqualTo("val = 0.0");
        assertThat(PythonEngine.constructBindingStatement("val", 0.1d)).isEqualTo("val = 0.1");
        assertThat(PythonEngine.constructBindingStatement("val", 1234.5678d)).isEqualTo("val = 1234.5678");

        // bigintegers
        assertThat(PythonEngine.constructBindingStatement("val", new BigInteger("-12345678901"))).isEqualTo("val = -12345678901");
        assertThat(PythonEngine.constructBindingStatement("val", new BigInteger("-1"))).isEqualTo("val = -1");
        assertThat(PythonEngine.constructBindingStatement("val", new BigInteger("0"))).isEqualTo("val = 0");
        assertThat(PythonEngine.constructBindingStatement("val", new BigInteger("1"))).isEqualTo("val = 1");
        assertThat(PythonEngine.constructBindingStatement("val", new BigInteger("12345678901"))).isEqualTo("val = 12345678901");

        // bigdecimals
        assertThat(PythonEngine.constructBindingStatement("val", new BigDecimal("-1234.5678"))).isEqualTo("val = -1234.5678");
        assertThat(PythonEngine.constructBindingStatement("val", new BigDecimal("-0.1"))).isEqualTo("val = -0.1");
        assertThat(PythonEngine.constructBindingStatement("val", new BigDecimal("0.0"))).isEqualTo("val = 0.0");
        assertThat(PythonEngine.constructBindingStatement("val", new BigDecimal("0.1"))).isEqualTo("val = 0.1");
        assertThat(PythonEngine.constructBindingStatement("val", new BigDecimal("1234.5678"))).isEqualTo("val = 1234.5678");
    }

    @Test
    public void testEncodePythonStringLiteral() throws Exception {
        // simple cases
        assertThat(PythonEngine.encodePythonStringLiteral("")).isEqualTo("''");
        assertThat(PythonEngine.encodePythonStringLiteral(" ")).isEqualTo("' '");
        assertThat(PythonEngine.encodePythonStringLiteral("ABC123")).isEqualTo("'ABC123'");

        // handling of single and double quotes inside literals
        assertThat(PythonEngine.encodePythonStringLiteral("ABC'123")).isEqualTo("\"ABC'123\"");
        assertThat(PythonEngine.encodePythonStringLiteral("ABC\"123")).isEqualTo("'ABC\"123'");
        assertThat(PythonEngine.encodePythonStringLiteral("ABC\"'123")).isEqualTo("'ABC\"\\'123'");

        // handling of unicode characters
        assertThat(PythonEngine.encodePythonStringLiteral("ABC\u2014123")).isEqualTo("'ABC\\u2014123'");
        assertThat(PythonEngine.encodePythonStringLiteral("ABC\u1F63C123")).isEqualTo("'ABC\\u1f63C123'");
        assertThat(PythonEngine.encodePythonStringLiteral("ABC\u1f63C123")).isEqualTo("'ABC\\u1f63C123'");
    }
}