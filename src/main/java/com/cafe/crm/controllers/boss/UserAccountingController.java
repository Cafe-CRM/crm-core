package com.cafe.crm.controllers.boss;

import com.cafe.crm.dto.ExtraUserData;
import com.cafe.crm.dto.PositionDTO;
import com.cafe.crm.dto.RoleDTO;
import com.cafe.crm.dto.UserLoggingDTO;
import com.cafe.crm.exceptions.user.PositionDataException;
import com.cafe.crm.exceptions.user.UserDataException;
import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.models.user.Position;
import com.cafe.crm.models.user.Role;
import com.cafe.crm.models.user.User;
import com.cafe.crm.services.interfaces.calculation.ShiftCalculationService;
import com.cafe.crm.services.interfaces.position.PositionService;
import com.cafe.crm.services.interfaces.role.RoleService;
import com.cafe.crm.services.interfaces.shift.ShiftService;
import com.cafe.crm.services.interfaces.user.UserService;
import com.yc.easytransformer.Transformer;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class UserAccountingController {

	private final UserService userService;
	private final PositionService positionService;
	private final RoleService roleService;
	private final ShiftCalculationService shiftCalculationService;
	private final Transformer transformer;

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(UserAccountingController.class);

	@Autowired
	public UserAccountingController(UserService userService, PositionService positionService, RoleService roleService,
									ShiftCalculationService shiftCalculationService, Transformer transformer) {
		this.userService = userService;
		this.positionService = positionService;
		this.roleService = roleService;
		this.shiftCalculationService = shiftCalculationService;
		this.transformer = transformer;
	}

	@RequestMapping(value = {"/boss/user/accounting"}, method = RequestMethod.GET)
	public String showAllUser(Model model) {
		List<User> allUsers = userService.findAll();
		Map<Position, List<User>> usersByPositions = userService.findAndSortUserByPosition();
		List<Position> allPositions = positionService.findAll();
		List<Role> allRoles = roleService.findAll();

		model.addAttribute("allUsers", allUsers);
		model.addAttribute("usersByPositions", usersByPositions);
		model.addAttribute("allPositions", allPositions);
		model.addAttribute("allRoles", allRoles);

		return "userAccounting/userAccounting";
	}

	@RequestMapping(value = {"/boss/user/add"}, method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> addUser(@ModelAttribute @Valid User user, BindingResult bindingResult,
									 @RequestParam(name = "positionsIds") String positionsIds,
									 @RequestParam(name = "rolesIds") String rolesIds,
									 @RequestParam(name = "isDefaultPassword", required = false) String isDefaultPassword) {
		if (bindingResult.hasErrors()) {
			String fieldError = bindingResult.getFieldError().getDefaultMessage();
			throw new UserDataException("Не удалось добавить пользователя!\n" + fieldError);
		}
		userService.save(user, positionsIds, rolesIds, isDefaultPassword);

		logger.info("Добавлен пользователь с именем: " + user.getFirstName() + " " + user.getLastName() +
				"\n email: " + user.getEmail() +
				"\n телефон: " + user.getPhone());

		return ResponseEntity.ok("Пользователь успешно обновлен!");
	}

	@RequestMapping(value = {"/boss/user/edit"}, method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> editUser(@ModelAttribute @Valid User user, BindingResult bindingResult,
									  ExtraUserData extraUserData) throws IllegalAccessException {
		if (bindingResult.hasErrors()) {
			String fieldError = bindingResult.getFieldError().getDefaultMessage();
			throw new UserDataException("Не удалось изменить данные пользователя!\n" + fieldError);
		}

		UserLoggingDTO existedUser = userService.transformUserToLogDTO(userService.findById(user.getId()));

		userService.update(user, extraUserData);

		logger.info("Пользователь с id: " + user.getId() + " был изменён." +
				"\n Имя: " + existedUser.getFirstName() + " -> " + user.getFirstName() +
				"\n Фамилия: " + existedUser.getLastName() + " -> " + user.getLastName() +
				"\n Телефон: " + existedUser.getPhone() + " -> " + user.getPhone() +
				"\n email: " + existedUser.getEmail() + " -> " + user.getEmail() +
				"\n Зарплата за смену: " + existedUser.getShiftSalary() + " -> " + user.getShiftSalary() +
				"\n Должность: " + existedUser.getPositions() + " -> " + user.getPositions() +
				"\n Роль: " + existedUser.getRoles() + " -> " + user.getRoles());

		return ResponseEntity.ok("Пользователь успешно обновлен!");
	}

	@RequestMapping(value = {"/boss/user/delete/{id}"}, method = RequestMethod.POST)
	public String deleteUser(@PathVariable("id") Long id) throws IOException {
		User existedUser = userService.findById(id);
		userService.delete(id);

		logger.info("Пользователь с id: " + id + " и именем: " + existedUser.getLastName() + " "
				+ existedUser.getLastName() + " был удалён.");

		return "redirect:/boss/user/accounting";
	}

	@RequestMapping(value = {"/boss/user/position/add"}, method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> addPosition(Position position) throws IOException {
		Position savedPosition = positionService.save(position);

		logger.info("Должность с id: " + savedPosition.getId() + " и названием: " + savedPosition.getName() +
				" была добавлена в базу");

		return ResponseEntity.ok("Должность успешно добавлена!");
	}

	@RequestMapping(value = {"/boss/user/position/edit"}, method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> editPosition(Position position) throws IOException {
		PositionDTO existedPosition = transformer.transform(positionService.findById(position.getId()));
		positionService.update(position);

		logger.info("Должность с id: " + existedPosition.getId() + " была изменена." +
				"\n Название: " + existedPosition.getName() + " -> " + position.getName() +
				"\n Процент: " + existedPosition.getPercentageOfSales() + " -> " + position.getPercentageOfSales());

		return ResponseEntity.ok("Должность успешно обновлена!");
	}

	@RequestMapping(value = {"/boss/user/position/delete/{id}"}, method = RequestMethod.GET)
	public String deletePosition(@PathVariable("id") Long id) throws IOException {
		Position existedPosition = positionService.findById(id);
		positionService.delete(id);

		logger.info("Должность с id: " + id + " и названием: " + existedPosition.getName() +
				" была удалена из базы");

		return "redirect:/boss/user/accounting";
	}

	@RequestMapping(value = {"/boss/user/get-all"}, method = RequestMethod.POST)
	@ResponseBody
	public List<User> getAllUsers() {
		List<User> users = userService.findAll();
		if (users == null) {
			throw new UserDataException("В системе нет ни одного пользователя!");
		}
		return users;
	}

	@RequestMapping(value = {"/boss/user/get-salary-clients"}, method = RequestMethod.POST)
	@ResponseBody
	public List<User> outputClients(@RequestParam(name = "clientsId", required = false) long[] clientsId) {
		if (clientsId == null || clientsId.length == 0) {
			throw new UserDataException("Выберите работников для выдачи зарплаты!");
		}
		List<User> salaryUsers = userService.findByIdIn(clientsId);
		if (salaryUsers == null) {
			throw new UserDataException("Выбраны несуществующие работники!");
		}
		return salaryUsers;
	}

	@RequestMapping(value = {"/boss/user/pay-salaries"}, method = RequestMethod.POST)
	@ResponseBody
	public List<User> paySalary(@RequestParam(name = "clientsId", required = false) long[] usersIds) {

		if (usersIds == null || usersIds.length == 0) {
			throw new UserDataException("Выберите работников для выдачи зарплаты!");
		}
		List<User> allUsers = getAllUsers();
		List<User> salaryUsers = userService.findByIdIn(usersIds);

		if (salaryUsers == null) {
			throw new UserDataException("Выбраны несуществующие работники!");
		}

		shiftCalculationService.paySalary(salaryUsers);

		StringBuilder salaryUsersInfo = new StringBuilder("Зарплаты выданы: \n");

		for (User user : salaryUsers) {
			salaryUsersInfo.append(user.getLastName())
					.append(" ")
					.append(user.getLastName())
					.append(" - ")
					.append(user.getBonusBalance() + user.getSalaryBalance());
		}

		logger.info(salaryUsersInfo.toString());

		return allUsers;
	}

	@ExceptionHandler(value = UserDataException.class)
	public ResponseEntity<?> handleUserUpdateException(UserDataException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}

	@ExceptionHandler(value = PositionDataException.class)
	public ResponseEntity<?> handleUserUpdateException(PositionDataException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}
}
