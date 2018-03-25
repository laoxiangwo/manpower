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

import java.util.UUID;

/**
 * Created by shengli on 2/22/16.
 */
public class PersonSkill {
    private static final Logger logger = LoggerFactory.getLogger(PersonSkill.class);

    public enum PersonSkillFieldName {
        SKILLNAME("skill_name", DataType.Name.TEXT),
        STATE("state", DataType.Name.TEXT),
        COUNTY("county", DataType.Name.TEXT),
        CITY("city", DataType.Name.TEXT),
        ZIPCODE("zipcode", DataType.Name.TEXT),
        USERID("user_id", DataType.Name.UUID),
        UPDATE_TIME("update_time", DataType.Name.TEXT);

        private final String columnName;
        private final DataType.Name columnType;

        PersonSkillFieldName(String columnName, DataType.Name columnType) {
            this.columnName = columnName;
            this.columnType = columnType;
        }

        public String columnName() {
            return columnName;
        }

        public DataType.Name columnType() {
            return columnType;
        }

        public static PersonSkillFieldName parse(String nameString) {
            for (PersonSkillFieldName value : values()) {
                if (value.name().equalsIgnoreCase(nameString)) {
                    return value;
                }
            }
            return null;
        }
    }

    public UUID userId;
    public String skillName;
    public String state;
    public String county;
    public String city;
    public String zipCode;
    public String updateTime;

    public Object getValueForDatabase(PersonSkillFieldName field) {
        try {
            switch (field) {
                case SKILLNAME:
                    return skillName;
                case USERID:
                    return userId;
                case STATE:
                    return state;
                case COUNTY:
                    return county;
                case CITY:
                    return city;
                case ZIPCODE:
                    return zipCode;
                case UPDATE_TIME:
                    return updateTime;

                default:
                    throw new LogicException("Unexpected field type " + field);
            }
        } catch (Exception e) {
            throw new DerivedDataFormatException("Error while retrieving value for " + field.name(), e);
        }
    }

    private void setValue(PersonSkillFieldName field, SmartRow row) {
        try {
            switch (field) {
                case SKILLNAME:
                    this.skillName = row.getString(field.columnName);
                    break;
                case USERID:
                    this.userId = row.getUUID(field.columnName);
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
                case UPDATE_TIME:
                    this.updateTime = row.getString(field.columnName);

                default:
                    throw new LogicException("Unexpected field type " + field);
            }
        } catch (Exception e) {
            throw new ObservedDataFormatException("Invalid fieldName" + field.columnName, e);

        }
    }

    public static PersonSkill fromRow(Row nakedRow) {
        SmartRow row = new SmartRow(nakedRow);
        logger.debug("Row object: {}", row);
        PersonSkill personSkill = new PersonSkill();

        for (PersonSkillFieldName field : PersonSkillFieldName.values()) {
            personSkill.setValue(field, row);
        }
        return personSkill;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
           .omitNullValues()
           .add("skillName", skillName)
           .add("userId", userId.toString())
           .add("state", state)
           .add("county", county)
           .add("city", city)
           .add("zipcode", zipCode)
           .add("updateTime", updateTime)
           .toString();
    }
}
