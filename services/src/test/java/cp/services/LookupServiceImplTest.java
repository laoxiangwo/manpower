package cp.services;

import cp.connect.CassandraSessionManager;
import cp.common.CommonServiceTestSupport;
import cp.model.Friend;
import cp.model.Message;
import cp.model.User;
import cp.testsupport.CassandraUnitSessionManager;
import cp.testsupport.supportmodel.AbstractCassandraUnit4CQLTestNGCase;
import org.cassandraunit.dataset.CQLDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static cp.config.ConfigurationKeys.*;
import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 * CassandraUnit testing of the LookupServiceImpl
 * <p>
 * Created by shengli on 10/16/15.
 */
public class LookupServiceImplTest extends AbstractCassandraUnit4CQLTestNGCase {

    private static final Logger log = LoggerFactory.getLogger(LookupServiceImplTest.class);

    public static final String KEYSPACE = "lookup_service_test";

    private LookupService lookupService;

    @BeforeClass
    public void setUp() throws Exception {
        CommonServiceTestSupport.CONFIG_PROPERTIES_SUPPLIER.get().setProperty(MAIN_KEYSPACE_KEY, KEYSPACE);
        CommonServiceTestSupport.CONFIG_PROPERTIES_SUPPLIER.get().setProperty(REFERENCE_KEYSPACE_KEY, KEYSPACE);
        super.before();
        CassandraSessionManager sessionManager = new CassandraUnitSessionManager(this::getSession);
        lookupService = new LookupServiceImpl(sessionManager, CommonServiceTestSupport.getConfigService());
    }

    @AfterClass
    public void tearDown() throws Exception {
        super.after();
    }

    @Test
    public void getUserIds() {
        {
            List<UUID> userList = lookupService.getUserIds("java", null, null, null, null);
            assertThat(userList).isNotNull();
            assertThat(userList.size()).isEqualTo(1);
            assertThat(userList.get(0)).isEqualTo(UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7"));
        }

        {
            List<UUID> userList = lookupService.getUserIds("java", "Maryland", null, null, null);
            assertThat(userList).isNotNull();
            assertThat(userList.size()).isEqualTo(1);
            assertThat(userList.get(0)).isEqualTo(UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7"));
        }

        {
            List<UUID> userList = lookupService.getUserIds("java", "Maryland", "Prince George", null, null);
            assertThat(userList).isNotNull();
            assertThat(userList.size()).isEqualTo(1);
            assertThat(userList.get(0)).isEqualTo(UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7"));
        }

        {
            List<UUID> userList = lookupService.getUserIds("java", "Maryland", "Prince George", "College Park", null);
            assertThat(userList).isNotNull();
            assertThat(userList.size()).isEqualTo(1);
            assertThat(userList.get(0)).isEqualTo(UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7"));
        }

        {
            List<UUID> userList = lookupService.getUserIds("java", "Maryland", "Prince George", "College Park", "20740");
            assertThat(userList).isNotNull();
            assertThat(userList.size()).isEqualTo(1);
            assertThat(userList.get(0)).isEqualTo(UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7"));
        }

    }

    @Test
    public void getSkills() {
        List<String> skillList = lookupService.getSkills("jav");
        assertThat(skillList).isNotNull();
        assertThat(skillList.size()).isEqualTo(2);
        assertThat(skillList.contains("java")).isTrue();
        assertThat(skillList.contains("javascript")).isTrue();
    }

    @Test
    public void getFriends() {
        List<Friend> friendList = lookupService.getFriendList(UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7"));
        assertThat(friendList).isNotNull();
        assertThat(friendList.size()).isEqualTo(2);
        assertThat(friendList.get(0).userId2).isEqualTo(UUID.fromString("6a07917c-b5d3-4b89-88cd-9108e68cfba9"));
        assertThat(friendList.get(1).userId2).isEqualTo(UUID.fromString("1a76e2ee-7aec-40ed-a4b6-53cf580c2f48"));
    }

    @Test
    public void getUser() {
        User user = lookupService.getUser(UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7"));
        assertThat(user).isNotNull();
        assertThat(user.userName).isEqualTo("first user");
        assertThat(user.state).isEqualTo("Maryland");
        assertThat(user.countryCode).isEqualTo(1);
        assertThat(user.county).isEqualTo("Prince George");
        assertThat(user.city).isEqualTo("College Park");
        assertThat(user.zipCode).isEqualTo("20740");
        assertThat(user.userId).isEqualTo(UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7"));
        assertThat(user.interstes.size()).isEqualTo(3);

        assertThat(user.interstes.contains("plumber")).isTrue();
        assertThat(user.interstes.contains("taxes holdem")).isTrue();
        assertThat(user.interstes.contains("stock trading")).isTrue();

        assertThat(user.skills.size()).isEqualTo(3);
        assertThat(user.skills.containsKey("Java")).isTrue();
        assertThat(user.skills.containsKey("Javascript")).isTrue();
        assertThat(user.skills.containsKey("Scala")).isTrue();
        assertThat(user.skills.get("Java")).isEqualToIgnoringCase("medium");
        assertThat(user.skills.get("Javascript")).isEqualToIgnoringCase("export");
        assertThat(user.skills.get("Scala")).isEqualToIgnoringCase("beginner");
    }

    @Test
    public void getMessages() {
        List<Message> messageList = lookupService.getMessages(UUID.fromString("6a07917c-b5d3-4b89-88cd-9108e68cfba9"));
        assertThat(messageList).isNotNull();
        assertThat(messageList.size()).isEqualTo(2);
        assertThat(messageList.get(0).message).isEqualTo("do you have to team up?");
        assertThat(messageList.get(1).message).isEqualTo("I have sth you may interested");
    }

    @Test(enabled = false)
    public void getGeoLocations() {

    }

    @Test
    public void getCountryCode() {
        int countryCode = lookupService.getCountryCode("United States");
        assertThat(countryCode).isEqualTo(1);
    }

    @Test
    public void getUserIdsBlurSearch() {
        {
            List<UUID> userList = lookupService.getUserIdsBlurSearch("j", null, null, null, null);
            assertThat(userList).isNotNull();
            assertThat(userList.size()).isEqualTo(2);
            assertThat(userList.contains(UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7")));
            assertThat(userList.contains(UUID.fromString("6a07917c-b5d3-4b89-88cd-9108e68cfba9")));
        }

        {
            List<UUID> userList = lookupService.getUserIdsBlurSearch("ho", null, null, null, null);
            assertThat(userList).isNotNull();
            assertThat(userList.size()).isEqualTo(1);
            assertThat(userList.contains(UUID.fromString("6a07917c-b5d3-4b89-88cd-9108e68cfba9")));
        }

    }

    @Override
    public CQLDataSet getDataSet() {
        return CassandraTestSupport.generateCQLMainEnvironment(KEYSPACE, "lookup-test-data.cql");
    }


}