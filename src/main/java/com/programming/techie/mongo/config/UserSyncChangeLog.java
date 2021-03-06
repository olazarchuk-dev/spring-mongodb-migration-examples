package com.programming.techie.mongo.config;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.programming.techie.mongo.error.ParseNameException;
import com.programming.techie.mongo.model.User;
import com.programming.techie.mongo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@ChangeLog
@Slf4j
public class UserSyncChangeLog {

    private final AtomicInteger successfulUpdatesCounter = new AtomicInteger();

    @Value("${app.sync.one-per-query-limit}")
    public int onePerQueryLimit;

    @Value("${app.sync.update-expiration-seconds}")
    public int updateExpirationSeconds = 30;

    @ChangeSet(order = "002", id = "userInitDatabase", author = "Alexander Lazarchuk")
    public void userInitDatabase(UserRepository userRepository) {
        log.info("Order-ChangeSet | Start Users-Init to Database");

        var users = Arrays.asList(
                User.builder().username("johndoe").fullName("John Doe").updatedAt(Instant.now()).build(),
                User.builder().username("maks").fullName("Maksym Vakulenko").updatedAt(Instant.now()).build(),
                User.builder().username("olexandr").fullName("Oleksandr Vorobei").updatedAt(Instant.now()).build(),
                User.builder().username("rostik").fullName("Rostyslav Shcherbyna").updatedAt(Instant.now()).build(),
                User.builder().username("serega").fullName("Serhii Martyniuk").updatedAt(Instant.now()).build(),
                User.builder().username("katya").fullName("Katerina Podolska").updatedAt(Instant.now()).build(),
                User.builder().username("oksana").fullName("Oksana Perekipska").updatedAt(Instant.now()).build(),
                User.builder().username("yarik").fullName("Yaroslav Soboliev").updatedAt(Instant.now()).build(),
                User.builder().username("dima").fullName("Dmitriy Khmarskyi").updatedAt(Instant.now()).build());
        userRepository.insert(users);

        log.info("Order-ChangeSet | Finish Users-Init to Database");
    }

    @ChangeSet(order = "003", id = "setFirstAndLastNameToUsers", author = "Alexander Lazarchuk", runAlways = true)
    public void setFirstAndLastNameToUsers(MongockTemplate mongockTemplate) {
        log.info("Order-ChangeSet | Start Users-Sync to Database");

        Criteria toDate = Criteria.where("updatedAt").lt( Instant.now().minusSeconds(updateExpirationSeconds) );
        var query = Query.query(
                new Criteria().andOperator(toDate));

        query.fields().include("_id", "fullName");

        mongockTemplate.count(query, User.class);
        query.limit(onePerQueryLimit);

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
                            .set("lastName", lastName)
                            .set("updatedAt", Instant.now());
                    mongockTemplate.findAndModify(new Query(criteria), update, User.class);
                    successfulUpdatesCounter.getAndIncrement();
                } catch (Exception | ParseNameException ex) {
                    ex.getStackTrace();
                }
            });

            users = mongockTemplate.find(query, User.class);
        }

        log.info("Order-ChangeSet | Successful count update = {} user(s)", successfulUpdatesCounter);
        log.info("Order-ChangeSet | Finish Users-Sync to Database");
    }

    private String[] splitNamesForUser(User user) throws ParseNameException {
        if (StringUtils.isEmpty(user.getFullName()) || !user.getFullName().contains(" ")) {
            throw new ParseNameException("Failed to parse the user's name");
        }
        return user.getFullName().split(" ");
    }
}
