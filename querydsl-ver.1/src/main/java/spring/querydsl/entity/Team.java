package spring.querydsl.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Team {
	@Id
	@GeneratedValue
	@Column(name = "team_id")
	private Long id;
	private String name;
	@OneToMany(mappedBy = "team")
	private List<Member> members = new ArrayList<>();

	protected Team() {
	}

	public Team(String name) {
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public List<Member> getMembers() {
		return members;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMembers(List<Member> members) {
		this.members = members;
	}

	@Override
	public String toString() {
		return "Team{" +
			"id=" + id +
			", name='" + name + '\'' +
			'}';
	}
}
