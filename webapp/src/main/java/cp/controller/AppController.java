package cp.controller;

import cp.model.User;
import cp.models.SearchCriteria;
import cp.services.SpringLookupService;
import cp.services.SpringUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Created by shengli on 3/16/16.
 */
@RestController
public class AppController {
    @Autowired
    private SpringLookupService springLookupService;

    @Autowired
    private SpringUpdateService springUpdateService;

    @RequestMapping(value = "/registerUser/emial/{email}", method = RequestMethod.POST)
    public void registerUser(@PathVariable String email) {
        User user = new User();
        user.userId = UUID.randomUUID();
        //todo: add validator for email
        user.email = email;
    }

    @RequestMapping(value = "/searchUser", method = RequestMethod.POST, headers = {"Content-type=application/json"})
    public List<UUID> getUsersBySkill(@RequestBody SearchCriteria searchCriteria) {
        List<UUID> userList = springLookupService.getLookupService().getUserIdsBlurSearch(searchCriteria.getSkill(),
                searchCriteria.getState(), searchCriteria.getCounty(), searchCriteria.getCity(), searchCriteria.getZip());
        return userList;
    }

}
