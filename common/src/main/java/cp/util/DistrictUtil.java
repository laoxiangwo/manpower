package cp.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import javax.annotation.Nullable;

/**
 * Various and sundry district manipulation utilities
 */
public class DistrictUtil {

    private DistrictUtil() {}

    /**
     * Given state FIPS and county FIPS codes, assemble a stateCountyFips value if possible.
     *
     * @param stateFips  A (possibly null or empty) 2 digit State FIPS code string
     * @param countyFips A (possibly null or empty) 4 digit County FIPS code string
     * @return A stateCountyFips construction if both <tt>stateFips</tt> and <tt>countyFips</tt> are present and not empty, otherwise <tt>null</tt>
     * @throws RuntimeException if either argument is not null, not empty and not the proper length
     */
    public static String assembleStateCountyFips(@Nullable String stateFips, @Nullable String countyFips) {
        // only assemble if we have both
        if (Strings.isNullOrEmpty(stateFips) || Strings.isNullOrEmpty(countyFips)) {
            return null;
        }
        Preconditions.checkArgument(stateFips.length() == 2, "State FIPS invalid: %s", stateFips);
        Preconditions.checkArgument(countyFips.length() == 3, "County FIPS invalid: %s", countyFips);
        return stateFips + countyFips;
    }

    /**
     * Given a state and a numeric string Congressional District number,
     * assemble a Congressional District name that is unique across the country
     *
     * @param state A A (possibly null or empty) 2 character state name
     * @param congressionalDistrict A (possibly null or empty) numeric Congressional district string
     * @return A concatenation of the congressional district name from the state and the number, or null if elements missing
     */
    public static String assembleCongressionalDistrictName(@Nullable String state, @Nullable String congressionalDistrict) {
        if (Strings.isNullOrEmpty(state) || Strings.isNullOrEmpty(congressionalDistrict)) {
            return null;
        }
        Preconditions.checkArgument(state.length() == 2, "State is invalid: %s should be a 2 letter state abbreviation");
        Preconditions.checkArgument(congressionalDistrict.length() <= 2, "Congressional is invalid: %s should be a congressional district number");

        String trimmedCD;
        try {
            Integer intCD = new Integer(congressionalDistrict);
            if (intCD <= 0 || intCD >= 100) {
                throw new NumberFormatException();
            }
            trimmedCD = intCD.toString();
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Congressional district " + congressionalDistrict + " is invalid");
        }

        // 0 pad the number so that the names are all the same length
        // and sort properly
        return state + "-" + (trimmedCD.length() == 1 ? "0" : "") + trimmedCD;
    }
}
