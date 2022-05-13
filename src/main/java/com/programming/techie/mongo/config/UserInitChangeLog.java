package com.programming.techie.mongo.config;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.programming.techie.mongo.model.User;
import com.programming.techie.mongo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

@ChangeLog
@Slf4j
public class UserInitChangeLog {

    @ChangeSet(order = "002", id = "userInitDatabase", author = "Alexander Lazarchuk")
    public void userInitDatabase(UserRepository userRepository) {
        log.info("Order-ChangeSet: {} | Start Users-Init to Database", "002");
        List<User> users = new ArrayList<>();
        users.add(createNewUser("johndoe", "John Doe", System.currentTimeMillis()));
        users.add(createNewUser("maks", "Maksym Vakulenko", System.currentTimeMillis()));
        users.add(createNewUser("olexandr", "Oleksandr Vorobei", System.currentTimeMillis()));
        users.add(createNewUser("rostik", "Rostyslav Shcherbyna", System.currentTimeMillis()));
        users.add(createNewUser("serega", "Serhii Martyniuk", System.currentTimeMillis()));
        users.add(createNewUser("katya", "Katerina Podolska", System.currentTimeMillis()));
        users.add(createNewUser("oksana", "Oksana Perekipska", System.currentTimeMillis()));
        users.add(createNewUser("yarik", "Yaroslav Soboliev", System.currentTimeMillis()));
        users.add(createNewUser("dima", "Dmitriy Khmarskyi", System.currentTimeMillis()));

        userRepository.insert(users);
        log.info("Order-ChangeSet: {} | Finish Users-Init to Database", "002");
    }

    private User createNewUser(String username, String fullName, long updatedAt) {
        var user = new User();
        user.setUsername(username);
        user.setFullName(fullName);
        user.setUpdatedAt(new Date(updatedAt));

        return user;
    }
}
