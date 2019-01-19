package ftn.UppProject.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ftn.UppProject.entity.Auction;
import ftn.UppProject.entity.Offer;
import ftn.UppProject.repository.IOfferRepository;


@Service
public class OfferService {

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private IOfferRepository offerRepository;

	@Autowired
	private IdentityService identityService;


	@SuppressWarnings("unchecked")
	public void saveOffer(Map<String, String> params) throws ParseException {
		System.out.println("+++++Servis cuvanje ponude");
		Offer offer = new Offer();

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Date date = format.parse(params.get("date"));

		offer.setDate(date);
		offer.setPrice(Long.parseLong(params.get("price")));
		offer.setProcessId(params.get("processId"));
		offer.setProviderId(params.get("providerId"));
		offer.setAuctionId(Long.parseLong(params.get("auctionId")));

		offer = offerRepository.save(offer);
		
		List<Offer> offers = null;
		
		try{
			offers = (List<Offer>) runtimeService.getVariable(params.get("processId"), "offers");
		}catch (Exception e){
			System.out.println("+++++Izuzetak kod List<Offer> offers = (List<Offer>) runtimeService.getVariable(params.get('processId'), 'offers'): Entity does not exist: ftn.UppProject.entity.Offer - 1");
		}
		
		offers = offers == null ? new ArrayList<>() : offers;

		offers.add(offer);

		runtimeService.setVariable(params.get("processId"), "offers", offers);
	}

	@SuppressWarnings({ "unchecked" })
	public void completed(String processId, Auction auction) {
		System.out.println("+++++Servis nudjenje opcija klijentu");

		List<Offer> offers = (List<Offer>) runtimeService.getVariable(processId, "offers");

		offers = offers == null ? new ArrayList<>() : offers;
		int offersNumber = offers.size();

		List<User> providers = (List<User>) runtimeService.getVariable(processId, "providers");
		int providersNumber = providers.size();

		runtimeService.setVariable(processId, "offersNumber", offersNumber);

		List<User> bidders = (List<User>) runtimeService.getVariable(processId, "bidders");
		bidders = bidders == null ? new ArrayList<>() : bidders;

		providersNumber += bidders.size();
		
		String logUserId = (String)runtimeService.getVariable(processId, "logUserId");
		UserQuery uq1 = identityService.createUserQuery().userId(logUserId);
		User logUser = uq1.singleResult();

		if (offers.isEmpty()) {
			emailService.sendEmail("Izvinjavamo se", "Postovani,\n nijedan ponudjac nije dostavio ponudu.\n"
					+ "Ukoliko zelite da odustanete: " + "http://localhost:9000/cancel/"
					+ processId + "/" + auction.getId() + "\n ako zelite da produzite aukciju: "
					+ "http://localhost:9000/changeOffersDate/" + processId + "/" + auction.getId() + "/yyyy-MM-dd", logUser.getEmail());
		} else if (offersNumber < providersNumber) {
			for (Offer offer : offers) {
				for (User user : providers) {
					if (offer.getProviderId().equals(user.getId())) {
						bidders.add(user);
					}
				}
			}

			runtimeService.setVariable(processId, "providers", providers);
			runtimeService.setVariable(processId, "bidders", bidders);

			runtimeService.setVariable(processId, "providersNumber", providersNumber);

			emailService.sendEmail("Izvinjavamo se",
					"Postovani,\n ima manje ponuda nego sto ste ocekivali.\n" + "KLiknite ako ste zadovoljni: "
							+ "http://localhost:9000/showOffers/" + processId + "/" + auction.getId()
							+ "\n a ako hocete da produzite aukciju: "
							+ "http://localhost:9000/changeOffersDate/" + processId + "/" + auction.getId() + "/yyyy-MM-dd",
							logUser.getEmail());
		} else {
			emailService.sendEmail("Odlicne vijesti", "Postovani, \n dobili ste onoliko ponuda koliko ste zeljeli. \n" + "Pregled ponuda: "
					+ "http://localhost:9000/showOffers/" + processId + "/" + auction.getId(), logUser.getEmail());
			runtimeService.setVariable(processId, "bidders", providers);

		}
	}

	@SuppressWarnings("unchecked")
	public List<Offer> rangOffers(Long maxValue, Date maxDate, String processId) {
		System.out.println("+++++Servis rangiranje ponuda");

		List<Offer> offers = (List<Offer>) runtimeService.getVariable(processId, "offers");
		List<Long> orderedByPrice = new ArrayList<>();
		List<Long> orderedByPriceBigger = new ArrayList<>();
		List<Long> orderedByDate = new ArrayList<>();
		List<Long> orderedByDateBigger = new ArrayList<>();
		
		offers.forEach(o -> {
			if(o.getPrice().compareTo(maxValue) <= 0) {
				orderedByPrice.add(o.getId());
			} else {
				orderedByPriceBigger.add(o.getId());
			}
			if(o.getDate().before(maxDate) || o.getDate().equals(maxDate)) {
				orderedByDate.add(o.getId());
			} else {
				orderedByDateBigger.add(o.getId());
			}
		});
		
		orderedByPrice.removeAll(orderedByDateBigger);
		orderedByDate.removeAll(orderedByPriceBigger);
		
		List<Offer> sortedOffers = new ArrayList<>();
		orderedByPrice.forEach(o -> sortedOffers.add(offerRepository.findOne(o)));
		orderedByDate.forEach(o -> sortedOffers.add(offerRepository.findOne(o)));
		orderedByDateBigger.forEach(o -> sortedOffers.add(offerRepository.findOne(o)));
		orderedByPriceBigger.forEach(o -> sortedOffers.add(offerRepository.findOne(o)));
		
		List<Offer> finalla = sortedOffers.stream().distinct().collect(Collectors.toList());
		
		return finalla;
	}

	public void showAllOffers(String processId, String auctionId) {
		System.out.println("+++++Servis prikaz svih ponuda");
	}
	
	public void giveUp() {
		System.out.println("+++++Servis odustajanje");
	}
	
	
	public void acceptOffer(String processId, String auctionId) {
		System.out.println("+++++Servis prihvatanje ponude");
		
		String offerId = (String) runtimeService.getVariable(processId, "offerId");
		
		Offer offer = offerRepository.findOne(Long.valueOf(offerId));
		
		if(offer != null){
			UserQuery uq1 = identityService.createUserQuery().userId(offer.getProviderId());
			User user = uq1.singleResult();

			emailService.sendEmail("Rok izvrsetka",
					"Postovani,\n vasa ponuda je prihvacena. \n"+
							"Potvrdite ovdje (dodajte rok izvrsavanja na kraj url-a u formatu: yyyy-mm-dd) \n"+
							"http://localhost:9000/acceptRequest/" + processId + "/" + auctionId+ "/" + offerId+"/yyyy-MM-dd", user.getEmail());
		}
	}
	
	public void removeOldOffers(String auctionId, String processId) {
		System.out.println("+++++Servis brisanje starih ponuda");

		int currentNumber = (int) runtimeService.getVariable(processId, "startover");
		currentNumber++;
		runtimeService.setVariable(processId, "startover", currentNumber);
		offerRepository.delete(offerRepository.findByAuctionId(Long.parseLong(auctionId)));
		
		String logUserId = (String)runtimeService.getVariable(processId, "logUserId");
		UserQuery uq1 = identityService.createUserQuery().userId(logUserId);
		User logUser = uq1.singleResult();

		
		emailService.sendEmail("Rok izvrsetka",
				"Postovani, \n unesite novi rok aukcije na kraju url-a u formatu: yyyy-mm-dd \n "
						+ "http://localhost:9000/insertNewMaxDate/" + processId + "/" + auctionId+"/yyyy-MM-dd", logUser.getEmail());
	}	
	
	public Offer findOne(Long offerId) {
		return offerRepository.findOne(offerId);
	}
	
	public void updateOffer(Offer offer){
		offerRepository.save(offer);
	}
}
