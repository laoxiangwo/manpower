package cp.model;

import cp.exceptions.DerivedDataFormatException;
import cp.exceptions.LogicException;
import cp.exceptions.ObservedDataFormatException;
import cp.util.SmartRow;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.Row;
import com.google.common.base.MoreObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by shengli on 2/22/16.
 */
public class Location {
    private static final Logger logger = LoggerFactory.getLogger(Location.class);

    public enum LocationFieldName {
        COUNTRYCODE("country_code", DataType.Name.BIGINT),
        COUNTRYNAME("country_name", DataType.Name.TEXT),
        STATE("state", DataType.Name.TEXT),
        COUNTY("county", DataType.Name.TEXT),
        CITY("city", DataType.Name.TEXT),
        ZIPCODE("zipcode", DataType.Name.TEXT);

        private final String columnName;
        private final DataType.Name columnType;

        LocationFieldName(String columnName, DataType.Name columnType) {
            this.columnName = columnName;
            this.columnType = columnType;
        }

        public String columnName() {
            return columnName;
        }

        public DataType.Name columnType() {
            return columnType;
        }

        public static LocationFieldName parse(String nameString) {
            for (LocationFieldName value : values()) {
                if (value.name().equalsIgnoreCase(nameString)) {
                    return value;
                }
            }
            return null;
        }
    }

    public Integer countryCode;
    public String countryNmae;
    public String state;
    public String county;
    public String city;
    public String zipCode;

    public Object getValueForDatabase(LocationFieldName field) {
        try {
            switch (field) {
                case COUNTRYNAME:
                    return countryNmae;
                case COUNTRYCODE:
                    return countryCode;
                case STATE:
                    return state;
                case CITY:
                    return city;
                case ZIPCODE:
                    return zipCode;

                default:
                    throw new LogicException("Unexpected field type " + field);
            }
        } catch (Exception e) {
            throw new DerivedDataFormatException("Error while retrieving value for " + field.name(), e);
        }
    }

    private void setValue(LocationFieldName field, SmartRow row) {
        try {
            switch (field) {
                case COUNTRYNAME:
                    this.countryNmae = row.getString(field.columnName);
                    break;
                case COUNTRYCODE:
                    this.countryCode = row.getInt(field.columnName);
                    break;
                case STATE:
                    this.state = row.getString(field.columnName);
                    break;
                case COUNTY:
                    this.county = row.getString(field.columnName);
                    break;
                case CITY:
                    this.city = row.getString(field.columnName);
                    break;
                case ZIPCODE:
                    this.zipCode = row.getString(field.columnName);
                    break;

                default:
                    throw new LogicException("Unexpected field type " + field);
            }
        } catch (Exception e) {
            throw new ObservedDataFormatException("Invalid fieldName" + field.columnName, e);

        }
    }

    public static Location fromRow(Row nakedRow) {
        SmartRow row = new SmartRow(nakedRow);
        logger.debug("Row object: {}", row);
        Location location = new Location();

        for (LocationFieldName field : LocationFieldName.values()) {
            location.setValue(field, row);
        }
        return location;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
           .omitNullValues()
           .add("countryName", countryNmae)
           .add("countryCode", countryCode)
           .add("state", state)
           .add("county", county)
           .add("city", city)
           .add("zipcode", zipCode)
           .toString();
    }

}
