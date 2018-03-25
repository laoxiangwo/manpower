package cp.services;

import cp.model.Message;
import cp.model.Skill;
import cp.model.User;
import com.datastax.driver.core.Session;

import java.util.UUID;

/**
 * All updates to Cassandra tables should go through this service.
 *
 */
public interface UpdateService {

    /**
     * Get the current Session object.
     * @return The current session object.
     */
    Session session();

    /***
     * update new user
     */
    void updateUser(User user, Iterable<User.UserFieldName> fields);

    /***
     * update friend list
     */

    void updateFriendList(UUID userId1, UUID userId2);

    /***
     * update message
     */
    void updateMessage(Message message, Iterable<Message.MessageFieldName> fields);

    /***
     * update skill list
     */
    void updateSkillList(String searchKey, String skill, UUID usrId);

    /***
     * update person skill
     */
    void updatePersonSkill(User user, String skill);

    void updatePersonSkill(User user);

    /***
     * remove skill from search table
     */

    void removeSkill(String searchKey, String skill);

    /***
     * remove a record from peron skill table
     */
    void removePersonSkill(User user, String skill);

    void removePersonSkill(User user);

    /***
     * update skill search index
     */
    void updateSkillSearchIndex(String skill, UUID userId);

}
