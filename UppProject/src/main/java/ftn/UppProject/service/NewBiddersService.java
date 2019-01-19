package ftn.UppProject.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ftn.UppProject.entity.Auction;

@Service
public class NewBiddersService {

	@Autowired
	AuctionService auctionService;

	@Autowired
	IdentityService identityService;

	@Autowired
	RuntimeService runtimeService;

	@SuppressWarnings("unchecked")
	public void sendNewBiddersNotification(String processId, String auctionId) {
		Auction auction = auctionService.findOne(Long.parseLong(auctionId));
		System.out.println("+++++Servis obavjestavanje novih ponudjaca");

		List<User> providers = (List<User>) runtimeService.getVariable(processId, "providers");
		providers = providers == null ? new ArrayList<>() : providers;

		String categoryName = auction.getCategory();

		UserQuery userQuery = identityService.createUserQuery().memberOfGroup(categoryName);

		List<User> users = userQuery.list();

		List<User> newBidders = new ArrayList<>();

		List<String> providersIds = providers.stream().map(User::getId).collect(Collectors.toList());
		List<String> allUsersIds = users.stream().map(User::getId).collect(Collectors.toList());

		allUsersIds.removeAll(providersIds);

		allUsersIds.forEach(id -> {
			UserQuery uq1 = identityService.createUserQuery().userId(id);
			User user = uq1.singleResult();
			newBidders.add(user);
		});

		String logUserId = (String)runtimeService.getVariable(processId, "logUserId");
		UserQuery uq1 = identityService.createUserQuery().userId(logUserId);
		User user = uq1.singleResult();
		users.remove(user);
		
		runtimeService.setVariable(processId, "providers", newBidders);
		if ((boolean) runtimeService.getVariable(processId, "sendNot")) {
			for (int i = 0; i < newBidders.size(); i++) {
				auctionService.enough(users.get(i), processId, auction.getMaxOffer(), users.size(), auction);
			}
		}

	}
}
