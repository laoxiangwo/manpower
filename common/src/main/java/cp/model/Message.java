package cp.model;

import cp.exceptions.DerivedDataFormatException;
import cp.exceptions.LogicException;
import cp.exceptions.ObservedDataFormatException;
import cp.util.CEDate;
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
public class Message {
    private static final Logger logger = LoggerFactory.getLogger(Message.class);

    public enum MessageFieldName {
        MESSAGEFROM("message_from", DataType.Name.BIGINT),
        MESSAGETO("message_to", DataType.Name.BIGINT),
        MESSAGE("message", DataType.Name.TEXT),
        INSERT_TIME("insert_time", DataType.Name.TEXT),
        READ("read", DataType.Name.BOOLEAN);

        private final String columnName;
        private final DataType.Name columnType;

        MessageFieldName(String columnName, DataType.Name columnType) {
            this.columnName = columnName;
            this.columnType = columnType;
        }

        public String columnName() {
            return columnName;
        }

        public DataType.Name columnType() {
            return columnType;
        }

        public static MessageFieldName parse(String nameString) {
            for (MessageFieldName value : values()) {
                if (value.name().equalsIgnoreCase(nameString)) {
                    return value;
                }
            }
            return null;
        }
    }

    public UUID messageFrom;
    public UUID messageTo;
    public String message;
    public CEDate insertTime;
    public Boolean read;

    public Object getValueForDatabase(MessageFieldName field) {
        try {
            switch (field) {
                case MESSAGE:
                    return message;
                case MESSAGEFROM:
                    return messageFrom;
                case MESSAGETO:
                    return messageTo;
                case INSERT_TIME:
                    return insertTime.toString();
                case READ:
                    return read;
                default:
                    throw new LogicException("Unexpected field type " + field);
            }
        } catch (Exception e) {
            throw new DerivedDataFormatException("Error while retrieving value for " + field.name(), e);
        }
    }

    private void setValue(MessageFieldName field, SmartRow row) {
        try {
            switch (field) {
                case MESSAGE:
                    this.message = row.getString(field.columnName);
                    break;
                case MESSAGEFROM:
                    this.messageFrom = row.getUUID(field.columnName);
                    break;
                case MESSAGETO:
                    this.messageTo = row.getUUID(field.columnName);
                    break;
                case INSERT_TIME:
                    this.insertTime = CEDate.parse(row.getString(field.columnName));
                    break;
                case READ:
                    this.read = row.getBool(field.columnName);
                    break;
                default:
                    throw new LogicException("Unexpected field type " + field);
            }
        } catch (Exception e) {
            throw new ObservedDataFormatException("Invalid fieldName" + field.columnName, e);

        }
    }

    public static Message fromRow(Row nakedRow) {
        SmartRow row = new SmartRow(nakedRow);
        logger.debug("Row object: {}", row);
        Message message = new Message();

        for (MessageFieldName field : MessageFieldName.values()) {
            message.setValue(field, row);
        }
        return message;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("messageFrom", messageFrom)
                .add("messageTo", messageTo)
                .add("message", message)
                .add("insertTime", insertTime.toString())
                .add("read", read)
                .toString();
    }
}
