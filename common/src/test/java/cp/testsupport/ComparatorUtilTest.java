package cp.testsupport;

import com.beust.jcommander.internal.Lists;
import cp.model.Message;
import cp.util.CEDate;
import cp.util.ComparatorUtil;
import org.testng.annotations.Test;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 * Created by shengli on 3/12/16.
 */
public class ComparatorUtilTest {
    @Test
    public void testCEDateComparator() {
        CEDate left = CEDate.parse("2015-4-7");
        CEDate right = CEDate.parse("2014-4-7");
        List<CEDate> list = Lists.newLinkedList();
        list.add(left);
        list.add(right);

        // the natural order of comparator is smaller iterm comes first
        // so earlier dates come first
        Collections.sort(list, ComparatorUtil.CEDATE_COMPARATOR);
        assertThat(list.get(0)).isEqualTo(right);
        assertThat(list.get(1)).isEqualTo(left);
    }

    @Test
    public void testBooleanComparator(){
        List<Boolean> list = Lists.newLinkedList();
        list.add(true);
        list.add(false);

        // the natural order of comparator is smaller iterm comes first
        // so false  come first
        Collections.sort(list, ComparatorUtil.BOOLEAN_COMPARATOR);
        assertThat(list.get(0)).isEqualTo(false);
        assertThat(list.get(1)).isEqualTo(true);
    }

    @Test
    public void testMessageComparator(){
        {
            Message message = new Message();
            message.insertTime = CEDate.parse("2015-4-7");
            message.message = "message";
            message.read = false;

            Message message1 = new Message();
            message1.insertTime = CEDate.parse("2014-4-7");
            message1.message = "message1";
            message1.read = false;

            List<Message> list = Lists.newArrayList();
            list.add(message1);
            list.add(message);

            assertThat(list.get(0).message).isEqualTo("message1");
            assertThat(list.get(1).message).isEqualTo("message");

            Collections.sort(list, ComparatorUtil.MOST_RECENT_MESSAGE_COMPARATOR);

            assertThat(list.get(0).message).isEqualTo("message");
            assertThat(list.get(1).message).isEqualTo("message1");
        }

        {
            Message message = new Message();
            message.insertTime = CEDate.parse("2015-4-7");
            message.message = "message";
            message.read = false;

            Message message1 = new Message();
            message1.insertTime = CEDate.parse("2015-4-7");
            message1.message = "message1";
            message1.read = true;

            List<Message> list = Lists.newArrayList();
            list.add(message1);
            list.add(message);

            assertThat(list.get(0).message).isEqualTo("message1");
            assertThat(list.get(1).message).isEqualTo("message");

            Collections.sort(list, ComparatorUtil.MOST_RECENT_MESSAGE_COMPARATOR);

            assertThat(list.get(0).message).isEqualTo("message");
            assertThat(list.get(1).message).isEqualTo("message1");
        }

    }
}

