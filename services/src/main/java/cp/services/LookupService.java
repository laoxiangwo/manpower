package cp.services;

import cp.model.Friend;
import cp.model.Location;
import cp.model.Message;
import cp.model.User;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Service layer to abstract away the processing of Cassandra ResultSet rows into  model objects.
 */
public interface LookupService {

    /**
     * find list of people from person_skill table
     */
    List<UUID> getUserIds(String skill, @Nullable String state, @Nullable String county, @Nullable String city,
                          @Nullable String zipcode);


    /**
     * do blurry search : find all skill names from skills table given a skill name
     */
    List<String> getSkills(String searchIndex);

    /**
     * get watches for a person from friend list
     */
    List<UUID> getFriends(UUID userId);


    // get a list of friends sorted by latest friend since date
    List<Friend> getFriendList(UUID userId);

    /**
     * get user given a userId
     */
    User getUser(UUID userId);


    /**
     * get messages for a iser
     */
    List<Message> getMessages(UUID userId);

    /**
     * get geolocations for a country
     */
    List<Location> getGeoLocations(Integer countryCode, @Nullable String state, @Nullable String county, @Nullable
    String city, @Nullable String zipcode);

    /**
     * get country code
     */
    Integer getCountryCode(String countryName);

    /***
     * get a list of given a skill name by doing a blur search if an exact search doesn't find any people
     */

    List<UUID> getUserIdsBlurSearch(String skill, @Nullable String state, @Nullable String county, @Nullable String city,
                                    @Nullable String zipcode);
}
