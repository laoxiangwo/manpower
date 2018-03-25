package cp.util;

import cp.config.ConfigurationService;
import cp.config.PropertiesConfigurationServiceImpl;
import cp.exceptions.ConfigurationException;
import com.google.common.base.Joiner;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class CommandLinePropertiesUtil {

    public static String ALL_CMD_LINE_ARGUMENTS = "command-line.arguments";

    public static String CMD_LINE_NONOPTION_PARAMETER = "command-line.parameter.";

    public static String CMD_LINE_NONOPTION_COUNT = "command-line.parameter-count";

    public static Properties setOptionsAsProperties(String args[]){
        Properties properties = new Properties();
        StringBuilder allValues = new StringBuilder();
        int nonOptionsCount = 0;
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                //Add all args in double quotes seperated by comma
                allValues.append("\"").append(args[i]).append("\"");
                if(i < args.length -1){
                    allValues.append(",");
                }
                if (args[i].startsWith("--")) {
                    if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                        properties.setProperty(args[i].substring(2), args[i + 1]);
                        allValues.append("\"").append(args[i + 1]).append("\"");
                        i++;

                        if (i < args.length -1) {
                            allValues.append(",");
                        }

                    } else {
                        throw new ConfigurationException("Invalid command line argument. Option "+ args[i] +" does not have a value. Every option passed in should have a value. Eg: --<option> <value>.");
                    }
                } else {
                    //If its not an option set it as a property command-line.parameter<>
                    nonOptionsCount++;
                    properties.setProperty(CMD_LINE_NONOPTION_PARAMETER +nonOptionsCount, args[i]);
                }
            }
        }

        properties.setProperty(ALL_CMD_LINE_ARGUMENTS, allValues.toString());
        properties.setProperty(CMD_LINE_NONOPTION_COUNT, String.valueOf(nonOptionsCount));

        return properties;
    }

    /**
     * Given a configuration service and one or more property names, confirm
     * that the names are present and have non-null values.
     *
     * @param configService The configuration service to use
     * @param propertyNames The property names to search for
     * @throws ConfigurationException if one or more of the properties are missing or have null values
     */
    public static void checkRequiredProperties(ConfigurationService configService, String... propertyNames) {
        List<String> missingProperties = null;
        for (String propertyName : propertyNames) {
            if (configService.getValue(propertyName) == null)  {
                if (missingProperties == null) {
                    missingProperties = new LinkedList<>();
                }
                missingProperties.add(propertyName);
            }
        }
        if (missingProperties != null && missingProperties.size() > 0)  {
            throw new ConfigurationException(String.format("%s required propert%s missing: %s",
                    missingProperties.size(),
                    missingProperties.size() == 1 ? "y is" : "ies are",
                    Joiner.on(", ").join(missingProperties)));
        }
    }

    public static PropertiesConfigurationServiceImpl getPropertiesFromFile(String configFile) throws IOException {
        return getPropertiesFromReader(new FileReader(configFile));
    }

    public static PropertiesConfigurationServiceImpl getPropertiesFromReader(Reader in) throws IOException {
        Properties configProperties = new Properties();
        configProperties.load(in);
        return getConfigurationServiceFromProperties(configProperties);
    }

    public static PropertiesConfigurationServiceImpl getConfigurationServiceFromProperties(Properties configProperties) {
        return PropertiesConfigurationServiceImpl.fromProperties(configProperties);
    }
}
