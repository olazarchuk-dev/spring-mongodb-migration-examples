package com.programming.techie.mongo.config;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.programming.techie.mongo.model.User;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@ChangeLog
public class DatabaseUserSyncChangeLog {

    AtomicInteger successfulUserUpdatesCounter = new AtomicInteger();

    @ChangeSet(order = "003", id = "setFirstAndLastNameToUsers", author = "Alexander Lazarchuk")
    public void setFirstAndLastNameToUsers(MongockTemplate mongockTemplate) {
        Query query = new Query(
                where("fullName").ne(null)
                        .andOperator(where("firstName").is(null), where("lastName").is(null))
        );

        query.fields().include("_id", "fullName");

        long usersWithoutFirstAndLastName = mongockTemplate.count(query, User.class);
        query.limit(3); // set after counting all users to avoid always getting 100 as the maximum number of users

        List<User> users = mongockTemplate.find(query, User.class);
        while(0 < users.size()) {
            users.forEach(user -> {
                try {
                    Criteria criteria = where("_id").is(user.getId());
                    String[] names = splitNamesForUser(user);
                    String firstName = names[0];
                    String lastName = names[1];
                    Update update = new Update()
                            .set("firstName", firstName)
                            .set("lastName", lastName);
                    mongockTemplate.findAndModify(new Query(criteria), update, User.class);
                    successfulUserUpdatesCounter.getAndIncrement();
                } catch (Exception | ParseNameException ex) {
                    ex.getStackTrace();
                }
            });

            users = mongockTemplate.find(query, User.class);
        }
    }

    private String[] splitNamesForUser(User user) throws ParseNameException {
        if (user.getFullName() == null
                || user.getFullName().isEmpty()
                || !user.getFullName().contains(" ")) {
            throw new ParseNameException("Failed to parse the user's name");
        }
        return user.getFullName().split(" ");
    }
}
