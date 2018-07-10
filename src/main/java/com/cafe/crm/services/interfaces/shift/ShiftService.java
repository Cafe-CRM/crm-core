package com.cafe.crm.services.interfaces.shift;

import com.cafe.crm.models.client.Debt;
import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.dto.ShiftView;
import com.cafe.crm.models.user.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;


public interface ShiftService {

	Shift saveAndFlush(Shift shift);

	Shift createNewShiftWithAlteredCashAmount(Double cashBox, Double bankCashBox, long... usersIdsOnShift);

	Shift createNewShift(Double cashBox, Double bankCashBox, long... usersId);

	Shift createMissingShift(LocalDate date);

	Shift findOne(Long L);

	List<User> getUsersNotOnShift();

	List<User> getUsersOnShift();

	User deleteUserFromShift(Long userId);

	User addUserToShift(Long userId);

	Shift getLast();

	Shift getMissingLast();

	List<Shift> findAll();

	Shift closeShift(Map<Long, Integer> mapOfUsersIdsAndBonuses, Double allPrice, Double shortage, Double bankCashBox, String comment, Map<String, String> mapOfNoteNameAndValue);

	void deleteShifts(String password, long... shiftId);

	void deleteMissingShift(Long shiftId);

 	LocalDate getLastShiftDate();

	List<Shift> findByDates(LocalDate start, LocalDate end);

	Shift findByDate(LocalDate start);

	List<Shift> findAllByDate(LocalDate start);

	Shift getLastMissingShift();

	long countingByDate(LocalDate date);

	long countingByDateWithoutMissing(LocalDate date);

	long countingByMissing();
}
