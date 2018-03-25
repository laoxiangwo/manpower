package export;

import cp.config.ConfigurationService;
import export.cli.ConfigurationException;
import export.model.ColumnExpression;
import export.model.ExportSpecification;
import export.model.generic.ConstantColumnExpression;
import export.model.generic.IdentityColumnExpression;
import export.model.python.PythonColumnExpression;
import export.model.python.PythonEngine;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static cp.config.ConfigurationKeys.*;

/**
 * Given a Reader providing a JSON document export specification,
 * convert it to an export specification object.
 *
 * Conversion here is specific to Python column expressions.
 *
 * Created by shengli on 12/29/15.
 */
public class PythonExportConfigurer {
    private static final Logger log = LoggerFactory.getLogger(PythonExportConfigurer.class);
    
    public static final String JSON_KEYSPACE = "keyspace";
    public static final String JSON_TABLE = "table";
    public static final String JSON_PREDICATE = "predicate";
    public static final String JSON_ALLOW_FILTERING = "allowFiltering";
    public static final String JSON_INPUT = "input";
    public static final String JSON_OUTPUT = "output";
    public static final String JSON_LABEL = "label";
    public static final String JSON_VALUE = "value";
    public static final String JSON_OMIT_HEADER = "omitHeader";

    public static final String IDENTIFIER_REGEXP = "^([A-Za-z_][A-Za-z0-9_]*)$";
    public static final Pattern IDENTIFIER_PATTERN = Pattern.compile(IDENTIFIER_REGEXP);

    public static final String CONSTANT_REGEXP = "^[-]?[0-9]*(\\.[0-9]+)?$";
    public static final Pattern CONSTANT_PATTERN = Pattern.compile(CONSTANT_REGEXP);
    public static final String NIL_PROPERTY_KEY = "";

    public static ExportSpecification readExportSpec(Reader configFileReader,
                                                     ConfigurationService configService,
                                                     PythonEngine pythonEngine) {
        JSONObject jsonDoc = new JSONObject(new JSONTokener(configFileReader));

        /*
            Example JSON format:

            {
                "keyspace": "my_keyspace",
                "table": "my_table",
                "predicate": "key = value AND other_key = other_value",
                "allowFiltering" : true,
                "omitHeader": true, // this is an optional attribute with default value false;
                "input" : [
                    "column_1", "column_2", "column_3"
                ],
                "output" : [
                    {
                        "label": "COL_1",
                        "value": "column_1"
                    },
                    {
                        "label": "COL+2",
                        "value": "UPPER(column2)"
                    },
                    {
                        "label": "COL3",
                        "value": "Round(column1) + Round(column3) / 10"
                    },
                    {
                        "label": "COL4",
                        "value": "'alwaysthisvalue'"
                    },
                ]
            }
         */
        ExportSpecification exportSpec = new ExportSpecification();
        exportSpec.setKeyspace(getRequiredJsonStringAttribute(jsonDoc, JSON_KEYSPACE, EXPORT_KEYSPACE_KEY, configService));
        exportSpec.setTable(getRequiredJsonStringAttribute(jsonDoc, JSON_TABLE, EXPORT_TABLE_NAME_KEY, configService));
        exportSpec.setPredicate(getOptionalJsonStringAttribute(jsonDoc, JSON_PREDICATE, null, EXPORT_PREDICATE_KEY,
           configService));
        exportSpec.setAllowFiltering(getOptionalJsonBooleanAttribute(jsonDoc, JSON_ALLOW_FILTERING, false,
           EXPORT_ALLOW_FILTERING_KEY, configService));
        exportSpec.setLimit(getOptionalJsonLongAttribute(jsonDoc, "limit", null, "limit", configService));
        exportSpec.setOmitHeader(getOptionalJsonBooleanAttribute(jsonDoc, JSON_OMIT_HEADER, false, EXPORT_OMIT_HEADER, configService));

        // get input columns
        configureInputColumns(jsonDoc, exportSpec);

        // get output columns
        configureOutputColumns(jsonDoc, exportSpec, configService, pythonEngine);

        log.debug("Parsed export spec: {}", exportSpec);
        return exportSpec;
    }

    public static void configureInputColumns(JSONObject jsonDoc, ExportSpecification exportSpec) {
        if (!jsonDoc.has(JSON_INPUT)) {
            throw new ConfigurationException(String.format("Config file missing required attribute \"%s\" column array", JSON_INPUT));
        }
        JSONArray inputColumnArray = null;
        try {
            inputColumnArray = jsonDoc.getJSONArray(JSON_INPUT);
        } catch (JSONException e) {
            throw new ConfigurationException(String.format("Config file attribute \"%s\" is not a valid JSON array", JSON_INPUT));
        }
        List<String> columnNames = new ArrayList<>(inputColumnArray.length());
        // why don't they make this stupid thing iterable?
        for (int i = 0; i < inputColumnArray.length(); i++) {
            String columnName = null;
            try {
                columnName = inputColumnArray.getString(i);
            } catch (JSONException e) {
                throw new ConfigurationException(
                        String.format("Config file attribute \"%s\" has invalid column name at index %d: %s",
                                JSON_INPUT, i, String.valueOf(inputColumnArray.get(i))));
            }
            columnNames.add(columnName);
        }
        exportSpec.setColumns(columnNames);
    }

    public static void configureOutputColumns(JSONObject jsonDoc,
                                              ExportSpecification exportSpec,
                                              ConfigurationService configService,
                                              PythonEngine pythonEngine) {
        if (!jsonDoc.has(JSON_OUTPUT)) {
            throw new ConfigurationException(String.format("Config file missing required attribute \"%s\" column array", JSON_OUTPUT));
        }
        JSONArray outputExprArray = null;
        try {
            outputExprArray = jsonDoc.getJSONArray(JSON_OUTPUT);
        } catch (JSONException e) {
            throw new ConfigurationException(String.format("Config file attribute \"%s\" is not a valid JSON array", JSON_OUTPUT));
        }
        List<ColumnExpression> columnExpressions = new ArrayList<>(outputExprArray.length());
        // why don't they make this stupid thing iterable?
        Set<String> labelsSeen = new HashSet<>(columnExpressions.size());
        for (int i = 0; i < outputExprArray.length(); i++) {
            JSONObject jsonExpressionObj = outputExprArray.getJSONObject(i);
            try {
                jsonExpressionObj = outputExprArray.getJSONObject(i);
            } catch (JSONException e) {
                throw new ConfigurationException(
                        String.format("Config file attribute \"%s\" has invalid json object at index %d: %s",
                                JSON_OUTPUT, i, String.valueOf(outputExprArray.get(i))));
            }
            String label = getOptionalJsonStringAttribute(jsonExpressionObj, JSON_LABEL, null, NIL_PROPERTY_KEY, configService);
            String value = getRequiredJsonStringAttribute(jsonExpressionObj, JSON_VALUE, NIL_PROPERTY_KEY, configService);

            boolean valueIsIdentifier = IDENTIFIER_PATTERN.matcher(value).matches();
            boolean valueIsConstant = CONSTANT_PATTERN.matcher(value).matches();
            if (label == null) {
                if (valueIsIdentifier) {
                    label = value.toUpperCase();
                } else {
                    label = "COL_" + i;
                }
                log.debug("Generated synthetic label for column {}: {}", i, value);
            }
            if (labelsSeen.contains(label)) {
                log.warn("Output column label {} appears more than once. Repeat is at column {}.", label, i);
            }
            labelsSeen.add(label);
            ColumnExpression colExpr;
            if (valueIsIdentifier) {
                log.debug("Column expression typed as identifier: {}", value);
                colExpr = new IdentityColumnExpression(label, value, i);
            } else if (valueIsConstant) {
                log.debug("Column expression typed as constant: {}", value);
                colExpr = new ConstantColumnExpression(label, value, i);
            } else {
                log.debug("Column expression typed as python expression: {}", value);
                // full python expression treatment
                colExpr = new PythonColumnExpression()
                        .setColumnNumber(i).setExpression(value).setLabel(label).setPyEngine(pythonEngine);
            }

            columnExpressions.add(colExpr);
        }
        exportSpec.setOutputExpressions(columnExpressions);

    }

    public static boolean getOptionalJsonBooleanAttribute(JSONObject jsonDoc,
                                                          String attribute,
                                                          boolean defaultValue,
                                                          String propertyKey,
                                                          ConfigurationService configService) {

        // check the config service first
        final Boolean propertyValue = configService.getBooleanValue(propertyKey);
        if (propertyValue != null) {
            return propertyValue;
        }
        // check the json doc
        if (jsonDoc.has(attribute)) {
            try {
                return jsonDoc.getBoolean(attribute);
            } catch (JSONException e) {
                throw new ConfigurationException("Config file has invalid value for attribute \"" + attribute + "\": " + jsonDoc.get(attribute));
            }
        }
        return defaultValue;
    }

    public static Long getOptionalJsonLongAttribute(JSONObject jsonDoc,
                                                          String attribute,
                                                          Long defaultValue,
                                                          String propertyKey,
                                                          ConfigurationService configService) {
        // check the config service first
        final Long propertyValue = configService.getLongValue(propertyKey);
        if (propertyValue != null) {
            return propertyValue;
        }
        // check the json doc
        if (jsonDoc.has(attribute)) {
            try {
                return jsonDoc.getLong(attribute);
            } catch (JSONException e) {
                throw new ConfigurationException("Config file has invalid value for attribute \"" + attribute + "\": " + jsonDoc.get(attribute));
            }
        }
        return defaultValue;
    }

    public static String getOptionalJsonStringAttribute(JSONObject jsonDoc,
                                                        String attribute,
                                                        String defaultValue,
                                                        String propertyKey,
                                                        ConfigurationService configService) {
        // check the config service first
        final String propertyValue = configService.getValue(propertyKey);
        if (propertyValue != null) {
            return propertyValue;
        }
        // check the json doc
        if (jsonDoc.has(attribute)) {
            try {
                return jsonDoc.getString(attribute);
            } catch (JSONException e) {
                throw new ConfigurationException("Config file has invalid value for attribute \"" + attribute + "\": " + jsonDoc.get(attribute));
            }
        }
        return defaultValue;
    }

    public static String getRequiredJsonStringAttribute(JSONObject jsonDoc,
                                                        String attribute,
                                                        String propertyKey,
                                                        ConfigurationService configService) {
        // check the config service first
        final String propertyValue = configService.getValue(propertyKey);
        if (propertyValue != null) {
            return propertyValue;
        }
        // check the json doc
        if (jsonDoc.has(attribute)) {
            return jsonDoc.getString(attribute);
        } else {
            throw new ConfigurationException("Config file missing required attribute \"" + attribute + "\"");
        }
    }
}
