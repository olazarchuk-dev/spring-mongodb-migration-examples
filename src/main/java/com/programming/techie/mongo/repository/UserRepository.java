package com.programming.techie.mongo.repository;

import com.programming.techie.mongo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

}
