package com.cafe.crm.services.impl.calculate;

import com.cafe.crm.models.client.TimerOfPause;
import com.cafe.crm.models.company.Company;
import com.cafe.crm.repositories.calculate.TimerOfPauseRepository;
import com.cafe.crm.services.interfaces.calculate.TimerOfPauseService;
import com.cafe.crm.services.interfaces.company.CompanyService;
import com.cafe.crm.utils.CompanyIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TimerOfPauseServiceImpl implements TimerOfPauseService {

	private final TimerOfPauseRepository timerOfPauseRepository;
	private final CompanyService companyService;
	private final CompanyIdCache companyIdCache;

	@Autowired
	public TimerOfPauseServiceImpl(TimerOfPauseRepository timerOfPauseRepository, CompanyService companyService, CompanyIdCache companyIdCache) {
		this.companyService = companyService;
		this.timerOfPauseRepository = timerOfPauseRepository;
		this.companyIdCache = companyIdCache;
	}

	private void setCompany(TimerOfPause timerOfPause) {
		Long companyId = companyIdCache.getCompanyId();
		Company company = companyService.findOne(companyId);
		timerOfPause.setCompany(company);
	}

	@Override
	public TimerOfPause getOne(Long id) {
		return timerOfPauseRepository.getOne(id);
	}

	@Override
	public void save(TimerOfPause timer) {
		setCompany(timer);
		timerOfPauseRepository.saveAndFlush(timer);
	}

	@Override
	public TimerOfPause findTimerOfPauseByIdOfClient(Long id) {
		return timerOfPauseRepository.findTimerOfPauseByIdOfClient(id);
	}
}
