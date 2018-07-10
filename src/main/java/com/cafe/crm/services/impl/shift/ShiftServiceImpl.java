package com.cafe.crm.services.impl.shift;

import com.cafe.crm.exceptions.password.PasswordException;
import com.cafe.crm.models.client.Debt;
import com.cafe.crm.models.company.Company;
import com.cafe.crm.models.missingModel.MissingProduct;
import com.cafe.crm.models.note.NoteRecord;
import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.models.shift.UserSalaryDetail;
import com.cafe.crm.models.user.Position;
import com.cafe.crm.models.user.User;
import com.cafe.crm.repositories.shift.ShiftRepository;
import com.cafe.crm.services.interfaces.calculation.ShiftCalculationService;
import com.cafe.crm.services.interfaces.company.CompanyService;
import com.cafe.crm.services.interfaces.cost.CostService;
import com.cafe.crm.services.interfaces.debt.DebtService;
import com.cafe.crm.services.interfaces.missing.MissingProductService;
import com.cafe.crm.services.interfaces.note.NoteRecordService;
import com.cafe.crm.services.interfaces.salary.UserSalaryDetailService;
import com.cafe.crm.services.interfaces.shift.ShiftService;
import com.cafe.crm.services.interfaces.token.ConfirmTokenService;
import com.cafe.crm.services.interfaces.user.UserService;
import com.cafe.crm.utils.CompanyIdCache;
import com.cafe.crm.utils.Target;
import com.cafe.crm.utils.TimeManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.*;

@Transactional
@Service
public class ShiftServiceImpl implements ShiftService {

	private final ShiftRepository shiftRepository;
	private final UserService userService;
	private final TimeManager timeManager;
	private final NoteRecordService noteRecordService;
	private UserSalaryDetailService userSalaryDetailService;
	private ShiftCalculationService shiftCalculationService;
	private final MissingProductService missingProductService;
	private DebtService debtService;
	private CostService costService;
	private ConfirmTokenService confirmTokenService;

    private final CompanyService companyService;
    private final CompanyIdCache companyIdCache;


	@Autowired
	public ShiftServiceImpl(TimeManager timeManager, ShiftRepository shiftRepository,
                            UserService userService,
                            NoteRecordService noteRecordService,
                            CompanyIdCache companyIdCache,
                            CompanyService companyService,
                            MissingProductService missingProductService,
                            CostService costService,
                            ConfirmTokenService confirmTokenService) {
		this.timeManager = timeManager;
		this.shiftRepository = shiftRepository;
		this.userService = userService;
		this.noteRecordService = noteRecordService;
        this.companyService = companyService;
        this.companyIdCache = companyIdCache;
        this.missingProductService = missingProductService;
        this.costService = costService;
        this.confirmTokenService = confirmTokenService;
	}

	@Autowired
	public void setUserSalaryDetailService(UserSalaryDetailService userSalaryDetailService) {
		this.userSalaryDetailService = userSalaryDetailService;
	}

	@Autowired
	public void setShiftCalculationService(ShiftCalculationService shiftCalculationService) {
		this.shiftCalculationService = shiftCalculationService;
	}

	@Autowired
    public void setDebtService(DebtService debtService) {
        this.debtService = debtService;
    }

    @Autowired
    public void setCostService(CostService costService) {
        this.costService = costService;
    }

    private void setCompany(Shift shift) {
		Long companyId = companyIdCache.getCompanyId();
		Company company = companyService.findOne(companyId);
		shift.setCompany(company);
	}

	@Override
	public Shift saveAndFlush(Shift shift) {
		setCompany(shift);
		return shiftRepository.saveAndFlush(shift);
	}

	@Override
	public Shift createNewShiftWithAlteredCashAmount(Double cashBox, Double bankCashBox, long... usersIdsOnShift) {
		Shift lastShift = getLast();
		Shift shift = createNewShift(cashBox, bankCashBox, usersIdsOnShift);

		Double currentCashAmount = cashBox + bankCashBox;
		Double lastCashAmount = lastShift.getCashBox() + lastShift.getBankCashBox();
		Double alteredCashAmount = currentCashAmount - lastCashAmount;

		shift.setAlteredCashAmount(alteredCashAmount);
		return shift;
	}

    @Override
    public Shift createNewShift(Double cashBox, Double bankCashBox, long... usersIdsOnShift) {
        List<User> users = userService.findByIdIn(usersIdsOnShift);
        Shift shift = new Shift(timeManager.getDate(), users, bankCashBox);
        shift.setOpen(true);
        for (User user : users) {
            user.getShifts().add(shift);
        }
        shift.setCashBox(cashBox);
        shift.setBankCashBox(bankCashBox);
        setCompany(shift);
        shiftRepository.saveAndFlush(shift);
        return shift;
    }

    @Override
    public Shift createMissingShift(LocalDate date) {
        Shift shift = new Shift(date);
        shift.setOpen(true);
        shift.setMissingShift(true);
        setCompany(shift);
        return shiftRepository.saveAndFlush(shift);
    }

    @Transactional(readOnly = true)
    @Override
    public Shift findOne(Long id) {
        return shiftRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getUsersNotOnShift() {
        List<User> users = userService.findAll();
        Shift lastShift = shiftRepository.getLastAndCompanyId(companyIdCache.getCompanyId());
        if (lastShift != null) {
            users.removeAll(lastShift.getUsers());
        }
        return users;
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> getUsersOnShift() {
        Shift lastShift = shiftRepository.getLastAndCompanyId(companyIdCache.getCompanyId());
        if (lastShift != null) {
            return lastShift.getUsers();
        }
        return new ArrayList<>();
    }

    @Override
    public User deleteUserFromShift(Long userId) {
        Shift shift = shiftRepository.getLastAndCompanyId(companyIdCache.getCompanyId());
        User user = userService.findById(userId);
        shift.getUsers().remove(user);
        user.getShifts().remove(shift);
        userSalaryDetailService.deleteByUserIdAndShiftId(userId, shift.getId());
        shiftRepository.saveAndFlush(shift);
        return user;
    }

    @Override
    public User addUserToShift(Long userId) {
        Shift shift = shiftRepository.getLastAndCompanyId(companyIdCache.getCompanyId());
        User user = userService.findById(userId);
        shift.getUsers().add(user);
        user.getShifts().add(shift);
        shiftRepository.saveAndFlush(shift);
        return user;
    }

    @Transactional(readOnly = true)
    @Override
    public Shift getLast() {
        return shiftRepository.getLastAndCompanyId(companyIdCache.getCompanyId());
    }

    @Transactional(readOnly = true)
    @Override
    public Shift getMissingLast() {
        return shiftRepository.getLastMissingShift(companyIdCache.getCompanyId());
    }

    @Transactional(readOnly = true)
    @Override
    public List<Shift> findAll() {
        //return shiftRepository.findByCompanyId(companyIdCache.getCompanyId());
        return shiftRepository.findAll();
    }


	@Transactional
	@Override
	public Shift closeShift(Map<Long, Integer> mapOfUsersIdsAndBonuses, Double allPrice, Double cashBox, Double bankCashBox, String comment, Map<String, String> mapOfNoteNameAndValue) {
		Shift shift = shiftRepository.getLastAndCompanyId(companyIdCache.getCompanyId());
		List<User> usersOnShift = shift.getUsers();
		List<UserSalaryDetail> userSalaryDetails = new ArrayList<>();
		for (Map.Entry<Long, Integer> entry : mapOfUsersIdsAndBonuses.entrySet()) {
			User user = userService.findById(entry.getKey());
			user.setSalaryBalance(user.getSalaryBalance() + user.getShiftSalary());
			user.setBonusBalance(user.getBonusBalance() + entry.getValue());
			userService.save(user);
		}

		for (User user : usersOnShift) {
			List<UserSalaryDetail> salaryDetailList = user.getUserSalaryDetail();
			int bonus = mapOfUsersIdsAndBonuses.get(user.getId());
			int amountOfPositionsPercent = user.getPositions().stream().filter(Position::isPositionUsePercentOfSales).mapToInt(Position::getPercentageOfSales).sum();
			int percent = (int) (allPrice * amountOfPositionsPercent) / 100;
			int shiftAmount = user.getShiftAmount() + 1;
			user.setShiftAmount(shiftAmount);

			user.setSalaryBalance(user.getSalaryBalance() + percent);
			UserSalaryDetail salaryDetail = shiftCalculationService.getUserSalaryDetail(user, percent, bonus, shift);
			if (salaryDetailList == null) {
				salaryDetailList = new ArrayList<>();
			}
			salaryDetailList.add(salaryDetail);
			userSalaryDetails.add(salaryDetail);
			user.setUserSalaryDetail(salaryDetailList);
		}
		List<NoteRecord> noteRecords = saveAndGetNoteRecords(mapOfNoteNameAndValue, shift);
		shift.setBankCashBox(bankCashBox);
		shift.setCashBox(cashBox);
		shift.setProfit(allPrice);
		shift.setComment(comment);
		shift.setNoteRecords(noteRecords);
		shift.setOpen(false);
		shiftRepository.saveAndFlush(shift);
		userSalaryDetailService.save(userSalaryDetails);
		userService.save(usersOnShift);
		return shift;
	}

    @Override
	public void deleteShifts(String password, long... shiftId) {

        if (password.equals("")) {
            throw new PasswordException("Заполните поле пароля перед отправкой!");
        }
        if (!confirmTokenService.confirm(password, Target.DELETE_SHIFT)) {
            throw new PasswordException("Пароль не действителен!");
        }

	    List<Shift> shifts = shiftRepository.findByIdIn(shiftId);
	    missingProductService.deleteByShiftIdIn(shiftId);

        /*Debts*/
	    debtService.deleteByGivenShiftIdIn(shiftId);
	    List<Debt> repaidDebts = debtService.findRepaidDebtsByShiftIdIn(shiftId);
	    List<Debt> deletedDebts = debtService.findAllDeletedDebtsByShiftIdIn(shiftId);
	    for (Debt debt : repaidDebts) {
	        debt.setRepaired(false);
	        debt.setRepaidDate(null);
	        debt.setRepairedShift(null);
        }
        for (Debt debt : deletedDebts) {
	        debt.setDeleted(false);
	        debt.setDeletedShift(null);
        }

        /*Costs*/
        costService.deleteAllCostByShiftIdIn(shiftId);

        /*Users*/
	    Set<User> usersOnShift = new HashSet<>();

	    for (Shift shift : shifts) {

	        Set<User> users = new HashSet<>(shift.getUsers());

            for (User user : users) {
                user.getShifts().remove(shift);
                setSalaryDate(user, shift);
            }
            userSalaryDetailService.deleteAllByShiftId(shift.getId());
            usersOnShift.addAll(users);

        }

        userSalaryDetailService.deleteAllByShiftIdIn(shiftId);

        userService.save(usersOnShift);

	    shiftRepository.deleteAllByIdIn(shiftId);
    }

    @Override
    public void deleteMissingShift(Long shiftId) {
        Shift shift = shiftRepository.findById(shiftId);
        missingProductService.deleteByShift(shift);

        /*Debts*/
        debtService.deleteByGivenShift(shift);
        List<Debt> repaidDebts = debtService.findRepaidDebtsByShift(shift);
        List<Debt> deletedDebts = debtService.findAllDeletedDebtsByShift(shift);
        for (Debt debt : repaidDebts) {
            debt.setRepaired(false);
            debt.setRepaidDate(null);
            debt.setRepairedShift(null);
        }
        for (Debt debt : deletedDebts) {
            debt.setDeleted(false);
            debt.setDeletedShift(null);
        }

        /*Costs*/
        costService.deleteAllCostByShift(shift);

        /*Users*/
        Set<User> usersOnShift = new HashSet<>(shift.getUsers());

        /*Salary details*/
        for (User user : usersOnShift) {
            user.getShifts().remove(shift);
            setSalaryDate(user, shift);
        }

        userSalaryDetailService.deleteAllByShiftId(shiftId);

        userService.save(usersOnShift);

        shiftRepository.delete(shiftId);
    }

    private void setSalaryDate(User user, Shift shift) {
	    long shiftId = shift.getId();
        UserSalaryDetail detail = null;
        try {
            detail = userSalaryDetailService.findLastShiftOtherDetailByUser(shiftId, user.getId());
        } catch (EntityNotFoundException e) {

        }

        if (detail != null) {
            user.setBonusBalance(detail.getBonusBalance());
            user.setSalaryBalance(detail.getSalaryBalance());
            user.setTotalBonus(detail.getTotalBonus());
            user.setTotalSalary(detail.getTotalSalary());
        } else {
            user.setBonusBalance(0);
            user.setSalaryBalance(0);
            user.setTotalBonus(0);
            user.setTotalSalary(0);
        }

        int shiftAmount = user.getShiftAmount() - 1;
        user.setShiftAmount(shiftAmount);
    }

    private List<NoteRecord> saveAndGetNoteRecords(Map<String, String> mapOfNoteNameAndValue, Shift shift) {
        List<NoteRecord> noteRecords = new ArrayList<>();
        if (mapOfNoteNameAndValue != null) {
            for (Map.Entry<String, String> noteNameAndValue : mapOfNoteNameAndValue.entrySet()) {
                NoteRecord noteRecord = new NoteRecord();
                noteRecord.setName(noteNameAndValue.getKey());
                noteRecord.setValue(noteNameAndValue.getValue());
                noteRecord.setShift(shift);
                NoteRecord savedNoteRecord = noteRecordService.save(noteRecord);
                noteRecords.add(savedNoteRecord);
            }
        }
        return noteRecords;
    }


    @Transactional(readOnly = true)
    @Override
    public List<Shift> findByDates(LocalDate start, LocalDate end) {
        return new ArrayList<>(shiftRepository.findByDatesAndCompanyId(start, end, companyIdCache.getCompanyId()));
        //return shiftRepository.findAllBySelfDateBetween(start, end);
    }

    @Transactional(readOnly = true)
    @Override
    public Shift findByDate(LocalDate date) {
        return shiftRepository.findByShiftDateAndCompanyId(date, companyIdCache.getCompanyId());
        //return shiftRepository.findBySelfDate(date);
    }

    @Transactional(readOnly = true)
    @Override
    public long countingByDateWithoutMissing(LocalDate date) {
        return shiftRepository.countAllByShiftDateAndMissingShiftIsFalseAndCompanyId(date, companyIdCache.getCompanyId());
        //return shiftRepository.findBySelfDate(date);
    }

    @Override
    public long countingByMissing() {
        return shiftRepository.countAllByMissingShiftIsTrueAndOpenedIsTrue();
    }

    @Override
    public List<Shift> findAllByDate(LocalDate start) {
        return shiftRepository.findAllByShiftDateAndCompanyId(start, companyIdCache.getCompanyId());
    }

    @Override
    public Shift getLastMissingShift() {
        return shiftRepository.getLastMissingShift(companyIdCache.getCompanyId());
    }

    @Override
    public long countingByDate(LocalDate date) {
        return shiftRepository.countAllByShiftDate(date);
    }

    @Override
    public LocalDate getLastShiftDate() {
        Shift lastShift = shiftRepository.getLastAndCompanyId(companyIdCache.getCompanyId());
        if (lastShift != null) {
            return lastShift.getShiftDate();
        } else {
            return timeManager.getDate();
        }
    }
}
