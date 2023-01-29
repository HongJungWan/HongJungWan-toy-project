package spring.querydsl.dto;

import com.querydsl.core.annotations.QueryProjection;

public class MemberDto {
	private String userName;
	private int age;

	public MemberDto() {
	}

	@QueryProjection
	public MemberDto(String userName, int age) {
		this.userName = userName;
		this.age = age;
	}

	public String getUserName() {
		return userName;
	}

	public int getAge() {
		return age;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setAge(int age) {
		this.age = age;
	}

}
