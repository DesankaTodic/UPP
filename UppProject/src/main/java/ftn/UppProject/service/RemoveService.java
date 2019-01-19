package ftn.UppProject.service;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class RemoveService implements JavaDelegate  {

	@Override
	public void execute(DelegateExecution execution) throws Exception {
		System.out.println("+++++Servis uklanjanje nepotvrdjenih registracija");
		execution.getEngineServices().getIdentityService().deleteUser(execution.getVariable("username").toString());
	}

}
