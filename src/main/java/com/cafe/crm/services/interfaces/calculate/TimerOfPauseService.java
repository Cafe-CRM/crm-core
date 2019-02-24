package com.cafe.crm.services.interfaces.calculate;


import com.cafe.crm.models.client.TimerOfPause;

import java.time.LocalDate;
import java.util.List;

public interface TimerOfPauseService {

	TimerOfPause getOne(Long id);

	void save(TimerOfPause timer);

	TimerOfPause findTimerOfPauseByIdOfClient(Long id);

	List<TimerOfPause> findByDates(LocalDate start, LocalDate end);
}
