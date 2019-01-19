package ftn.UppProject.controllers;

import java.util.List;
import java.util.Map;

import org.activiti.engine.FormService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.FormProperty;
import org.activiti.engine.form.StartFormData;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ftn.UppProject.service.EmailService;

@Controller
public class RegistrationController {
	
	@Autowired
	private TaskService taskService;

	@Autowired
	private FormService formService;

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private IdentityService identityService;

	private static final String filename = "diagrams/Registration.bpmn";

	@Autowired
	private RuntimeService runtimeService;
	
	@Autowired
	private EmailService emailService;


	@RequestMapping(value = "/registration", method = RequestMethod.GET)
	public String registration(ModelMap model) {
		System.out.println("+++++Zahtjev za registraciju");
		repositoryService.createDeployment().addClasspathResource(filename).deploy();
		
		String processName = "registration";

		ProcessDefinition procDef = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey(processName).latestVersion().singleResult();

		StartFormData formData = formService.getStartFormData(procDef.getId());

		List<FormProperty> formProperties = formData.getFormProperties();

		if (formProperties.size() == 0) {
			runtimeService.startProcessInstanceByKey(processName);
			String message = "Nova instanca je uspe�no pokrenuta";
			model.addAttribute("message", message);
			
			return processName;
		} else {
			model.addAttribute("formProperties", formProperties);
			String form = formService.getStartFormData(procDef.getId()).getFormKey();

			return  form;
		}
	}
	
	@RequestMapping(value = "/submitRegistartion", method = RequestMethod.POST)
	public String submitRegistartion(ModelMap model, @RequestParam Map<String, String> requestParams) {		
		String type = requestParams.get("clientType");
		String username = requestParams.get("username");
		String email = requestParams.get("email");
		String firstname = requestParams.get("firstname");
		String lastname = requestParams.get("lastname");
		String password = requestParams.get("password");
		System.out.println("+++++Cuvanje novog korisnika:" + username);

		GroupQuery gq = identityService.createGroupQuery().groupId(type);
		Group group = null;
		if (gq.singleResult() == null) {
			group = identityService.newGroup(type);
		} else {
			group = gq.singleResult();
		}
		group.setName(type);
		identityService.saveGroup(group);
		
		UserQuery uq1 = identityService.createUserQuery().userId(username);
		User user = null;
		if (uq1.singleResult() == null) {
//			UserQuery uq2 = identityService.createUserQuery().userEmail(email);
//			if(uq2.count() == 0) {
				user = identityService.newUser(username);
				user.setEmail(email);
				user.setFirstName(firstname);
				user.setLastName(lastname);
				user.setPassword(password);
				
				identityService.saveUser(user);
//			}else{
//				return "validationFailed";
//			}
		} else {
			return "validationFailed";
		}
		
		ProcessDefinition procDef = repositoryService.createProcessDefinitionQuery()
				.processDefinitionKey("registration").latestVersion().singleResult();

		identityService.createMembership(username, type);
		
		ProcessInstance procInst = formService.submitStartFormData(procDef.getId(), requestParams);
		model.addAttribute("processId", procInst.getProcessInstanceId());
		model.addAttribute("username", username);
		runtimeService.setVariable(procInst.getId(), "username", username);

		if(type.equals("legal")) {
			String deploymentId = procDef.getDeploymentId();

			ProcessInstanceQuery processInstQuery = runtimeService.createProcessInstanceQuery().deploymentId(deploymentId);

			ProcessInstance pi = processInstQuery.singleResult();

			List<Task> taskList = taskService.createTaskQuery().processInstanceId(pi.getId()).list();

			Task task = taskList.get(0);

			TaskFormData formData = formService.getTaskFormData(task.getId());

			List<FormProperty> formProperties = formData.getFormProperties();

			model.addAttribute("formProperties", formProperties);
			formService.getStartFormData(procDef.getId()).getFormKey();
			return "choosingCategory";
		} else {
			emailService.sendEmail("Registracija", "Postovani, \n " + user.getLastName()
			+ " molimo vas da potvrdite registraciju: http://localhost:9000/confirmEmail/"
			+  procInst.getId()+"/"+username, user.getEmail());
			
			return "mailNotification";
		}
		
	}
		
		@RequestMapping(value = "/submitCategory", method = RequestMethod.POST)
		public String submitCategory(ModelMap model, @RequestParam Map<String, String> requestParams) {
			String category = requestParams.get("companyCategory");
			System.out.println("+++++Cuvanje kategorije firme pravnog lica, kategorija: " + category);

			GroupQuery gq = identityService.createGroupQuery().groupId(category);
			Group group = null;
			if (gq.singleResult() == null) {
				group = identityService.newGroup(category);
			} else {
				group = gq.singleResult();
			}
			group.setName(category);
			identityService.saveGroup(group);
			
			identityService.createMembership(requestParams.get("username"), category);

			String processId = requestParams.get("processId");
			String username = requestParams.get("username");
			UserQuery uq = identityService.createUserQuery().userId(username);
			User user = uq.singleResult();

			ProcessInstanceQuery piq = runtimeService.createProcessInstanceQuery().processInstanceId(processId);

			ProcessInstance pi = piq.singleResult();

			List<Task> taskList = taskService.createTaskQuery().processInstanceId(pi.getId()).list();

			Task task = taskList.get(0);


			formService.submitTaskFormData(task.getId(), requestParams);
			
			emailService.sendEmail("Registracija", "Postovani, \n " + user.getLastName()
			+ " molimo vas da potvrdite registraciju: http://localhost:9000/confirmEmail/"
			+  pi.getId()+"/"+username, user.getEmail());
			
			return "mailNotification";
		}
		
		@RequestMapping(value = "/confirmEmail/{processId}/{id}", method = RequestMethod.GET)
		public String confirm(@PathVariable String processId, @PathVariable String id) {

			System.out.println("+++++Potvrda registracije:" + id);

			ProcessInstanceQuery piq = runtimeService.createProcessInstanceQuery().processInstanceId(processId);

			ProcessInstance pi = piq.singleResult();
			if(pi == null){
				return "redirect:http://localhost:9000/verificationFailed";
			}

			List<Task> taskList = taskService.createTaskQuery().processInstanceId(pi.getId()).list();

			Task task = taskList.get(0);

			taskService.complete(task.getId());

			return "redirect:http://localhost:9000/verification";
		}
		
		@RequestMapping(value = "/verification", method = RequestMethod.GET)
		public String verification(ModelMap model) {

			return "verificationPage";
		}
		
		@RequestMapping(value = "/verificationFailed", method = RequestMethod.GET)
		public String verificationFailed(ModelMap model) {

			return "verificationFailedPage";
		}
}
