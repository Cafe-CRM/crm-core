package com.cafe.crm.services.interfaces.salary;


import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.models.shift.UserSalaryDetail;

import java.time.LocalDate;
import java.util.List;

public interface UserSalaryDetailService {
	void save(UserSalaryDetail userSalaryDetail);

	void save(List<UserSalaryDetail> userSalaryDetails);

	List<UserSalaryDetail> findByShiftId(Long shiftId);

	List<UserSalaryDetail> findPaidDetailsByShiftId(Long shiftId);

	List<UserSalaryDetail> findOtherDetailsByShiftId(Long shiftId);

	UserSalaryDetail findPaidDetailsByUserIdAndShiftId(Long shiftId, Long userId);

	UserSalaryDetail findFirstByUserIdAndShiftId(Long userId, Long shiftId);

	UserSalaryDetail findFirstUnpaidByUserIdAndShiftId(Long userId, Long shiftId);

	List<UserSalaryDetail> findByUserIdAndShiftIdBetween(Long userId, Long from, Long to);

	List<UserSalaryDetail> findByUserIdAndShiftDateBetween(Long userId, LocalDate from, LocalDate to);

	void deleteByUserIdAndShiftId(Long userId, Long shiftId);

	List<UserSalaryDetail> findAllAdminsDetailByShift(Shift shift);

	List<UserSalaryDetail> findAllWorkersDetailByShift(Shift shift);

	UserSalaryDetail findLastShiftOtherDetailByUser(long shiftId, long userId);

	void deleteAllByShiftId(Long shiftId);

	void deleteAllByShiftIdIn(long[] ids);
}
