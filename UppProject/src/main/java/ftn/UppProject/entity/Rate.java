package ftn.UppProject.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Rate{
	@Id
	@GeneratedValue
	private Long id;	
		
	private String userId;
	
	private int rate;
}
