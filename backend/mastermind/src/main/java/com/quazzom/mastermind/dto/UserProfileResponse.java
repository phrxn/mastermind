package com.quazzom.mastermind.dto;

import com.quazzom.mastermind.entity.User;

public class UserProfileResponse {

    private final String name;
    private final String email;
    private final String nickname;
    private final Integer age;

    public UserProfileResponse(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.age = user.getAge();
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getNickname() {
        return nickname;
    }

    public Integer getAge() {
        return age;
    }
}