package com.cafe.crm.repositories.calculate;


import com.cafe.crm.models.client.TimerOfPause;
import com.cafe.crm.repositories.customRepository.CommonRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimerOfPauseRepository extends JpaRepository<TimerOfPause, Long> {

	TimerOfPause findTimerOfPauseByIdOfClient(Long id);

}
