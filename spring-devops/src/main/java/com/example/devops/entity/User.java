package com.example.devops.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    private String email;
    private String pwd;
    private String username;

    private String profileImage;

    @Builder
    public User(String email, String pwd, String username) {
        this.email = email;
        this.pwd = pwd;
        this.username = username;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
}
