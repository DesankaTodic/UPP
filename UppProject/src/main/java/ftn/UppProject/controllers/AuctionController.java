package ftn.UppProject.controllers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.activiti.engine.FormService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ftn.UppProject.entity.Auction;
import ftn.UppProject.entity.Offer;
import ftn.UppProject.service.AuctionService;
import ftn.UppProject.service.EmailService;
import ftn.UppProject.service.OfferService;
import ftn.UppProject.service.RateService;

@Controller
public class AuctionController {

	@Autowired
	private TaskService taskService;

	@Autowired
	private FormService formService;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private AuctionService auctionService;

	@Autowired
	private OfferService offerService;
	
	@Autowired
	private RateService rateService;
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private IdentityService identityService;

	private String processIdSave;

	private String currentProvider;
	
	private String currentAuctionId;
	
	private String currentOfferId;
	
	private String clientIdForRating;

	private static final String filename = "diagrams/Auction.bpmn";

	@Autowired
	private RuntimeService runtimeService;

	@RequestMapping(value = "/startAuction", method = RequestMethod.GET)
	public String clientRegistration(ModelMap model) {

		repositoryService.createDeployment().addClasspathResource(filename).deploy();

		String processName = "myAuction";

		ProcessDefinition procDef = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processName)
				.latestVersion().singleResult();

		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processName);

		String processId = processInstance.getId();
		taskService.createTaskQuery().processInstanceId(processId).list();

		StartFormData formData = formService.getStartFormData(procDef.getId());

		List<FormProperty> formProperties = formData.getFormProperties();
		model.addAttribute("formProperties", formProperties);
		String form = formService.getStartFormData(procDef.getId()).getFormKey();
		model.addAttribute("processId", processId);
		processIdSave = processId;
		
		UserQuery uq1 = identityService.createUserQuery().userId(LoginController.loggedUserId);
		User user = uq1.singleResult();
		
		System.out.println("+++++Startovanje aukcija sa processId-om:" + processId + " i trenutno ulogovani korisnik je:" + user.getId());
		
		runtimeService.setVariable(processIdSave, "logUserId", user.getId());
		return form;
	}

	@RequestMapping(value = "/submitAuction", method = RequestMethod.POST)
	public String submitAuction(ModelMap model, @RequestParam Map<String, String> params) throws ParseException {
		String processId = params.get("processId");
		System.out.println("+++++Cuvanje aukcije");
		auctionService.saveAuction(params);
		
		runtimeService.setVariable(processId, "startover", 0);

		Task task = taskService.createTaskQuery().taskDefinitionKey("simpleTask").executionId(processId).singleResult();
		taskService.complete(task.getId());
		return "notificationAfterSubmitAuction";
	}

	@RequestMapping(value = "/ok", method = RequestMethod.GET)
	public String ok() {
		return "redirect:http://localhost:9000/goToClientHomePage";
	}

	@RequestMapping(value = "/continueAuction/{processId}", method = RequestMethod.GET)
	public String continueAuction(@PathVariable String processId) {
		System.out.println("+++++Korisnik je odlucio da nastavi s aukcijom");

		runtimeService.setVariable(processId, "continued", true);

		Task task = taskService.createTaskQuery().taskDefinitionKey("decision").executionId(processId).singleResult();
		taskService.complete(task.getId());

		return "redirect:http://localhost:9000/goToClientHomePage";
	}

	@RequestMapping(value = "/terminateAuction/{processId}", method = RequestMethod.GET)
	public String terminateAuction(@PathVariable String processId) {
		System.out.println("+++++Korisnik je odlucio da odustane");

		runtimeService.setVariable(processId, "continued", false);

		Task task = taskService.createTaskQuery().taskDefinitionKey("decision").executionId(processId).singleResult();
		taskService.complete(task.getId());

		return "redirect:http://localhost:9000/goToClientHomePage";
	}

	@RequestMapping(value = "/goToClientHomePage", method = RequestMethod.GET)
	public String goToClientHomePage(ModelMap model) {

		return "clientHomePage";
	}

	@RequestMapping(value = "/fillOffer", method = RequestMethod.GET)
	public String fillOffer(ModelMap model) {

		System.out.println("+++++Popunjavanje ponude, firma:" + currentProvider);
		
		List<Task> taskList = taskService.createTaskQuery().taskDefinitionKey("offerFill").list();
		Task task = taskList.get(0);

		TaskFormData formData = formService.getTaskFormData(task.getId());

		List<FormProperty> formProperties = formData.getFormProperties();

		model.addAttribute("formProperties", formProperties);

		model.addAttribute("processId", processIdSave);
		model.addAttribute("providerId", currentProvider);
		model.addAttribute("auctionId", currentAuctionId);

		return "fillOffer";
	}

	@RequestMapping(value = "/submitFilledOffer", method = RequestMethod.POST)
	public String submitFilledOffer(ModelMap model, @RequestParam Map<String, String> params) throws ParseException {
		System.out.println("+++++Cuvanje ponude");
		offerService.saveOffer(params);

		return "redirect:http://localhost:9000/goToClientHomePage";
	}

	@RequestMapping(value = "/fillOffer/{processId}/{providerId}/{auctionId}", method = RequestMethod.GET)
	public String fillOffer(@PathVariable String processId, @PathVariable String providerId, @PathVariable String auctionId ) {
		System.out.println("+++++Zahtjev za popunjavanje ponude");
		currentProvider = providerId;
		currentAuctionId = auctionId;
		return "redirect:http://localhost:9000/fillOffer";
	}

	@RequestMapping(value = "/cancel/{processId}/{auctionId}", method = RequestMethod.GET)
	public String cancel(@PathVariable String processId, @PathVariable String auctionId) {

		System.out.println("+++++Klijent je odustao od daljeg procesa");

		runtimeService.setVariable(processId, "procced", "cancel");

		Task task = taskService.createTaskQuery().taskDefinitionKey("decideToContinue").executionId(processId)
				.singleResult();
		taskService.complete(task.getId());

		return "redirect:http://localhost:9000/goToClientHomePage";
	}

	@RequestMapping(value = "/changeOffersDate/{processId}/{auctionId}/{newDate}", method = RequestMethod.GET)
	public String changeOffersDate(@PathVariable String processId, @PathVariable String auctionId,
			@PathVariable String newDate) throws ParseException {

		System.out.println("+++++Klijent odlucuje da produzi rok");

		runtimeService.setVariable(processId, "procced", "changeOffersDate");
		runtimeService.setVariable(processId, "sendNot", true);

		auctionService.updateAuction(auctionId, newDate, processId);

		Task task = taskService.createTaskQuery().taskDefinitionKey("decideToContinue").executionId(processId)
				.singleResult();
		taskService.complete(task.getId());

		return "redirect:http://localhost:9000/goToClientHomePage";
	}
	
	

	@RequestMapping(value = "/showOffers/{processId}/{auctionId}", method = RequestMethod.GET)
	public String showOffers(@PathVariable String processId, @PathVariable String auctionId, ModelMap model)
			throws ParseException {
		System.out.println("+++++Prikazivanje svih ponuda");

		runtimeService.setVariable(processId, "procced", "showAll");

		Auction auction = auctionService.findOne(Long.parseLong(auctionId));

		List<Offer> offers = offerService.rangOffers(auction.getMaxValue(), auction.getMaxDate(), processId);

		String rootAccept = "acceptTheOffer";
		String rootInfo = "moreInfo";

		model.addAttribute("offers", offers);
		model.addAttribute("rootAccept", rootAccept);
		model.addAttribute("rootInfo", rootInfo);

		Task task = taskService.createTaskQuery().taskDefinitionKey("decideToContinue").executionId(processId)
				.singleResult();
		taskService.complete(task.getId());

		return "/offers";
	}

	@RequestMapping(value = "/giveup/{processId}", method = RequestMethod.GET)
	public String giveup(@PathVariable String processId, ModelMap model) {
		System.out.println("+++++Odustajanje od daljeg procesa");

		runtimeService.setVariable(processId, "offerChoice", "giveUp");

		Task task = taskService.createTaskQuery().taskDefinitionKey("chooseOffer").executionId(processId)
				.singleResult();
		taskService.complete(task.getId());

		return "redirect:http://localhost:9000/goToClientHomePage";
	}

	@RequestMapping(value = "/startover/{processId}", method = RequestMethod.GET)
	public String startOver(@PathVariable String processId, ModelMap model) {
		System.out.println("+++++Pokretanje procesa nanovo");
		runtimeService.setVariable(processId, "offerChoice", "startOver");

		Task task = taskService.createTaskQuery().taskDefinitionKey("chooseOffer").executionId(processId)
				.singleResult();
		taskService.complete(task.getId());
		
		return "redirect:http://localhost:9000/goToClientHomePage";

	}

	@RequestMapping(value = "/moreInfo/{processId}/{offerId}", method = RequestMethod.GET)
	public String moreInfo(@PathVariable String processId, @PathVariable String offerId, ModelMap model) {
		System.out.println("+++++Zahtjev za vise informacija o ponudi");
		runtimeService.setVariable(processId, "offerChoice", "moreInfo");
		runtimeService.setVariable(processId, "offerId", offerId);
		Task task = taskService.createTaskQuery().taskDefinitionKey("chooseOffer").executionId(processId)
				.singleResult();
		taskService.complete(task.getId());

		return "redirect:http://localhost:9000/goToClientHomePage";

	}

	@RequestMapping(value = "/acceptTheOffer/{processId}/{offerId}", method = RequestMethod.GET)
	public String acceptTheOffer(@PathVariable String processId, @PathVariable String offerId, ModelMap model) {
		System.out.println("+++++Prihvatanje ponude ciji je id:" + offerId);
		
		runtimeService.setVariable(processId, "offerChoice", "acceptOffer");
		
		runtimeService.setVariable(processId, "offerId", offerId);

		Task task = taskService.createTaskQuery().taskDefinitionKey("chooseOffer").executionId(processId)
				.singleResult();
		taskService.complete(task.getId());

		return "redirect:http://localhost:9000/goToClientHomePage";

	}
	
	@RequestMapping(value = "/insertNewMaxDate/{processId}/{auctionId}/{newDate}", method = RequestMethod.GET)
	public String insertNewMaxDate(@PathVariable String processId, @PathVariable String auctionId,
			@PathVariable String newDate) throws ParseException {

		System.out.println("+++++Novi rok za aukciju nakon pocinjanja nanovo");

		auctionService.updateAuction(auctionId, newDate, processId);

		Task task = taskService.createTaskQuery().taskDefinitionKey("insertNewDate").executionId(processId)
				.singleResult();
		taskService.complete(task.getId());

		return "redirect:http://localhost:9000/goToClientHomePage";
	}
	
	@RequestMapping(value = "/acceptRequest/{processId}/{auctionId}/{offerId}/{newDate}", method = RequestMethod.GET)
	public String acceptRequest(@PathVariable String processId, @PathVariable String auctionId,@PathVariable String offerId,
			@PathVariable String newDate) throws ParseException {

		System.out.println("+++++Prihvatanje dogovora aukcija-ponuda, i postavljanje roka zavrsetka");
		
		Offer offer = offerService.findOne(Long.valueOf(offerId));
		
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
		Date date = format.parse(newDate);
		
		if(offer != null){
			offer.setDate(date);
		}
		
		offerService.updateOffer(offer);
		
		currentProvider = offer.getProviderId();
		
		UserQuery uq1 = identityService.createUserQuery().userId(currentProvider);
		User user = uq1.singleResult();
		Auction auction = auctionService.findOne(Long.valueOf(auctionId));

		emailService.sendEmail("Ocjena vlasnika aukcije",
				"Postovani , molimo vas da ocjenite vlasnika aukcije. \n"+
						"http://localhost:9000/rateClient/"+processId+"/"+auction.getInitiatorId(), user.getEmail());
		
		return "redirect:http://localhost:9000/rateCompany";
	}
	
	@RequestMapping(value = "/rateCompany", method = RequestMethod.GET)
	public String rateCompany(ModelMap model) {
		System.out.println("+++++Zahtjev za ocjenjivanje firme:" + currentProvider);
		
		model.addAttribute("providerId", currentProvider);
		model.addAttribute("processId", processIdSave);

		return "ratingCompany";
	}
	
	@RequestMapping(value = "/submitRatingCompany", method = RequestMethod.POST)
	public String submitRating(ModelMap model, @RequestParam Map<String, String> params) throws ParseException {
		System.out.println("+++++Ocjenjivanje firme");
		
		String providerId = params.get("providerId");
		String rate = params.get("rate");
		String processId = params.get("processId");
				
		rateService.saveRate(providerId, Integer.parseInt(rate));
		
		Task task = taskService.createTaskQuery().taskDefinitionKey("ratings").executionId(processId).singleResult();
		
		if(task != null)
			taskService.complete(task.getId());

		return "success";
	}
	
	@RequestMapping(value = "/rateClient/{processId}/{clientId}", method = RequestMethod.GET)
	public String rateClient(@PathVariable String processId, @PathVariable String clientId, ModelMap model) {
		System.out.println("+++++Zahtjev za ocjenjivanje clienta:" + clientId);
		
		clientIdForRating = clientId;
		processIdSave = processId;

		return "redirect:http://localhost:9000/ratingClient";
	}
	
	@RequestMapping(value = "/ratingClient", method = RequestMethod.GET)
	public String ratingClient(ModelMap model) {
		System.out.println("+++++Prosledjivanje zahtjeva za ocjenjivanje klijenta");
		
		model.addAttribute("clientId", clientIdForRating);
		model.addAttribute("processId", processIdSave);
		
		return "ratingClient";
	}
	
	@RequestMapping(value = "/submitRatingClient", method = RequestMethod.POST)
	public String submitRatingClient(ModelMap model, @RequestParam Map<String, String> params) throws ParseException {
		System.out.println("+++++Ocjenjivanje klijenta");
		
		String clientId = params.get("clientId");
		String rate = params.get("rate");
		String processId = params.get("processId");
				
		rateService.saveRate(clientId, Integer.parseInt(rate));
		
		Task task = taskService.createTaskQuery().taskDefinitionKey("ratings").executionId(processId).singleResult();
		
		if(task != null)
			taskService.complete(task.getId());

		return "success";
	}
	
	@RequestMapping(value = "/addingMoreInfo/{processId}/{auctionId}/{offerId}", method = RequestMethod.GET)
	public String addingMoreInfo(@PathVariable String processId, @PathVariable String auctionId,@PathVariable String offerId) {

		System.out.println("+++++Prosledjivanje zahtjeva za vise informacija, ponuda:" + offerId);
		
		processIdSave = processId;
		currentAuctionId = auctionId;
		currentOfferId = offerId;
		
		return "redirect:http://localhost:9000/addMoreInfo";
	}
	
	@RequestMapping(value = "/addMoreInfo", method = RequestMethod.GET)
	public String addMoreInfo(ModelMap model) {
		System.out.println("+++++Dalje prosledjivanje zahtjeva za vise informacija");
		model.addAttribute("auctionId", currentAuctionId);
		model.addAttribute("processId", processIdSave);
		model.addAttribute("offerId", currentOfferId);
		
		return "addingMoreInfo";
	}
	
	@RequestMapping(value = "/submitAddingInfo", method = RequestMethod.POST)
	public String submitAddingInfo(ModelMap model, @RequestParam Map<String, String> params){
		
		String offerId = params.get("offerId");
		String info = params.get("info");
		String processId = params.get("processId");
		String auctionId = params.get("auctionId");
		
		System.out.println("+++++Prosledjivanje informacija ponudjacu:" + info);

		Auction auction = auctionService.findOne(Long.valueOf(auctionId));
		
		UserQuery uq1 = identityService.createUserQuery().userId(auction.getInitiatorId());
		User user = uq1.singleResult();
		
		emailService.sendEmail("Nove informacije o ponudi",
				"Postovani, ponudjac je dodao nove informacije o ponudi. \n"+
				"Sazetak: "+ info+" \n"+
				"Ako zelite da prihvatite, kliknite na ovaj link: http://localhost:9000/acceptOfferAfterInfo/" + processId+ "/" + auctionId + "/" + offerId+" \n"+
				"Ukoliko ponovo zelite da vidite ponude, kliknite ovdje: http://localhost:9000/showOffersAfterInfo/" + processId + "/" + auctionId, 
				user.getEmail());

		return "redirect:http://localhost:9000/goToClientHomePage";
	}
	
	@RequestMapping(value = "/acceptOfferAfterInfo/{processId}/{auctionId}/{offerId}", method = RequestMethod.GET)
	public String acceptOfferAfterInfo(@PathVariable String processId, @PathVariable String auctionId,@PathVariable String offerId, ModelMap model) {
		System.out.println("+++++Zahtjev za prihvatanje ponude nakon dobijanja vise informacija, ponuda:" + offerId);
		
		runtimeService.setVariable(processId, "acceptOffer", true);
		
		runtimeService.setVariable(processId, "offerId", offerId);
		
		runtimeService.setVariable(processId, "auctionId", auctionId);
		
		runtimeService.setVariable(processId, "processId", processId);
		
		Task task = taskService.createTaskQuery().taskDefinitionKey("moreInfo").executionId(processId)
				.singleResult();
		taskService.complete(task.getId());

		return "redirect:http://localhost:9000/goToClientHomePage";
	}
	
	@RequestMapping(value = "/showOffersAfterInfo/{processId}/{auctionId}", method = RequestMethod.GET)
	public String showOffersAfterInfo(@PathVariable String processId, @PathVariable String auctionId, ModelMap model)
			throws ParseException {
		System.out.println("+++++Zahtjev za ponovni prokaz ponuda");
		
		runtimeService.setVariable(processId, "acceptOffer", false);

		Auction auction = auctionService.findOne(Long.parseLong(auctionId));

		List<Offer> offers = offerService.rangOffers(auction.getMaxValue(), auction.getMaxDate(), processId);

		String rootAccept = "acceptTheOffer";
		String rootInfo = "moreInfo";

		model.addAttribute("offers", offers);
		model.addAttribute("rootAccept", rootAccept);
		model.addAttribute("rootInfo", rootInfo);

		Task task = taskService.createTaskQuery().taskDefinitionKey("moreInfo").executionId(processId)
				.singleResult();
		taskService.complete(task.getId());

		return "/offers";
	}


}
