package spring.querydsl.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Member {
	@Id
	@GeneratedValue
	@Column(name = "member_id")
	private Long id;
	private String username;
	private int age;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "team_id")
	private Team team;

	protected Member() {
	}

	public Member(String username) {
		this(username, 0);
	}

	public Member(String username, int age) {
		this(username, age, null);
	}

	public Member(String username, int age, Team team) {
		this.username = username;
		this.age = age;
		if (team != null) {
			changeTeam(team);
		}
	}

	//양방향 연관관계 다시 찾아보기.
	public void changeTeam(Team team) {
		this.team = team;
		team.getMembers().add(this);
	}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public int getAge() {
		return age;
	}

	public Team getTeam() {
		return team;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	@Override
	public String toString() {
		return "Member{" +
			"id=" + id +
			", username='" + username + '\'' +
			", age=" + age +
			", team=" + team +
			'}';
	}
}
