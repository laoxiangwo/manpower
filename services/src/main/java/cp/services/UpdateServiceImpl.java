package cp.services;

import cp.config.ConfigurationService;
import cp.connect.CassandraSessionManager;
import cp.model.*;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.Update;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static cp.config.ConfigurationKeys.*;
import static com.datastax.driver.core.querybuilder.QueryBuilder.*;

public class UpdateServiceImpl implements UpdateService {
    private static final Logger log = LoggerFactory.getLogger(UpdateServiceImpl.class);

    private final CassandraSessionManager sessionManager;
    private final ConfigurationService configService;
    private final String mainKeyspace;

    public UpdateServiceImpl(ConfigurationService configService, CassandraSessionManager sessionManager) {
        this.configService = configService;
        this.sessionManager = sessionManager;
        this.mainKeyspace = configService.getValue(MAIN_KEYSPACE_KEY);
    }

    @Override
    public Session session() {
        return sessionManager.getSession();
    }

    @Override
    public void updateUser(User user, Iterable<User.UserFieldName> fields) {
        Update update = update(mainKeyspace, Constants.USER_TABLE);
        for (User.UserFieldName field : fields) {
            update.with(set(field.columnName(), user.getValueForDatabase(field)));
        }
        update.where(eq(User.UserFieldName.USERID.columnName(), user.userId));
        session().execute(update);
    }

    @Override
    public void updateFriendList(UUID userId1, UUID userId2) {
        Update.Where update = update(mainKeyspace, Constants.FRIEND_LIST_TABLE)
                .with(set(Constants.FRIEND_UPDATE_TIME, LocalDate.now().toString()))
                .where(eq(Constants.USER_ID1, userId1))
                .and(eq(Constants.USER_ID2, userId2));
        session().execute(update);
    }

    @Override
    public void updateMessage(Message message, Iterable<Message.MessageFieldName> fields) {
        Update update = update(mainKeyspace, Constants.MESSAGE_TABLE);
        for (Message.MessageFieldName field : fields) {
            update.with(set(field.columnName(), message.getValueForDatabase(field)));
        }
        update.where(eq(Message.MessageFieldName.MESSAGETO.columnName(), message.messageTo))
                .and(eq(Message.MessageFieldName.MESSAGEFROM.columnName(), message.messageFrom))
                .and(eq(Message.MessageFieldName.MESSAGE.columnName(), message.message));
        session().execute(update);
    }

    @Override
    public void updateSkillList(String searchKey, String skill, UUID userId) {
        Update.Where update = update(mainKeyspace, Constants.SKILLS_TABLE)
                .with(set(Skill.SkillFieldName.USERID.columnName(), userId))
                .where(eq(Skill.SkillFieldName.SEARCHKEY.columnName(), searchKey))
                .and(eq(Skill.SkillFieldName.SKILLNAME.columnName(), skill));
        session().execute(update);
    }

    @Override
    public void updatePersonSkill(User user, String skill) {
        Update update = update(mainKeyspace, Constants.PERSON_SKILL_TABLE);
        update.with(set(PersonSkill.PersonSkillFieldName.UPDATE_TIME.columnName(), LocalDate.now().toString()));
        update.where(eq(PersonSkill.PersonSkillFieldName.SKILLNAME.columnName(), skill))
                .and(eq(User.UserFieldName.STATE.columnName(), user.state))
                .and(eq(User.UserFieldName.COUNTY.columnName(), user.county))
                .and(eq(User.UserFieldName.CITY.columnName(), user.city))
                .and(eq(User.UserFieldName.ZIPCODE.columnName(), user.zipCode))
                .and(eq(User.UserFieldName.USERID.columnName(), user.userId));
        session().execute(update);
    }

    @Override
    public void updatePersonSkill(User user) {
        for(String skill:user.skills.keySet())
            updatePersonSkill(user, skill);
    }

    @Override
    public void removeSkill(String searchKey, String skill) {
        Statement delete = delete()
                .from(mainKeyspace, Constants.SKILLS_TABLE)
                .where(eq(Skill.SkillFieldName.SEARCHKEY.columnName(), searchKey))
                .and(eq(Skill.SkillFieldName.SKILLNAME.columnName(), skill));
        ResultSet rs = session().execute(delete);
    }

    @Override
    public void removePersonSkill(User user, String skill) {
        Statement delete = delete()
                .from(mainKeyspace, Constants.PERSON_SKILL_TABLE)
                .where(eq(PersonSkill.PersonSkillFieldName.SKILLNAME.columnName(), skill))
                .and(eq(User.UserFieldName.STATE.columnName(), user.state))
                .and(eq(User.UserFieldName.COUNTY.columnName(), user.county))
                .and(eq(User.UserFieldName.CITY.columnName(), user.city))
                .and(eq(User.UserFieldName.ZIPCODE.columnName(), user.zipCode))
                .and(eq(User.UserFieldName.USERID.columnName(), user.userId));

        ResultSet rs = session().execute(delete);
    }

    @Override
    public void removePersonSkill(User user) {
        for(String skill : user.skills.keySet())
            removePersonSkill(user, skill);
    }

    @Override
    public void updateSkillSearchIndex(String skill, UUID userId) {
        for (int i = 1; i <=skill.length(); i++)
            updateSkillList(skill.substring(0, i), skill, userId);
    }

}
