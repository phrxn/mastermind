package com.quazzom.mastermind.dto;

import com.quazzom.mastermind.entity.User;

public class RegisterResponse {

    private Long id;
    private String name;
    private String email;
    private String nickname;
	private Integer age;

    public RegisterResponse(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
		this.age = user.getAge();
    }

    // getters
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
