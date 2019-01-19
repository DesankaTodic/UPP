package ftn.UppProject.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Auction {
	@Id
	@GeneratedValue
	private Long id;
	
	private String category;
	
	private String jobDescription;
	
	private Long maxValue;
	
	private Date maxDate;
	
	private int maxOffer;
	
	private Date finishedDate;
	
	private String initiatorId;
}
