package com.quazzom.mastermind.dto;

import com.quazzom.mastermind.entity.User;

public class MeResponse {

    private final boolean authenticated;
    private final Long id;
    private final String name;
    private final String email;
    private final String nickname;
    private final Integer age;

    public MeResponse(User user) {
        this.authenticated = true;
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.age = user.getAge();
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public Long getId() {
        return id;
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