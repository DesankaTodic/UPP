package ftn.UppProject.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ftn.UppProject.entity.Auction;
import ftn.UppProject.repository.IAuctionRepository;

@Service
public class AuctionService {

	@Autowired
	private IAuctionRepository auctionRepository;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private IdentityService identityService;

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private NewBiddersService newBiddersService;

	public void saveAuction(Map<String, String> params) throws ParseException {
		System.out.println("+++++Servis cuvanje aukcije");

		Auction auction = new Auction();
		auction.setCategory(params.get("category"));
		auction.setJobDescription(params.get("jobDescription"));

		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Date maxDate = format.parse(params.get("maxDate"));
		Date finishedDate = format.parse(params.get("finishedDate"));

		auction.setMaxDate(maxDate);
		auction.setFinishedDate(finishedDate);
		auction.setMaxOffer(Integer.parseInt(params.get("maxOffer")));
		auction.setMaxValue(Long.parseLong(params.get("maxValue")));
		
		String processId = params.get("processId");
		
		String logUserId = (String)runtimeService.getVariable(processId, "logUserId");
		auction.setInitiatorId(logUserId);

		auction = auctionRepository.save(auction);

		runtimeService.setVariable(processId, "auction", auction);
		runtimeService.setVariable(processId, "processId", processId);
	}

	@SuppressWarnings("unchecked")
	public void findCompaniesByCategory(Auction auction, String processId) {
		System.out.println("+++++Servis trazenje firmi za zaposljavanje");

		int numberOfRepetitons = (int) runtimeService.getVariable(processId, "startover");
		List<User> providers = new ArrayList<>();
		
		if (numberOfRepetitons <= 2) {
			List<User> users = new ArrayList<>();
			String categoryName = auction.getCategory();
			if (numberOfRepetitons == 0) {
				UserQuery userQuery = identityService.createUserQuery().memberOfGroup(categoryName);

				users = userQuery.list();
				String logUserId = (String)runtimeService.getVariable(processId, "logUserId");
				UserQuery uq1 = identityService.createUserQuery().userId(logUserId);
				User user = uq1.singleResult();
				users.remove(user);
			} else {	
				runtimeService.setVariable(processId, "sendNot", false);
				
				newBiddersService.sendNewBiddersNotification(processId, String.valueOf(auction.getId()));
				
				users = (List<User>) runtimeService.getVariable(processId, "providers");
			}
			
			int offersNo = auction.getMaxOffer();

			if (offersNo > users.size()) {
				runtimeService.setVariable(processId, "providersNumber", users.size());
				runtimeService.setVariable(processId, "providers", users);
			} else {
				if (offersNo < users.size()) {
					users = getRandomListOfUsers(users, offersNo);
				}
				for (int i = 0; i < offersNo; i++) {
					providers.add(users.get(i));
				}

				runtimeService.setVariable(processId, "auction", auction);
				runtimeService.setVariable(processId, "providersNumber", offersNo);
				runtimeService.setVariable(processId, "providers", providers);
			}
		}
	}

	public List<User> getRandomListOfUsers(List<User> allUsers, int min) {
		System.out.println("+++++Servis biranje random firmi");

		int usersSize = allUsers.size();
		List<User> users = new ArrayList<>();

		Random randomForUsersIndex = new Random();

		ArrayList<Integer> numbers = new ArrayList<Integer>();

		while (numbers.size() < min) {
			int random = randomForUsersIndex.nextInt(usersSize);

			if (!numbers.contains(random)) {
				numbers.add(random);
			}
		}

		for (int i = 0; i < numbers.size(); i++) {
			users.add(allUsers.get(numbers.get(i)));
		}

		return users;
	}

	public void enough(User provider, String processId, int maxOffer, int providers, Auction auction) {
		System.out.println("+++++Servis svi su poslali ponudu");

		String description = auction.getJobDescription();
		String maxValue = Long.toString(auction.getMaxValue());
		String finishedDate = auction.getFinishedDate().toString();
		emailService.sendEmail("Obavjestenje",
				"Postovani, \n ovo je ponuda za posao. \n" + "Opis posla: " + description + "\n"
						+ "granicna cijena: " + maxValue + "\n" + "rok izvrsavanja: " + finishedDate + "\n"
						+ "Popunite ponudu: http://localhost:9000/fillOffer/" + processId + "/"
						+ provider.getId() + "/" + auction.getId() + "\n",
				provider.getEmail());
	}

	public void less(String processId, int maxOffer, int providers) {
		System.out.println("+++++Servis manje nego ocekivano ponuda");
		
		String logUserId = (String)runtimeService.getVariable(processId, "logUserId");
		UserQuery uq1 = identityService.createUserQuery().userId(logUserId);
		User user = uq1.singleResult();
		
		emailService.sendEmail("Obavjestenje",
				"Postovovani, \n trenutno nemamo toliko ponudjaca za ovu aukciju. \n"
						+ "Ako zelite da nastavite: http://localhost:9000/continueAuction/" + processId + "\n"
						+ "a ako ne: http://localhost:9000/terminateAuction/" + processId,user.getEmail());
	}

	public void notOne(String processId, int maxOffer, int providers) {
		System.out.println("+++++Servis nijedan ponudjac");
		
		String logUserId = (String)runtimeService.getVariable(processId, "logUserId");
		UserQuery uq1 = identityService.createUserQuery().userId(logUserId);
		User user = uq1.singleResult();
		
		emailService.sendEmail("Obavjestenje", "Postovani, \n trenutno nemamo ponudjace za ovu aukciju.", user.getEmail());
	}

	public void updateAuction(String auctionId, String newDate, String processId) throws ParseException {
		System.out.println("+++++Service izmjena aukcije");

		Auction auction = auctionRepository.findOne(Long.parseLong(auctionId));
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Date maxDate = format.parse(newDate);
		auction.setMaxDate(maxDate);
		auction = auctionRepository.save(auction);
		runtimeService.setVariable(processId, "auction", auction);
	}

	public void enough() {
		System.out.println("+++++Dosta");
	}

	public Auction findOne(Long auctionId) {
		return auctionRepository.findOne(auctionId);
	}
}
