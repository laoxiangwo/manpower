package cp.model;

import cp.exceptions.DerivedDataFormatException;
import cp.exceptions.LogicException;
import cp.exceptions.ObservedDataFormatException;
import cp.util.SmartRow;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.Row;
import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by shengli on 2/20/16.
 */
public class User {
    private static final Logger logger = LoggerFactory.getLogger(User.class);

    public enum UserFieldName {
        USERID("user_id", DataType.Name.UUID),
        USERNAME("user_name", DataType.Name.TEXT),
        COUNTRY_CODE("country_code", DataType.Name.INT),
        STATE("state", DataType.Name.TEXT),
        COUNTY("county", DataType.Name.TEXT),
        CITY("city", DataType.Name.TEXT),
        ZIPCODE("zipcode", DataType.Name.TEXT),
        FIRST_NAME("first_name", DataType.Name.TEXT),
        MIDDLE_NAME("middle_name", DataType.Name.TEXT),
        LAST_NAME("last_name", DataType.Name.TEXT),
        PHONE("phone", DataType.Name.TEXT),
        EMAIL("email", DataType.Name.TEXT),
        SKILLS("skills", DataType.Name.MAP),
        INTERESTS("interests", DataType.Name.SET);

        private final String columnName;
        private final DataType.Name columnType;

        UserFieldName(String columnName, DataType.Name columnType) {
            this.columnName = columnName;
            this.columnType = columnType;
        }

        public String columnName() {
            return columnName;
        }

        public DataType.Name columnType() {
            return columnType;
        }

        public static UserFieldName parse(String nameString) {
            for (UserFieldName value : values()) {
                if (value.name().equalsIgnoreCase(nameString)) {
                    return value;
                }
            }
            return null;
        }
    }

    public UUID userId;
    public Integer countryCode;
    public String userName;
    public String state;
    public String county;
    public String city;
    public String zipCode;
    public String firstName;
    public String middleName;
    public String lastName;
    public String phone;
    public String email;
    public Map<String, String> skills;
    public Set<String> interstes;

    public Object getValueForDatabase(UserFieldName field) {
        try {
            switch (field) {
                case USERID:
                    return userId;
                case USERNAME:
                    return userName;
                case COUNTRY_CODE:
                    return countryCode;
                case STATE:
                    return state;
                case CITY:
                    return city;
                case ZIPCODE:
                    return zipCode;
                case FIRST_NAME:
                    return firstName;
                case MIDDLE_NAME:
                    return middleName;
                case LAST_NAME:
                    return lastName;
                case PHONE:
                    return phone;
                case EMAIL:
                    return email;
                case SKILLS:
                    return skills;
                case INTERESTS:
                    return interstes;
                case COUNTY:
                    return county;

                default:
                    throw new LogicException("Unexpected field type " + field);
            }
        } catch (Exception e) {
            throw new DerivedDataFormatException("Error while retrieving value for " + field.name() +
                    " for user with id {} and name {}: " + userId + userName, e);
        }
    }

    private void setValue(UserFieldName field, SmartRow row) {
        try {
            switch (field) {
                case USERID:
                    this.userId = row.getUUID(field.columnName);
                    break;
                case USERNAME:
                    this.userName = row.getString(field.columnName);
                    break;
                case COUNTRY_CODE:
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
                case FIRST_NAME:
                    this.firstName = row.getString(field.columnName);
                    break;
                case MIDDLE_NAME:
                    this.middleName = row.getString(field.columnName);
                    break;
                case LAST_NAME:
                    this.lastName = row.getString(field.columnName);
                    break;
                case PHONE:
                    this.phone = row.getString(field.columnName);
                    break;
                case EMAIL:
                    this.email = row.getString(field.columnName);
                    break;
                case SKILLS:
                    this.skills = row.getMap(field.columnName, String.class, String.class);
                    break;
                case INTERESTS:
                    this.interstes = row.getSet(field.columnName, String.class);
                    break;
                default:
                    throw new LogicException("Unexpected field type " + field);
            }
        } catch (Exception e) {
            throw new ObservedDataFormatException("Invalid fieldName" + field.columnName +
                    MoreObjects.toStringHelper(this).omitNullValues()
                            .add("userId", userId)
                            .add("userName", userName), e);

        }
    }

    public static User fromRow(Row nakedRow) {
        SmartRow row = new SmartRow(nakedRow);
        logger.debug("Row object: {}", row);
        User pdf = new User();

        for (UserFieldName field : UserFieldName.values()) {
            pdf.setValue(field, row);
        }
        return pdf;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("userId", userId.toString())
                .add("user_name", userName)
                .add("country_code", countryCode)
                .add("state", state)
                .add("county", county)
                .add("city", city)
                .add("zipcode", zipCode)
                .add("first_name", firstName)
                .add("middle_name", middleName)
                .add("last_name", lastName)
                .add("phone", phone)
                .add("email", email)
                .add("skills", skills.toString())
                .add("observations", interstes == null ? null : "{" + (Joiner.on(", ").useForNull("null").join(interstes)) + "}")
                .toString();
    }
}
