package com.programming.techie.mongo.config;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.programming.techie.mongo.model.User;
import com.programming.techie.mongo.repository.UserRepository;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

@ChangeLog
public class DatabaseUserInitChangeLog {

    @ChangeSet(order = "002", id = "userInitDatabase", author = "Alexander Lazarchuk")
    public void userInitDatabase(UserRepository userRepository) {
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
    }

    private User createNewUser(String username, String fullName, long dob) {
        var user = new User();
        user.setUsername(username);
        user.setFullName(fullName);
        user.setDob(new Date(dob));

        return user;
    }
}
