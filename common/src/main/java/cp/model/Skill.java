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
public class Skill {
    private static final Logger logger = LoggerFactory.getLogger(Skill.class);

    public enum SkillFieldName {
        SKILLNAME("skill_name", DataType.Name.TEXT),
        SEARCHKEY("search_key", DataType.Name.TEXT),
        USERID("user_id", DataType.Name.UUID);

        private final String columnName;
        private final DataType.Name columnType;

        SkillFieldName(String columnName, DataType.Name columnType) {
            this.columnName = columnName;
            this.columnType = columnType;
        }

        public String columnName() {
            return columnName;
        }

        public DataType.Name columnType() {
            return columnType;
        }

        public static SkillFieldName parse(String nameString) {
            for (SkillFieldName value : values()) {
                if (value.name().equalsIgnoreCase(nameString)) {
                    return value;
                }
            }
            return null;
        }
    }

    public String skillName;
    public String searchKey;
    public UUID userId;

    public Object getValueForDatabase(SkillFieldName field) {
        try {
            switch (field) {
                case SKILLNAME:
                    return skillName;
                case SEARCHKEY:
                    return searchKey;
                case USERID:
                    return userId;
                default:
                    throw new LogicException("Unexpected field type " + field);
            }
        } catch (Exception e) {
            throw new DerivedDataFormatException("Error while retrieving value for " + field.name(), e);
        }
    }

    private void setValue(SkillFieldName field, SmartRow row) {
        try {
            switch (field) {
                case SKILLNAME:
                    this.skillName = row.getString(field.columnName);
                    break;
                case SEARCHKEY:
                    this.searchKey = row.getString(field.columnName);
                    break;
                case USERID:
                    this.userId = row.getUUID(field.columnName);
                    break;
                default:
                    throw new LogicException("Unexpected field type " + field);
            }
        } catch (Exception e) {
            throw new ObservedDataFormatException("Invalid fieldName" + field.columnName, e);

        }
    }

    public static Skill fromRow(Row nakedRow) {
        SmartRow row = new SmartRow(nakedRow);
        logger.debug("Row object: {}", row);
        Skill skill = new Skill();

        for (SkillFieldName field : SkillFieldName.values()) {
            skill.setValue(field, row);
        }
        return skill;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("skillName", skillName)
                .add("searchKey", searchKey)
                .add("userId", userId.toString())
                .toString();
    }
}
