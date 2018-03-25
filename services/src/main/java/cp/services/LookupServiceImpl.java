package cp.services;

import cp.config.ConfigurationKeys;
import cp.config.ConfigurationService;
import cp.connect.CassandraSessionManager;
import cp.model.*;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import cp.util.ComparatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static cp.model.Constants.*;
import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;
import static com.datastax.driver.core.querybuilder.QueryBuilder.select;

/**
 * Basic implementation against CassandraSessionManager
 */
public class LookupServiceImpl implements LookupService {
    private static final Logger logger = LoggerFactory.getLogger(LookupServiceImpl.class);

    private final CassandraSessionManager sessionManager;
    private final String mainKeyspace;
    private final String referenceKeyspace;

    public LookupServiceImpl(CassandraSessionManager sessionManager, ConfigurationService configService) {
        this.sessionManager = sessionManager;
        this.mainKeyspace = configService.getValue(ConfigurationKeys.MAIN_KEYSPACE_KEY);
        this.referenceKeyspace = configService.getValue(ConfigurationKeys.REFERENCE_KEYSPACE_KEY);
    }

    private Session session() {
        return sessionManager.getSession();
    }


    @Override
    public List<UUID> getUserIds(String skill, @Nullable String state, @Nullable String county, @Nullable String
            city, @Nullable String zipcode) {
        List<UUID> userIds = Lists.newLinkedList();
        Statement statement = select(User.UserFieldName.USERID.columnName())
                .from(mainKeyspace, PERSON_SKILL_TABLE)
                .where(eq(PersonSkill.PersonSkillFieldName.SKILLNAME.columnName(), skill));
        if (!Strings.isNullOrEmpty(state)) {
            statement = select(User.UserFieldName.USERID.columnName())
                    .from(mainKeyspace, PERSON_SKILL_TABLE)
                    .where(eq(PersonSkill.PersonSkillFieldName.SKILLNAME.columnName(), skill))
                    .and(eq(PersonSkill.PersonSkillFieldName.STATE.columnName(), state));
            if (!Strings.isNullOrEmpty(county)) {
                statement = select(User.UserFieldName.USERID.columnName())
                        .from(mainKeyspace, PERSON_SKILL_TABLE)
                        .where(eq(PersonSkill.PersonSkillFieldName.SKILLNAME.columnName(), skill))
                        .and(eq(PersonSkill.PersonSkillFieldName.STATE.columnName(), state))
                        .and(eq(PersonSkill.PersonSkillFieldName.COUNTY.columnName(), county));
                if (!Strings.isNullOrEmpty(city)) {
                    statement = select(User.UserFieldName.USERID.columnName())
                            .from(mainKeyspace, PERSON_SKILL_TABLE)
                            .where(eq(PersonSkill.PersonSkillFieldName.SKILLNAME.columnName(), skill))
                            .and(eq(PersonSkill.PersonSkillFieldName.STATE.columnName(), state))
                            .and(eq(PersonSkill.PersonSkillFieldName.COUNTY.columnName(), county))
                            .and(eq(PersonSkill.PersonSkillFieldName.CITY.columnName(), city));
                    if (!Strings.isNullOrEmpty(zipcode))
                        statement = select(User.UserFieldName.USERID.columnName())
                                .from(mainKeyspace, PERSON_SKILL_TABLE)
                                .where(eq(PersonSkill.PersonSkillFieldName.SKILLNAME.columnName(), skill))
                                .and(eq(PersonSkill.PersonSkillFieldName.STATE.columnName(), state))
                                .and(eq(PersonSkill.PersonSkillFieldName.COUNTY.columnName(), county))
                                .and(eq(PersonSkill.PersonSkillFieldName.CITY.columnName(), city))
                                .and(eq(PersonSkill.PersonSkillFieldName.ZIPCODE.columnName(), zipcode));
                }
            }
        }

        ResultSet rs = session().execute(statement);
        for (Row row : rs)
            userIds.add(row.getUUID(User.UserFieldName.USERID.columnName()));
        return userIds;
    }

    @Override
    public List<String> getSkills(String searchIndex) {
        List<String> skills = Lists.newLinkedList();
        Statement statement = select(Skill.SkillFieldName.SKILLNAME.columnName())
                .from(mainKeyspace, SKILLS_TABLE)
                .where(eq(Skill.SkillFieldName.SEARCHKEY.columnName(), searchIndex));

        ResultSet rs = session().execute(statement);
        for (Row row : rs)
            skills.add(row.getString(Skill.SkillFieldName.SKILLNAME.columnName()));

        return skills;
    }

    @Override
    public List<UUID> getFriends(UUID userId) {
        List<UUID> friends = Lists.newLinkedList();
        Statement statement = select()
                .from(mainKeyspace, FRIEND_LIST_TABLE)
                .where(eq(Friend.FriendFieldName.USERID1.columnName(), userId));

        ResultSet rs = session().execute(statement);
        for (Row row : rs)
            friends.add(row.getUUID(Friend.FriendFieldName.USERID2.columnName()));

        return friends;
    }

    @Override
    public List<Friend> getFriendList(UUID userId) {
        List<Friend> friends = Lists.newLinkedList();
        Statement statement = select()
                .from(mainKeyspace, FRIEND_LIST_TABLE)
                .where(eq(Friend.FriendFieldName.USERID1.columnName(), userId));

        ResultSet rs = session().execute(statement);
        for (Row row : rs)
            friends.add(Friend.fromRow(row));

        Collections.sort(friends, ComparatorUtil.MOST_RECENT_FRIEND_COMPARATOR);

        return friends;
    }

    @Override
    public User getUser(UUID userId) {
        Statement statement = select()
                .from(mainKeyspace, USER_TABLE)
                .where(eq(User.UserFieldName.USERID.columnName(), userId));
        logger.info("setting user {} by {}", String.valueOf(userId), statement.toString());
        ResultSet rs = session().execute(statement);
        if (!rs.isExhausted())
            return User.fromRow(rs.one());
        else
            return null;
    }

    // get a list of messages most recent not read ones come first
    @Override
    public List<Message> getMessages(UUID userId) {
        List<Message> messages = Lists.newLinkedList();
        Statement statement = select()
                .from(mainKeyspace, MESSAGE_TABLE)
                .where(eq(Message.MessageFieldName.MESSAGETO.columnName(), userId));
        ResultSet rs = session().execute(statement);

        for (Row row : rs)
            messages.add(Message.fromRow(row));

        Collections.sort(messages, ComparatorUtil.MOST_RECENT_MESSAGE_COMPARATOR);
        return messages;
    }

    @Override
    public List<Location> getGeoLocations(Integer countryCode, @Nullable String state,
                                          @Nullable String county, @Nullable String city, @Nullable String zipcode) {

        List<Location> locations = Lists.newLinkedList();

        Statement statement = select()
                .from(mainKeyspace, GEOLOCATION_TABLE)
                .where(eq(Location.LocationFieldName.COUNTRYCODE.columnName(), countryCode));
        if (!Strings.isNullOrEmpty(state)) {
            statement = select()
                    .from(mainKeyspace, GEOLOCATION_TABLE)
                    .where(eq(Location.LocationFieldName.COUNTRYCODE.columnName(), countryCode))
                    .and(eq(Location.LocationFieldName.STATE.columnName(), state));
            if (!Strings.isNullOrEmpty(county)) {
                statement = select()
                        .from(mainKeyspace, GEOLOCATION_TABLE)
                        .where(eq(Location.LocationFieldName.COUNTRYCODE.columnName(), countryCode))
                        .and(eq(Location.LocationFieldName.STATE.columnName(), state))
                        .and(eq(Location.LocationFieldName.COUNTY.columnName(), county));
                if (!Strings.isNullOrEmpty(city)) {
                    statement = select()
                            .from(mainKeyspace, GEOLOCATION_TABLE)
                            .where(eq(Location.LocationFieldName.COUNTRYCODE.columnName(), countryCode))
                            .and(eq(Location.LocationFieldName.STATE.columnName(), state))
                            .and(eq(Location.LocationFieldName.COUNTY.columnName(), county))
                            .and(eq(Location.LocationFieldName.CITY.columnName(), city));
                    if (!Strings.isNullOrEmpty(zipcode))
                        statement = select()
                                .from(mainKeyspace, GEOLOCATION_TABLE)
                                .where(eq(Location.LocationFieldName.COUNTRYCODE.columnName(), countryCode))
                                .and(eq(Location.LocationFieldName.STATE.columnName(), state))
                                .and(eq(Location.LocationFieldName.COUNTY.columnName(), county))
                                .and(eq(Location.LocationFieldName.CITY.columnName(), city))
                                .and(eq(Location.LocationFieldName.ZIPCODE.columnName(), zipcode));
                }

            }

        }

        ResultSet rs = session().execute(statement);

        for (Row row : rs)
            locations.add(Location.fromRow(row));
        return locations;
    }

    @Override
    public Integer getCountryCode(String countryName) {
        Statement statement = select()
                .from(mainKeyspace, COUNTRY_LIST_TABLE)
                .where(eq("country_name", countryName));
        ResultSet rs = session().execute(statement);
        if (!rs.isExhausted())
            return rs.one().getInt("country_code");
        else
            return null;
    }

    @Override
    public List<UUID> getUserIdsBlurSearch(String skill, @Nullable String state, @Nullable String county, @Nullable String city, @Nullable String zipcode) {
        List<UUID> userIds = getUserIds(skill, state, county, city, zipcode);
        if (userIds.isEmpty()) {
            for (String skillName : getSkills(skill)) {
                for (UUID userId : getUserIds(skillName, state, county, city, zipcode)) {
                    if (!userIds.contains(userId))
                        userIds.add(userId);
                }
            }
        }
        return userIds;
    }
}
