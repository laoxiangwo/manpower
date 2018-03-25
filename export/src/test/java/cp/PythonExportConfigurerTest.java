package cp;

import cp.config.ConfigurationService;
import cp.config.PropertiesConfigurationServiceImpl;
import export.PythonExportConfigurer;
import export.cli.ConfigurationException;
import export.model.ExportSpecification;
import export.model.generic.ConstantColumnExpression;
import export.model.generic.IdentityColumnExpression;
import export.model.python.PythonColumnExpression;
import export.model.python.PythonEngine;
import org.assertj.core.api.Assertions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Properties;

import static org.assertj.core.api.StrictAssertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.assertj.core.api.StrictAssertions.assertThat;

public class PythonExportConfigurerTest {
    private ConfigurationService configService;
    private Properties configProperties;

    @BeforeClass
    public void setup() {
        configProperties = new Properties();
        configService = new PropertiesConfigurationServiceImpl(configProperties);
    }

    @Test
    public void testReadExportSpec() throws Exception {

    }

    @Test
    public void testConfigureInputColumns() throws Exception {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(0, "col_1");
        jsonArray.put(1, "col_2");
        jsonArray.put(2, "col_3");
        jsonArray.put(3, "col_4");
        jsonArray.put(4, "col_5");
        jsonObject.put(PythonExportConfigurer.JSON_INPUT, jsonArray);

        ExportSpecification exportSpec = new ExportSpecification();
        PythonExportConfigurer.configureInputColumns(jsonObject, exportSpec);

        Assertions.assertThat(exportSpec.columns())
                .hasSize(5)
                .containsExactly("col_1", "col_2", "col_3", "col_4", "col_5");

        JSONObject jsonObject2 = new JSONObject();
        assertThatThrownBy(() -> PythonExportConfigurer.configureInputColumns(jsonObject2, exportSpec))
                .isInstanceOf(ConfigurationException.class)
                .hasMessageContaining("Config file missing required attribute");
    }

    @Test
    public void testConfigureOutputColumns() throws Exception {
        PythonEngine pythonEngine = mock(PythonEngine.class);
        {
            // constant expression
            JSONObject jsonDoc = new JSONObject();
            JSONArray jsonExprArray = new JSONArray();
            JSONObject jsonExpr1 = new JSONObject();
            jsonExpr1.put(PythonExportConfigurer.JSON_LABEL, "myCol");
            jsonExpr1.put(PythonExportConfigurer.JSON_VALUE, "123.456");
            jsonExprArray.put(0, jsonExpr1);
            jsonDoc.put(PythonExportConfigurer.JSON_OUTPUT, jsonExprArray);
            ExportSpecification exportSpec = new ExportSpecification();
            PythonExportConfigurer.configureOutputColumns(jsonDoc, exportSpec, configService, pythonEngine);
            Assertions.assertThat(exportSpec.outputExpressions())
                    .extracting("class", "label", "columnNumber", "constantValue")
                    .containsOnly(Assertions.tuple(ConstantColumnExpression.class, "myCol", 0, "123.456"));

        }

        {
            // identifier expression
            JSONObject jsonDoc = new JSONObject();
            JSONArray jsonExprArray = new JSONArray();
            JSONObject jsonExpr1 = new JSONObject();
            jsonExpr1.put(PythonExportConfigurer.JSON_LABEL, "myCol");
            jsonExpr1.put(PythonExportConfigurer.JSON_VALUE, "abc123");
            jsonExprArray.put(0, jsonExpr1);
            jsonDoc.put(PythonExportConfigurer.JSON_OUTPUT, jsonExprArray);
            ExportSpecification exportSpec = new ExportSpecification();
            PythonExportConfigurer.configureOutputColumns(jsonDoc, exportSpec, configService, pythonEngine);
            Assertions.assertThat(exportSpec.outputExpressions())
                    .hasSize(1)
                    .extracting("class")
                    .containsOnly(IdentityColumnExpression.class);
        }

        {
            // python expression
            JSONObject jsonDoc = new JSONObject();
            JSONArray jsonExprArray = new JSONArray();
            JSONObject jsonExpr1 = new JSONObject();
            jsonExpr1.put(PythonExportConfigurer.JSON_LABEL, "myCol");
            jsonExpr1.put(PythonExportConfigurer.JSON_VALUE, "toUpper(abc123)");
            jsonExprArray.put(0, jsonExpr1);
            jsonDoc.put(PythonExportConfigurer.JSON_OUTPUT, jsonExprArray);
            ExportSpecification exportSpec = new ExportSpecification();
            PythonExportConfigurer.configureOutputColumns(jsonDoc, exportSpec, configService, pythonEngine);
            Assertions.assertThat(exportSpec.outputExpressions())
                    .hasSize(1)
                    .extracting("class")
                    .containsOnly(PythonColumnExpression.class);
        }

        {
            // all 3
            JSONObject jsonDoc = new JSONObject();
            JSONArray jsonExprArray = new JSONArray();

            JSONObject jsonExpr1 = new JSONObject();
            jsonExpr1.put(PythonExportConfigurer.JSON_LABEL, "myCol1");
            jsonExpr1.put(PythonExportConfigurer.JSON_VALUE, "123.456");
            jsonExprArray.put(0, jsonExpr1);

            JSONObject jsonExpr2 = new JSONObject();
            jsonExpr2.put(PythonExportConfigurer.JSON_LABEL, "myCol2");
            jsonExpr2.put(PythonExportConfigurer.JSON_VALUE, "abc123");
            jsonExprArray.put(1, jsonExpr2);

            JSONObject jsonExpr3 = new JSONObject();
            jsonExpr3.put(PythonExportConfigurer.JSON_LABEL, "myCol3");
            jsonExpr3.put(PythonExportConfigurer.JSON_VALUE, "toUpper(abc123)");
            jsonExprArray.put(2, jsonExpr3);

            jsonDoc.put(PythonExportConfigurer.JSON_OUTPUT, jsonExprArray);
            ExportSpecification exportSpec = new ExportSpecification();
            PythonExportConfigurer.configureOutputColumns(jsonDoc, exportSpec, configService, pythonEngine);
            Assertions.assertThat(exportSpec.outputExpressions())
                    .hasSize(3)
                    .extracting("class", "label", "columnNumber")
                    .containsOnly(Assertions.tuple(ConstantColumnExpression.class, "myCol1", 0),
                            Assertions.tuple(IdentityColumnExpression.class, "myCol2", 1),
                            Assertions.tuple(PythonColumnExpression.class, "myCol3", 2));

        }

    }

    @Test
    public void testGetOptionalJsonBooleanAttribute() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("bvar1", true);

        // value in JSON attr
        assertThat(PythonExportConfigurer.getOptionalJsonBooleanAttribute(jsonObject, "bvar1", false, "", configService))
                .isTrue();

        // value in default
        assertThat(PythonExportConfigurer.getOptionalJsonBooleanAttribute(jsonObject, "bvar2", false, "", configService))
                .isFalse();

        // value in config service
        configProperties.setProperty("bpkey", "true");
        assertThat(PythonExportConfigurer.getOptionalJsonBooleanAttribute(jsonObject, "bvar3", false, "bpkey", configService))
                .isTrue();

    }

    @Test
    public void testGetOptionalJsonStringAttribute() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ovar1", "xyz");

        // value in JSON attr
        assertThat(PythonExportConfigurer.getOptionalJsonStringAttribute(jsonObject, "ovar1", "pdq", "", configService))
                .isEqualTo("xyz");

        // value in default
        assertThat(PythonExportConfigurer.getOptionalJsonStringAttribute(jsonObject, "ovar2", "pdq", "", configService))
                .isEqualTo("pdq");

        // value in config sevice
        configProperties.setProperty("opkey", "abc");
        assertThat(PythonExportConfigurer.getOptionalJsonStringAttribute(jsonObject, "ovar3", "pdq", "opkey", configService))
                .isEqualTo("abc");

    }

    @Test
    public void testGetRequiredJsonStringAttribute() throws Exception {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rvar1", "xyz");

        // value in JSON attr
        assertThat(PythonExportConfigurer.getRequiredJsonStringAttribute(jsonObject, "rvar1", "", configService))
                .isEqualTo("xyz");

        // missing value
        assertThatThrownBy(() -> { PythonExportConfigurer.getRequiredJsonStringAttribute(jsonObject, "rvar2", "", configService); })
                .isInstanceOf(ConfigurationException.class)
                .hasMessageContaining("Config file missing required attribute \"rvar2\"");

        // value in config sevice
        configProperties.setProperty("rpkey", "abc");
        assertThat(PythonExportConfigurer.getRequiredJsonStringAttribute(jsonObject, "ovar3",  "rpkey", configService))
                .isEqualTo("abc");

    }
}