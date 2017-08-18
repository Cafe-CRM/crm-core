package com.cafe.crm.services.interfaces.shift;

import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.models.shift.ShiftView;
import com.cafe.crm.models.worker.Worker;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;


public interface ShiftService {

	void saveAndFlush(Shift shift);

	Shift newShift(long[] box, Double cashBox, Double bankCashBox);

	Shift findOne(Long L);

	List<Worker> findAllUsers();

	// Рабочие не добавленные в открытую  смену
	List<Worker> getWorkers();

	// Рабочие добавленные в открытую  смену
	Set<Worker> getAllActiveWorkers();

	void deleteWorkerFromShift(String name);

	void addWorkerFromShift(String name);

	Shift getLast();

	List<Shift> findAll();

	void closeShift(Double totalCashBox, Map<Long, Long> workerIdBonusMap, Double allPrice, Double shortage,
					Double bankCashBox);

	Set<Shift> findByDates(LocalDate start, LocalDate end);

	ShiftView createShiftView(Shift shift);

	Shift findByDateShift(LocalDate date);

	void transferFromCard(Double transfer);
	void transferFromCasse (Double transfer);


}
