package com.taskmanager.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_passwords")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPassword extends BaseEntity {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(nullable = false)
    private String encryptedPassword;
}
