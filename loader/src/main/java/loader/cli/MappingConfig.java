package loader.cli;

import cp.util.CaseInsensitiveSet;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;
import java.util.TreeMap;
import static cp.config.ConfigurationKeys.*;
public class MappingConfig {
    private Map<String, String> headerToColumnMap;

    private Map<String, String> syntheticToColumnMap;

    private CaseInsensitiveSet timestampColumns;

    private CaseInsensitiveSet lineNumberColumns;

    private CaseInsensitiveSet headersToIgnore;

    private CaseInsensitiveSet uuidColumns;

    private String table;

    private String useStrictMapping;

    public MappingConfig() {
        headerToColumnMap = new TreeMap<>();
        syntheticToColumnMap = new TreeMap<>();
        timestampColumns = new CaseInsensitiveSet();
        lineNumberColumns = new CaseInsensitiveSet();
        uuidColumns = new CaseInsensitiveSet();
        headersToIgnore = new CaseInsensitiveSet();
    }

    public Map<String, String> getHeaderToColumnMap() {
        return headerToColumnMap;
    }

    public Map<String, String> getSyntheticToColumnMap() {
        return syntheticToColumnMap;
    }

    public CaseInsensitiveSet getTimestampColumns() {
        return timestampColumns;
    }

    public CaseInsensitiveSet getLineNumberColumns() {
        return lineNumberColumns;
    }

    public CaseInsensitiveSet getUuidColumns(){return uuidColumns; }

    public CaseInsensitiveSet getHeadersToIgnore() {
        return headersToIgnore;
    }

    public String getTable() {
        return table;
    }

    public String isUseStrictMapping() {
        return useStrictMapping;
    }

    public MappingConfig setTable(String table) {
        this.table = table;
        return this;
    }

    public MappingConfig setUseStrictMapping(String useStrictMapping) {
        this.useStrictMapping = useStrictMapping;
        return this;
    }

    public static MappingConfig fromJSON(JSONObject jsonObject) {
        return new MappingConfig().loadFromJSON(jsonObject);
    }

    public MappingConfig loadFromJSON(JSONObject json) {
        headerToColumnMap.clear();
        syntheticToColumnMap.clear();
        JSONObject columnMapJson = json.optJSONObject(MAPPED_KEY);
        if (columnMapJson != null) {
            for (Object keyObj : columnMapJson.keySet()) {
                try {
                    if (headerToColumnMap.containsKey((String)keyObj))
                        throw new CLILoaderRuntimeException("Mapping config file contains same header targeting multiple columns. Column key: " + keyObj);

                    if (headerToColumnMap.containsValue(columnMapJson.getString((String) keyObj)))
                        throw new CLILoaderRuntimeException("Mapping config file contains multiple headers targeting same column. Column: " + columnMapJson.getString((String) keyObj));

                    headerToColumnMap.put((String)keyObj, columnMapJson.getString((String)keyObj));
                } catch (JSONException e) {
                    throw new CLILoaderRuntimeException("Mapping config header and column names must be strings in JSON. Key: " +
                            keyObj, e);
                }
            }
        }
        JSONObject syntheticMapJson = json.optJSONObject(SYNTHETICS_KEY);
        if (syntheticMapJson != null) {
            for (Object keyObj : syntheticMapJson.keySet()) {
                try {
                    String key = keyObj.toString().toLowerCase();
                    if (syntheticToColumnMap.containsKey(key))
                        throw new CLILoaderRuntimeException("Mapping config file contains multiple synthetics targeting same column. Column key: " + key);

                    syntheticToColumnMap.put(key, syntheticMapJson.getString((String)keyObj));
                } catch (JSONException e) {
                    throw new CLILoaderRuntimeException("Mapping config values and column names must be strings in JSON. Key: " +
                            keyObj, e);
                }
            }
        }
        JSONArray timestampJson = json.optJSONArray(TIMESTAMPS_KEY);
        if (timestampJson != null) {
            for (int i = 0; i < timestampJson.length(); i++) {
                String column = timestampJson.getString(i);

                if (headerToColumnMap.containsValue(column))
                    throw new CLILoaderRuntimeException("Column " + column + " already mapped to a header");

                if (syntheticToColumnMap.containsKey(column))
                    throw new CLILoaderRuntimeException("Column " + column + " already has synthetic associated with it");

                timestampColumns.add(column);
            }
        }

        JSONArray uuidJson = json.optJSONArray(UUID_KEYS);
        if (uuidJson != null) {
            for (int i = 0; i < uuidJson.length(); i++) {
                String column = uuidJson.getString(i);

                if (headerToColumnMap.containsValue(column))
                    throw new CLILoaderRuntimeException("Column " + column + " already mapped to a header");

                if (syntheticToColumnMap.containsKey(column))
                    throw new CLILoaderRuntimeException("Column " + column + " already has synthetic associated with it");

                uuidColumns.add(column);
            }
        }

        JSONArray lineNumberJson = json.optJSONArray(LINENUMBERS_KEY);
        if (lineNumberJson != null) {
            for (int i = 0; i < lineNumberJson.length(); i++) {
                String column = lineNumberJson.getString(i);

                if (headerToColumnMap.containsValue(column))
                    throw new CLILoaderRuntimeException("Column " + column + " already mapped to a header");

                if (syntheticToColumnMap.containsKey(column))
                    throw new CLILoaderRuntimeException("Column " + column + " already has synthetic associated with it");

                if (timestampColumns.contains(column))
                    throw new CLILoaderRuntimeException("Column " + column + " already has timestamp associated with it");

                lineNumberColumns.add(column);
            }
        }
        JSONArray ignoreHeadersJson = json.optJSONArray(IGNORED_KEY);
        if (ignoreHeadersJson != null) {
            for (int i = 0; i < ignoreHeadersJson.length(); i++) {
                String header = ignoreHeadersJson.getString(i);
                headersToIgnore.add(header);
            }
        }
        try {
            table = json.getString("table");
        } catch (JSONException e) {
            throw new CLILoaderRuntimeException("Mapping config must include \"table\" string attribute in JSON");
        }
        return this;
    }

    /**
     * Adds to the synthetics map extra synthetics that were passed as a command line argument.
     * @param synthetics - comma delimited string of column=value pairs
     *                   Eg: "AGE=21,GENDER=UNKNOWN,VOTER_STATUS=UNKNOWN"
     */
    public MappingConfig addSynthetics(String synthetics) {
        Lists.newArrayList(Splitter.on(',').trimResults().split(synthetics))
                .stream()
                .forEach(s -> {
                    String[] pair = Iterables.toArray(Splitter.on('=').trimResults().split(s), String.class);
                    if (pair.length != 2) {
                        throw new CLILoaderRuntimeException(String.format("Synthetic syntax incorrect: " +
                                "expected \"{column}={value}\", got \"%s\" instead", s));
                    }
                    if (syntheticToColumnMap.containsKey(pair[0].toLowerCase()))
                        throw new CLILoaderRuntimeException("Command line mapping contains synthetics targeting column " +
                                "already mapped either in the mapping config file or command line synthetics. Column key: " + pair[0].toLowerCase());
                    syntheticToColumnMap.put(pair[0].toLowerCase(), pair[1]);
                });
        return this;
    }

    /**
     * Adds to the timestamps set extra timestamp that were passed as a command line argument.
     * @param timestamps - comma delimited string of columns to be filled with timestamps
     *                   Eg: "DATE_ADDED,DATE_UPDATED"
     */
    public MappingConfig addTimestamps(String timestamps) {
        Lists.newArrayList(Splitter.on(',').trimResults().split(timestamps))
                .stream()
                .forEach(s -> {
                    if (headerToColumnMap.containsValue(s))
                        throw new CLILoaderRuntimeException("Command line mapping contains timestamp targeting column " +
                                "already mapped in the mapping config file. Column key: " + s);
                    if (syntheticToColumnMap.containsKey(s))
                        throw new CLILoaderRuntimeException("Command line mapping contains timestamp targeting column " +
                                "already mapped to a synthetic in either the config file or a command line argument. Column key: " + s);
                    timestampColumns.add(s);
                });
        return this;
    }

    /**
     * Adds to the timestamps set extra timestamp that were passed as a command line argument.
     * @param linenumbers - comma delimited string of columns to be filled with timestamps
     *                   Eg: "DATE_ADDED,DATE_UPDATED"
     */
    public MappingConfig addLineNumbers(String linenumbers) {
        Lists.newArrayList(Splitter.on(',').trimResults().split(linenumbers))
                .stream()
                .forEach(s -> {
                    if (headerToColumnMap.containsValue(s))
                        throw new CLILoaderRuntimeException("Command line mapping contains linenumber targeting column " +
                                "already mapped in the mapping config file. Column key: " + s);
                    if (syntheticToColumnMap.containsKey(s))
                        throw new CLILoaderRuntimeException("Command line mapping contains linenumber targeting column " +
                                "already mapped to a synthetic in either the config file or a command line argument. Column key: " + s);
                    if (timestampColumns.contains(s))
                        throw new CLILoaderRuntimeException("Command line mapping contains linenumber targeting column " +
                                "already mapped to a timestamp in either the config file or a command line argument. Column key: " + s);
                    lineNumberColumns.add(s);
                });
        return this;
    }

    public MappingConfig addUUIDs(String uuids) {
        Lists.newArrayList(Splitter.on(',').trimResults().split(uuids))
           .stream()
           .forEach(s -> {
               if (headerToColumnMap.containsValue(s))
                   throw new CLILoaderRuntimeException("Command line mapping contains linenumber targeting column " +
                      "already mapped in the mapping config file. Column key: " + s);
               if (syntheticToColumnMap.containsKey(s))
                   throw new CLILoaderRuntimeException("Command line mapping contains linenumber targeting column " +
                      "already mapped to a synthetic in either the config file or a command line argument. Column key: " + s);
               if (timestampColumns.contains(s))
                   throw new CLILoaderRuntimeException("Command line mapping contains linenumber targeting column " +
                      "already mapped to a timestamp in either the config file or a command line argument. Column key: " + s);
               uuidColumns.add(s);
           });
        return this;
    }
}
