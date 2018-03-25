package cp.services;

import com.beust.jcommander.internal.Lists;
import com.beust.jcommander.internal.Maps;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import cp.common.CommonServiceTestSupport;
import cp.config.DelegatingConfigurationServiceImpl;
import cp.config.PropertiesConfigurationServiceImpl;
import cp.connect.CassandraSessionManager;
import cp.model.*;
import cp.testsupport.CassandraUnitSessionManager;
import cp.testsupport.supportmodel.AbstractCassandraUnit4CQLTestNGCase;
import cp.util.CEDate;
import org.cassandraunit.dataset.CQLDataSet;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.util.*;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static cp.config.ConfigurationKeys.*;
import static org.assertj.core.api.StrictAssertions.assertThat;

/**
 * Unit tests of the UpdateService against CassandraUnit
 */
public class UpdateServiceImplTest extends AbstractCassandraUnit4CQLTestNGCase {
    private static final String UPDATE_SERVICE_TEST_KEYSPACE = "update_service_test";

    private CassandraSessionManager sessionManager;
    private UpdateService updateService;

    @BeforeClass
    public void setUp() throws Exception {
        super.before();
        sessionManager = new CassandraUnitSessionManager(this::getSession);
        Properties localProperties = new Properties();
        localProperties.setProperty(MAIN_KEYSPACE_KEY, UPDATE_SERVICE_TEST_KEYSPACE);
        PropertiesConfigurationServiceImpl localPropsConfigService = new PropertiesConfigurationServiceImpl(localProperties);
        DelegatingConfigurationServiceImpl replacementConfigService =
                DelegatingConfigurationServiceImpl.builder()
                        .add(localPropsConfigService)
                        .add(CommonServiceTestSupport.getConfigService())
                        .build();
        updateService = new UpdateServiceImpl(replacementConfigService, sessionManager);
    }

    @AfterClass
    public void tearDown() throws Exception {
        super.after();
    }

    @Test
    public void testUpdateUser(){
        User user = new User();
        user.userId = UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7");
        user.userName = "first user";
        user.countryCode = 1;
        user.state = "Maryland";
        user.county = "Prince George";
        user.city = "College Park";
        user.zipCode = "20740";
        user.firstName = "Daniel";
        user.lastName = "Lee";
        user.phone = "7089894563";
        user.email = "testUser@gmail.com";
        Set<String> interests = new HashSet<String>();
        interests.add("stock trading");
        interests.add("taxes holdem");
        interests.add("plumber");
        Map<String, String> skills = Maps.newHashMap();
        skills.put("Java", SkillLevel.Medium.name());
        skills.put("Javascript", SkillLevel.Export.name());
        skills.put("Scala", SkillLevel.Biginner.name());
        user.interstes = interests;
        user.skills = skills;

        List<User.UserFieldName> fields = Lists.newLinkedList();
        fields.add(User.UserFieldName.USERNAME);
        fields.add(User.UserFieldName.COUNTY);
        fields.add(User.UserFieldName.CITY);
        fields.add(User.UserFieldName.ZIPCODE);
        fields.add(User.UserFieldName.FIRST_NAME);
        fields.add(User.UserFieldName.LAST_NAME);
        fields.add(User.UserFieldName.PHONE);
        fields.add(User.UserFieldName.EMAIL);
        fields.add(User.UserFieldName.SKILLS);
        fields.add(User.UserFieldName.INTERESTS);

        updateService.updateUser(user, fields);

        Statement statement = QueryBuilder.select()
                .from(Constants.USER_TABLE)
                .where(eq(User.UserFieldName.USERID.columnName(), UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7")));

        ResultSet rs = sessionManager.getSession().execute(statement);
        assertThat(rs.isExhausted()).isFalse();
        Row row = rs.one();

        assertThat(row.getUUID(User.UserFieldName.USERID.columnName())).isEqualTo(UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7"));
        assertThat(row.getString(User.UserFieldName.USERNAME.columnName())).isEqualTo("first user");
        assertThat(row.getInt(User.UserFieldName.COUNTRY_CODE.columnName())).isEqualTo(1);
        assertThat(row.getString(User.UserFieldName.STATE.columnName())).isEqualTo("Maryland");
        assertThat(row.getString(User.UserFieldName.COUNTY.columnName())).isEqualTo("Prince George");
        assertThat(row.getString(User.UserFieldName.CITY.columnName())).isEqualTo("College Park");
        assertThat(row.getString(User.UserFieldName.ZIPCODE.columnName())).isEqualTo("20740");
        assertThat(row.getString(User.UserFieldName.FIRST_NAME.columnName())).isEqualTo("Daniel");
        assertThat(row.getString(User.UserFieldName.LAST_NAME.columnName())).isEqualTo("Lee");
        assertThat(row.getString(User.UserFieldName.PHONE.columnName())).isEqualTo("7089894563");
        assertThat(row.getString(User.UserFieldName.EMAIL.columnName())).isEqualTo("testUser@gmail.com");

        Map<String, String> skills2 = row.getMap("skills", String.class, String.class);
        assertThat(skills2.containsKey("Java")).isTrue();
        assertThat(skills2.containsKey("Javascript")).isTrue();
        assertThat(skills2.containsKey("Scala")).isTrue();

        Set<String> interests2 = row.getSet("interests", String.class);
        assertThat(interests2.contains("stock trading"));
        assertThat(interests2.contains("taxes holdem"));
        assertThat(interests2.contains("plumber"));
    }

    @Test
    public void updateFriendList(){
        updateService.updateFriendList(UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7"), UUID.fromString("6a07917c-b5d3-4b89-88cd-9108e68cfba9"));
        Statement statement = QueryBuilder.select()
                .from(Constants.FRIEND_LIST_TABLE)
                .where(eq("user_id1", UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7")));

        ResultSet rs = sessionManager.getSession().execute(statement);
        assertThat(rs.isExhausted()).isFalse();
        Row row = rs.one();

        assertThat(row.getUUID(0)).isEqualTo(UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7"));
        assertThat(row.getUUID(1)).isEqualTo(UUID.fromString("6a07917c-b5d3-4b89-88cd-9108e68cfba9"));
    }

    @Test
    public void updateMessage(){
        Message message = new Message();
        message.read = false;
        message.messageFrom = UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7");
        message.messageTo = UUID.fromString("6a07917c-b5d3-4b89-88cd-9108e68cfba9");
        message.message = "testing message";
        message.insertTime = CEDate.parse("2015-4-7");

        List<Message.MessageFieldName> fields = Lists.newLinkedList();
        fields.add(Message.MessageFieldName.INSERT_TIME);
        fields.add(Message.MessageFieldName.READ);

        updateService.updateMessage(message, fields);

        Statement statement = QueryBuilder.select()
                .from(Constants.MESSAGE_TABLE)
                .where(eq(Message.MessageFieldName.MESSAGETO.columnName(), UUID.fromString("6a07917c-b5d3-4b89-88cd-9108e68cfba9")))
                .and(eq(Message.MessageFieldName.MESSAGEFROM.columnName(), UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7")));

        ResultSet rs = sessionManager.getSession().execute(statement);
        assertThat(rs.isExhausted()).isFalse();
        Row row = rs.one();

        assertThat(row.getUUID(Message.MessageFieldName.MESSAGEFROM.columnName())).isEqualTo(UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7"));
        assertThat(row.getUUID(Message.MessageFieldName.MESSAGETO.columnName())).isEqualTo(UUID.fromString("6a07917c-b5d3-4b89-88cd-9108e68cfba9"));
        assertThat(row.getString(Message.MessageFieldName.MESSAGE.columnName())).isEqualTo("testing message");
        assertThat(row.getBool(Message.MessageFieldName.READ.columnName())).isEqualTo(false);
    }

    @Test
    public void updateSkillList(){
        updateService.updateSkillList("java", "javascript", UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7"));

        Statement statement = QueryBuilder.select()
                .from(Constants.SKILLS_TABLE)
                .where(eq(Skill.SkillFieldName.SEARCHKEY.columnName(), "java"))
                .and(eq(Skill.SkillFieldName.SKILLNAME.columnName(), "javascript"));

        ResultSet rs = sessionManager.getSession().execute(statement);
        assertThat(rs.isExhausted()).isFalse();

        Skill skill = Skill.fromRow(rs.one());

        assertThat(skill.searchKey).isEqualToIgnoringCase("java");
        assertThat(skill.skillName).isEqualToIgnoringCase("javascript");
    }

    @Test
    public void updatePersonSkill(){
        User user = new User();
        user.userId = UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7");
        user.userName = "first user";
        user.countryCode = 1;
        user.state = "Maryland";
        user.county = "Prince George";
        user.city = "College Park";
        user.zipCode = "20740";
        user.firstName = "Daniel";
        user.lastName = "Lee";
        user.phone = "7089894563";
        user.email = "testUser@gmail.com";
        Set<String> interests = new HashSet<String>();
        interests.add("stock trading");
        interests.add("taxes holdem");
        interests.add("plumber");
        Map<String, String> skills = Maps.newHashMap();
        skills.put("Java", SkillLevel.Medium.name());
        skills.put("Javascript", SkillLevel.Export.name());
        skills.put("Scala", SkillLevel.Biginner.name());
        user.interstes = interests;
        user.skills = skills;

        updateService.updatePersonSkill(user);
        Statement statement = QueryBuilder.select()
                .from(Constants.PERSON_SKILL_TABLE)
                .where(eq(PersonSkill.PersonSkillFieldName.SKILLNAME.columnName(), "Java"))
                .and(eq(User.UserFieldName.STATE.columnName(), user.state))
                .and(eq(User.UserFieldName.COUNTY.columnName(), user.county))
                .and(eq(User.UserFieldName.CITY.columnName(), user.city))
                .and(eq(User.UserFieldName.ZIPCODE.columnName(), user.zipCode))
                .and(eq(User.UserFieldName.USERID.columnName(), user.userId));


        ResultSet rs = sessionManager.getSession().execute(statement);
        assertThat(rs.isExhausted()).isFalse();
        Row row = rs.one();

        assertThat(row.getString(0)).isEqualTo("Java");
        assertThat(row.getString(1)).isEqualTo("Maryland");
        assertThat(row.getString(2)).isEqualTo("Prince George");
        assertThat(row.getString(3)).isEqualTo("College Park");
        assertThat(row.getString(4)).isEqualTo("20740");
        assertThat(row.getUUID(5)).isEqualTo(UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7"));

        updateService.updatePersonSkill(user);
        statement = QueryBuilder.select()
                .from(Constants.PERSON_SKILL_TABLE)
                .where(eq(PersonSkill.PersonSkillFieldName.SKILLNAME.columnName(), "Javascript"))
                .and(eq(User.UserFieldName.STATE.columnName(), user.state))
                .and(eq(User.UserFieldName.COUNTY.columnName(), user.county))
                .and(eq(User.UserFieldName.CITY.columnName(), user.city))
                .and(eq(User.UserFieldName.ZIPCODE.columnName(), user.zipCode))
                .and(eq(User.UserFieldName.USERID.columnName(), user.userId));


        rs = sessionManager.getSession().execute(statement);
        assertThat(rs.isExhausted()).isFalse();
        row = rs.one();

        assertThat(row.getString(0)).isEqualTo("Javascript");
        assertThat(row.getString(1)).isEqualTo("Maryland");
        assertThat(row.getString(2)).isEqualTo("Prince George");
        assertThat(row.getString(3)).isEqualTo("College Park");
        assertThat(row.getString(4)).isEqualTo("20740");
        assertThat(row.getUUID(5)).isEqualTo(UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7"));



        updateService.updatePersonSkill(user);
        statement = QueryBuilder.select()
                .from(Constants.PERSON_SKILL_TABLE)
                .where(eq(PersonSkill.PersonSkillFieldName.SKILLNAME.columnName(), "Scala"))
                .and(eq(User.UserFieldName.STATE.columnName(), user.state))
                .and(eq(User.UserFieldName.COUNTY.columnName(), user.county))
                .and(eq(User.UserFieldName.CITY.columnName(), user.city))
                .and(eq(User.UserFieldName.ZIPCODE.columnName(), user.zipCode))
                .and(eq(User.UserFieldName.USERID.columnName(), user.userId));


        rs = sessionManager.getSession().execute(statement);
        assertThat(rs.isExhausted()).isFalse();
        row = rs.one();

        assertThat(row.getString(0)).isEqualTo("Scala");
        assertThat(row.getString(1)).isEqualTo("Maryland");
        assertThat(row.getString(2)).isEqualTo("Prince George");
        assertThat(row.getString(3)).isEqualTo("College Park");
        assertThat(row.getString(4)).isEqualTo("20740");
        assertThat(row.getUUID(5)).isEqualTo(UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7"));
    }

    @Test
    public void removeSkill(){
        updateService.updateSkillList("java", "javascript", UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7"));
        Statement statement = QueryBuilder.select()
                .from(Constants.SKILLS_TABLE)
                .where(eq(Skill.SkillFieldName.SEARCHKEY.columnName(), "java"))
                .and(eq(Skill.SkillFieldName.SKILLNAME.columnName(), "javascript"));
        ResultSet rs = sessionManager.getSession().execute(statement);
        assertThat(rs.isExhausted()).isFalse();

        updateService.removeSkill("java", "javascript");
        statement = QueryBuilder.select()
                .from(Constants.SKILLS_TABLE)
                .where(eq(Skill.SkillFieldName.SEARCHKEY.columnName(), "java"))
                .and(eq(Skill.SkillFieldName.SKILLNAME.columnName(), "javascript"));
        rs = sessionManager.getSession().execute(statement);
        assertThat(rs.isExhausted()).isTrue();
    }

    @Test
    public void removePersonSkill(){
        User user = new User();
        user.userId = UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7");
        user.userName = "first user";
        user.countryCode = 1;
        user.state = "Maryland";
        user.county = "Prince George";
        user.city = "College Park";
        user.zipCode = "20740";
        user.firstName = "Daniel";
        user.lastName = "Lee";
        user.phone = "7089894563";
        user.email = "testUser@gmail.com";
        Set<String> interests = new HashSet<String>();
        interests.add("stock trading");
        interests.add("taxes holdem");
        interests.add("plumber");
        Map<String, String> skills = Maps.newHashMap();
        skills.put("Java", SkillLevel.Medium.name());
        skills.put("Javascript", SkillLevel.Export.name());
        skills.put("Scala", SkillLevel.Biginner.name());
        user.interstes = interests;
        user.skills = skills;

        updateService.updatePersonSkill(user);
        updateService.removePersonSkill(user);

        Statement statement = QueryBuilder.select()
                .from(Constants.PERSON_SKILL_TABLE)
                .where(eq(PersonSkill.PersonSkillFieldName.SKILLNAME.columnName(), "Java"))
                .and(eq(User.UserFieldName.STATE.columnName(), user.state))
                .and(eq(User.UserFieldName.COUNTY.columnName(), user.county))
                .and(eq(User.UserFieldName.CITY.columnName(), user.city))
                .and(eq(User.UserFieldName.ZIPCODE.columnName(), user.zipCode))
                .and(eq(User.UserFieldName.USERID.columnName(), user.userId));


        ResultSet rs = sessionManager.getSession().execute(statement);
        assertThat(rs.isExhausted()).isTrue();

        statement = QueryBuilder.select()
                .from(Constants.PERSON_SKILL_TABLE)
                .where(eq(PersonSkill.PersonSkillFieldName.SKILLNAME.columnName(), "Javascript"))
                .and(eq(User.UserFieldName.STATE.columnName(), user.state))
                .and(eq(User.UserFieldName.COUNTY.columnName(), user.county))
                .and(eq(User.UserFieldName.CITY.columnName(), user.city))
                .and(eq(User.UserFieldName.ZIPCODE.columnName(), user.zipCode))
                .and(eq(User.UserFieldName.USERID.columnName(), user.userId));


        rs = sessionManager.getSession().execute(statement);
        assertThat(rs.isExhausted()).isTrue();

        statement = QueryBuilder.select()
                .from(Constants.PERSON_SKILL_TABLE)
                .where(eq(PersonSkill.PersonSkillFieldName.SKILLNAME.columnName(), "Scala"))
                .and(eq(User.UserFieldName.STATE.columnName(), user.state))
                .and(eq(User.UserFieldName.COUNTY.columnName(), user.county))
                .and(eq(User.UserFieldName.CITY.columnName(), user.city))
                .and(eq(User.UserFieldName.ZIPCODE.columnName(), user.zipCode))
                .and(eq(User.UserFieldName.USERID.columnName(), user.userId));


        rs = sessionManager.getSession().execute(statement);
        assertThat(rs.isExhausted()).isTrue();
    }

    @Test
    public void updateSkillSearchIndex(){
        updateService.updateSkillSearchIndex("scala", UUID.fromString("3c7c251e-5e8d-4f2b-a1ce-eaf181dfb5a7"));
        Statement statement = QueryBuilder.select()
                .from(Constants.SKILLS_TABLE)
                .where(eq(Skill.SkillFieldName.SEARCHKEY.columnName(), "s"))
                .and(eq(Skill.SkillFieldName.SKILLNAME.columnName(), "scala"));
        List<Row>  rs = sessionManager.getSession().execute(statement).all();
        assertThat(rs.size()).isEqualTo(1);
        rs.clear();

         statement = QueryBuilder.select()
                .from(Constants.SKILLS_TABLE)
                .where(eq(Skill.SkillFieldName.SEARCHKEY.columnName(), "sc"))
                .and(eq(Skill.SkillFieldName.SKILLNAME.columnName(), "scala"));
        rs = sessionManager.getSession().execute(statement).all();
        assertThat(rs.size()).isEqualTo(1);
        rs.clear();

         statement = QueryBuilder.select()
                .from(Constants.SKILLS_TABLE)
                .where(eq(Skill.SkillFieldName.SEARCHKEY.columnName(), "sca"))
                .and(eq(Skill.SkillFieldName.SKILLNAME.columnName(), "scala"));
        rs = sessionManager.getSession().execute(statement).all();
        assertThat(rs.size()).isEqualTo(1);
        rs.clear();

         statement = QueryBuilder.select()
                .from(Constants.SKILLS_TABLE)
                .where(eq(Skill.SkillFieldName.SEARCHKEY.columnName(), "scal"))
                .and(eq(Skill.SkillFieldName.SKILLNAME.columnName(), "scala"));
        rs = sessionManager.getSession().execute(statement).all();
        assertThat(rs.size()).isEqualTo(1);
        rs.clear();

         statement = QueryBuilder.select()
                .from(Constants.SKILLS_TABLE)
                .where(eq(Skill.SkillFieldName.SEARCHKEY.columnName(), "scala"))
                .and(eq(Skill.SkillFieldName.SKILLNAME.columnName(), "scala"));
        rs = sessionManager.getSession().execute(statement).all();
        assertThat(rs.size()).isEqualTo(1);
    }

    @Override
    public CQLDataSet getDataSet() {
        return CassandraTestSupport.generateCQLMainEnvironment(UPDATE_SERVICE_TEST_KEYSPACE, "update-test-data.cql");
    }

}