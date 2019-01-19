package ftn.UppProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ftn.UppProject.entity.Rate;
import ftn.UppProject.repository.IRateRepository;

@Service
public class RateService {

	@Autowired
	private IRateRepository rateRepository;
	
	public void saveRate(String userId, int rate){
		System.out.println("+++++Servis cuvanje ocjene");

		Rate r = new Rate();
		
		r.setUserId(userId);
		r.setRate(rate);
		
		rateRepository.save(r);
	}
}
