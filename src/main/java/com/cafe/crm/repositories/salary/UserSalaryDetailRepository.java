package com.cafe.crm.repositories.salary;


import com.cafe.crm.models.shift.UserSalaryDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface UserSalaryDetailRepository extends JpaRepository<UserSalaryDetail, Long> {
	List<UserSalaryDetail> findByShiftId(Long shiftId);
	List<UserSalaryDetail> findByShiftIdAndIsPaidDetailTrue(Long shiftId);
	UserSalaryDetail findByShiftIdAndUserIdAndIsPaidDetailTrue(Long shiftId, Long userId);	//todo удалить
	UserSalaryDetail findFirstByUserIdAndShiftId(Long userId, Long shiftId);
	UserSalaryDetail findFirstByUserIdAndShiftIdAndIsPaidDetailFalse(Long userId, Long shiftId);
	List<UserSalaryDetail> findByUserIdAndShiftIdBetween(Long userId, Long from, Long to);
	List<UserSalaryDetail> findByUserIdAndShiftShiftDateBetween(Long userId, LocalDate from, LocalDate to);
	void deleteByUserIdAndShiftId (Long userId, Long shiftId);
}
