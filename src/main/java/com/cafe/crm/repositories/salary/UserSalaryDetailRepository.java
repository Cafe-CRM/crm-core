package com.cafe.crm.repositories.salary;


import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.models.shift.UserSalaryDetail;
import com.cafe.crm.repositories.customRepository.CommonRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface UserSalaryDetailRepository extends JpaRepository<UserSalaryDetail, Long> {
	List<UserSalaryDetail> findByShiftId(Long shiftId);

	List<UserSalaryDetail> findByShiftIdAndIsPaidDetailTrue(Long shiftId);

	List<UserSalaryDetail> findByShiftIdAndIsPaidDetailFalse(Long shiftId);

	UserSalaryDetail findByShiftIdAndUserIdAndIsPaidDetailTrue(Long shiftId, Long userId);	//todo удалить

	UserSalaryDetail findFirstByUserIdAndShiftId(Long userId, Long shiftId);

	UserSalaryDetail findFirstByUserIdAndShiftIdAndIsPaidDetailFalse(Long userId, Long shiftId);

	List<UserSalaryDetail> findByUserIdAndShiftIdBetween(Long userId, Long from, Long to);

	List<UserSalaryDetail> findByUserIdAndShiftShiftDateBetween(Long userId, LocalDate from, LocalDate to);

	void deleteByUserIdAndShiftId (Long userId, Long shiftId);

	void deleteAllByShiftId(Long shiftId);

	void deleteAllByShiftIdIn(long[] ids);

	List<UserSalaryDetail> findByShiftAndUserPositionsId(Shift shift, Long positionId);

	List<UserSalaryDetail> findByShiftAndUserPositionsIdIsNot(Shift shift, Long positionId);

    @Query("SELECT u FROM UserSalaryDetail u WHERE u.id = (SELECT max(id) FROM UserSalaryDetail usd WHERE usd.user.id = :userId AND usd.shift.id < :shiftId)")
	UserSalaryDetail getLastShiftDetail(@Param("shiftId") Long shiftId, @Param("userId") Long userId);
}
