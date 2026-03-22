package com.quazzom.mastermind.dto;

import java.util.UUID;

import com.quazzom.mastermind.entity.User;

public class RegisterResponse {

    private UUID uuidPublic;
    private String name;
    private String email;
    private String nickname;
	private Integer age;

    public RegisterResponse(User user) {
        this.uuidPublic = user.getUuidPublic();
        this.name = user.getName();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
		this.age = user.getAge();
    }

    // getters
    public UUID getUuidPublic() {
        return uuidPublic;
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
