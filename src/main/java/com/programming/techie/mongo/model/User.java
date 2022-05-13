package com.programming.techie.mongo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("migration_users")
public class User {
    @Id
    private String id;
    private String username;
    private String fullName;
    private String firstName;
    private String lastName;
    private Date dob;
}
