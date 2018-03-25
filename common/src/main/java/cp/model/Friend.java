package cp.model;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.Row;
import com.google.common.base.MoreObjects;
import cp.exceptions.DerivedDataFormatException;
import cp.exceptions.LogicException;
import cp.exceptions.ObservedDataFormatException;
import cp.util.CEDate;
import cp.util.SmartRow;

import java.util.UUID;

/**
 * Created by shengli on 3/12/16.
 */
public class Friend {
    public enum FriendFieldName {
        USERID1("user_id1", DataType.Name.UUID),
        USERID2("user_id2", DataType.Name.UUID),
        FRINDSINCE("update_time", DataType.Name.TEXT);

        private final String columnName;
        private final DataType.Name columnType;

        FriendFieldName(String columnName, DataType.Name columnType) {
            this.columnName = columnName;
            this.columnType = columnType;
        }

        public String columnName() {
            return columnName;
        }

        public DataType.Name columnType() {
            return columnType;
        }

        public static FriendFieldName parse(String nameString) {
            for (FriendFieldName value : values()) {
                if (value.name().equalsIgnoreCase(nameString)) {
                    return value;
                }
            }
            return null;
        }
    }

    public UUID userId1;
    public UUID userId2;
    public CEDate friendSince;

    public Object getValueForDatabase(FriendFieldName field) {
        try {
            switch (field) {
                case USERID1:
                    return userId1;
                case USERID2:
                    return userId2;
                case FRINDSINCE:
                    return friendSince;
                default:
                    throw new LogicException("Unexpected field type " + field);
            }
        } catch (Exception e) {
            throw new DerivedDataFormatException("Error while retrieving value for " + field.name(), e);
        }
    }

    private void setValue(FriendFieldName field, SmartRow row) {
        try {
            switch (field) {
                case USERID1:
                    this.userId1 = row.getUUID(field.columnName);
                    break;
                case USERID2:
                    this.userId2 = row.getUUID(field.columnName);
                    break;
                case FRINDSINCE:
                    this.friendSince = CEDate.parse(row.getString(field.columnName));
                    break;
                default:
                    throw new LogicException("Unexpected field type " + field);
            }
        } catch (Exception e) {
            throw new ObservedDataFormatException("Invalid fieldName" + field.columnName, e);

        }
    }

    public static Friend fromRow(Row nakedRow) {
        SmartRow row = new SmartRow(nakedRow);
        Friend friend = new Friend();

        for (FriendFieldName field : FriendFieldName.values()) {
            friend.setValue(field, row);
        }
        return friend;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("userId1", userId1.toString())
                .add("userId2", userId2.toString())
                .add("friendSince", friendSince.toString())
                .toString();
    }
}
