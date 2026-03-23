package com.quazzom.mastermind.dto;

public class RegisterRequest {

	public RegisterRequest() {
		this("", "", "", 0, "");
	}
	public RegisterRequest(String name, String email, String nickname, Integer age, String password) {
		this.name = name;
		this.email = email;
		this.nickname = nickname;
		this.age = age;
		this.password = password;
	}

    private String name;
    private String email;
    private String nickname;
    private Integer age;
    private String password;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}


}