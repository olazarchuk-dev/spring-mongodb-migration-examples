package com.programming.techie.mongo.config;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.programming.techie.mongo.model.User;
import com.programming.techie.mongo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Arrays;

@ChangeLog
@Slf4j
public class UserInitChangeLog {

    @ChangeSet(order = "002", id = "userInitDatabase", author = "Alexander Lazarchuk")
    public void userInitDatabase(UserRepository userRepository) {
        log.info("Order-ChangeSet: {} | Start Users-Init to Database", "002");

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

        log.info("Order-ChangeSet: {} | Finish Users-Init to Database", "002");
    }
}
