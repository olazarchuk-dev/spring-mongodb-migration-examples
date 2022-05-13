package com.programming.techie.mongo.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document("migration_users")
public class User {
    @Id
    private String id;
    private String username;
    private String fullName;
    private String firstName;
    private String lastName;
    private Instant updatedAt;
}
