package ftn.UppProject.service;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ftn.UppProject.entity.Offer;

@Service
public class MoreInfoService {

	@Autowired
	AuctionService auctionService;

	@Autowired
	IdentityService identityService;

	@Autowired
	RuntimeService runtimeService;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	OfferService offerService;
	
	public void askForInfo(String processId, String auctionId) {
		System.out.println("+++++Servis trazenje vise informacija");
		
		String offerId = (String) runtimeService.getVariable(processId, "offerId");
		
		Offer offer = offerService.findOne(Long.valueOf(offerId));
		
		if(offer != null){
			UserQuery uq1 = identityService.createUserQuery().userId(offer.getProviderId());
			User user = uq1.singleResult();

			emailService.sendEmail("Zahtjev za vise informacija",
					"Postovani, dodajte dodatne informacije o ponudi. \n"+
					"Ovdje popunite: http://localhost:9000/addingMoreInfo/" + processId + "/" + auctionId+ "/" + offerId, user.getEmail());
		}
	}
}
