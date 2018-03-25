package cp.services;

import com.datastax.driver.core.Session;
import cp.model.Message;
import cp.model.Skill;
import cp.model.User;

import java.util.UUID;

import static org.mockito.Mockito.mock;

/**
 * Mockito mocked UpdateService created to support unit testing.
 * Created by shengli on 9/16/15.
 */
public class MockUpdateService implements UpdateService {

    private final UpdateService mockUpdateService;

    public MockUpdateService() {
        mockUpdateService = mock(UpdateService.class);
    }

    public UpdateService getMockUpdateService() {
        return mockUpdateService;
    }

    @Override
    public Session session() {
        return mockUpdateService.session();
    }

    @Override
    public void updateUser(User user, Iterable<User.UserFieldName> fields) {
        mockUpdateService.updateUser(user, fields);
    }

    @Override
    public void updateFriendList(UUID userId1, UUID userId2) {
        mockUpdateService.updateFriendList(userId1, userId2);
    }

    @Override
    public void updateMessage(Message message, Iterable<Message.MessageFieldName> fields) {
        mockUpdateService.updateMessage(message, fields);
    }

    @Override
    public void updateSkillList(String searchKey, String skill, UUID userId) {
        mockUpdateService.updateSkillList(searchKey, skill, userId);
    }

    @Override
    public void updatePersonSkill(User user, String skill) {
        mockUpdateService.updatePersonSkill(user, skill);
    }

    @Override
    public void updatePersonSkill(User user) {
        mockUpdateService.updatePersonSkill(user);
    }

    @Override
    public void removeSkill(String searchKey, String skill) {
        mockUpdateService.removeSkill(searchKey, skill);
    }

    @Override
    public void removePersonSkill(User user, String skill) {
        mockUpdateService.removePersonSkill(user, skill);
    }

    @Override
    public void removePersonSkill(User user) {
        mockUpdateService.removePersonSkill(user);
    }

    @Override
    public void updateSkillSearchIndex(String skill, UUID useId) {
        mockUpdateService.updateSkillSearchIndex(skill, useId);
    }
}
