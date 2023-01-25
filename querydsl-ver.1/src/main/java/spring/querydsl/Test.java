package spring.querydsl;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Test {
	@Id
	@Generated
	private Long id;
	
}
