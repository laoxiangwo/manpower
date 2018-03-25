package cp.util;

import com.google.common.collect.ComparisonChain;
import cp.model.Friend;
import cp.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public class ComparatorUtil {
    protected static final Logger logger = LoggerFactory.getLogger(ComparatorUtil.class);

    private ComparatorUtil() {
        throw new UnsupportedOperationException();
    }

    //if left is after right, consider left is bigger than right
    public static final Comparator<CEDate> CEDATE_COMPARATOR = (left, right) ->{
        if (left == null) {
            return right == null ? 0 : -1;
        }
        if (right == null) {
            return 1;
        }
        return left.isBefore(right) ? -1 : left.isAfter(right) ? 1 : 0;
    };

    public static final Comparator<Boolean> BOOLEAN_COMPARATOR = (left, right) ->{
        if (left == null) {
            return right == null ? 0 : -1;
        }
        if (right == null) {
            return 1;
        }
        return left.compareTo(right);
    };

    public static final Comparator<Message> MOST_RECENT_MESSAGE_COMPARATOR = (left, right) -> {
        if (left == null) {
            return right == null ? 0 : 1;
        }
        if (right == null) {
            return -1;
        }

        return ComparisonChain.start()
                .compare(left.insertTime, right.insertTime, CEDATE_COMPARATOR.reversed())
                .compare(left.read, right.read, BOOLEAN_COMPARATOR)
                .result();
    };

    public static final Comparator<Friend> MOST_RECENT_FRIEND_COMPARATOR = (left, right) -> {
        if (left == null) {
            return right == null ? 0 : 1;
        }
        if (right == null) {
            return -1;
        }

        return ComparisonChain.start()
                .compare(left.friendSince, right.friendSince, CEDATE_COMPARATOR.reversed())
                .result();
    };

}
