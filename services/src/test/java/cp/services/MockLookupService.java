package cp.services;

import cp.model.Friend;
import cp.model.Location;
import cp.model.Message;
import cp.model.User;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;

/**
 * Mockito mocked LookupService created to support unit testing.
 * Created by shengli on 9/16/15.
 */
public class MockLookupService implements LookupService {

    private LookupService mockLookupService;

    public MockLookupService() {
        mockLookupService = mock(LookupService.class);
    }

    public LookupService getMockLookupService() {
        return mockLookupService;
    }


    @Override
    public List<UUID> getUserIds(String skill, @Nullable String state, @Nullable String county, @Nullable String city, @Nullable String zipcode) {
        return mockLookupService.getUserIds(skill, state, county, city, zipcode);
    }

    @Override
    public List<String> getSkills(String searchIndex) {
        return mockLookupService.getSkills(searchIndex);
    }

    @Override
    public List<UUID> getFriends(UUID userId) {
        return mockLookupService.getFriends(userId);
    }

    @Override
    public List<Friend> getFriendList(UUID userId) {
        return mockLookupService.getFriendList(userId);
    }

    @Override
    public User getUser(UUID userId) {
        return mockLookupService.getUser(userId);
    }

    @Override
    public List<Message> getMessages(UUID userId) {
        return mockLookupService.getMessages(userId);
    }

    @Override
    public List<Location> getGeoLocations(Integer countryCode, @Nullable String state, @Nullable String county, @Nullable String city, @Nullable String zipcode) {
        return mockLookupService.getGeoLocations(countryCode, state, county, city, zipcode);
    }

    @Override
    public Integer getCountryCode(String countryName) {
        return mockLookupService.getCountryCode(countryName);
    }

    @Override
    public List<UUID> getUserIdsBlurSearch(String skill, @Nullable String state, @Nullable String county, @Nullable String city, @Nullable String zipcode) {
        return mockLookupService.getUserIdsBlurSearch(skill, state, county, city, zipcode);
    }
}
