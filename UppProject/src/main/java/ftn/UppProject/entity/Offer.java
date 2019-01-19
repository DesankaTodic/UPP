package ftn.UppProject.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Offer {
	@Id
	@GeneratedValue
	private Long id;	

	private Date date;
	
	private Long price;
		
	private String providerId;
	
	private String processId;
	
	private Long auctionId;
}
