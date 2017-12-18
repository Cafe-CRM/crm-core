package com.cafe.crm.services.interfaces.salary;


import com.cafe.crm.models.shift.UserSalaryDetail;

import java.time.LocalDate;
import java.util.List;

public interface UserSalaryDetailService {
	void save(UserSalaryDetail userSalaryDetail);
	void save(List<UserSalaryDetail> userSalaryDetails);
	List<UserSalaryDetail> findByShiftId(Long shiftId);
	UserSalaryDetail findFirstByUserIdAndShiftId(Long userId, Long shiftId);
	List<UserSalaryDetail> findByUserIdAndShiftIdBetween(Long userId, Long from, Long to);
	List<UserSalaryDetail> findByUserIdAndShiftDateBetween(Long userId, LocalDate from, LocalDate to);
	void deleteByUserIdAndShiftId(Long userId, Long shiftId);
}
