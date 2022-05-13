package com.programming.techie.mongo.config;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.programming.techie.mongo.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@ChangeLog
@Slf4j
public class UserSyncChangeLog {

    public final int QUERY_LIMIT = 3; // set after counting all users to avoid always getting 100 as the maximum number of users
    private final AtomicInteger successfulUpdatesCounter = new AtomicInteger();

    @ChangeSet(order = "003", id = "setFirstAndLastNameToUsers", author = "Alexander Lazarchuk", runAlways = true)
    public void setFirstAndLastNameToUsers(MongockTemplate mongockTemplate) throws ParseException {
        log.info("Order-ChangeSet: {} | Start Users-Sync to Database", "003");

        var query = new Query(
                where("fullName")
                        .ne(null)
                        .andOperator(where("firstName").is(null), where("lastName").is(null))
        );

        query.fields().include("_id", "fullName");

        mongockTemplate.count(query, User.class);
        query.limit(QUERY_LIMIT);

        List<User> users = mongockTemplate.find(query, User.class);
        while (!users.isEmpty()) {
            users.forEach(user -> {
                try {
                    var criteria = where("_id").is(user.getId());
                    var names = splitNamesForUser(user);
                    var firstName = names[0];
                    var lastName = names[1];
                    Update update = new Update()
                            .set("firstName", firstName)
                            .set("lastName", lastName);
                    mongockTemplate.findAndModify(new Query(criteria), update, User.class);
                    successfulUpdatesCounter.getAndIncrement();
                } catch (Exception | ParseNameException ex) {
                    ex.getStackTrace();
                }
            });

            users = mongockTemplate.find(query, User.class);
        }

        log.info("Order-ChangeSet: {} | Finish Users-Sync to Database", "003");
    }

    private String[] splitNamesForUser(User user) throws ParseNameException {
        if (StringUtils.isEmpty(user.getFullName()) || !user.getFullName().contains(" ")) {
            throw new ParseNameException("Failed to parse the user's name");
        }
        return user.getFullName().split(" ");
    }
}