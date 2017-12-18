package com.cafe.crm.controllers.supervisor;

import com.cafe.crm.dto.ExtraUserData;
import com.cafe.crm.exceptions.user.PositionDataException;
import com.cafe.crm.exceptions.user.RoleDataException;
import com.cafe.crm.exceptions.user.UserDataException;
import com.cafe.crm.models.user.Role;
import com.cafe.crm.models.user.User;
import com.cafe.crm.services.interfaces.role.RoleService;
import com.cafe.crm.services.interfaces.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Controller
public class SupervisorController {

	private final UserService userService;
	private final RoleService roleService;

	@Autowired
	public SupervisorController(UserService userService, RoleService roleService) {
		this.userService = userService;
		this.roleService = roleService;
	}

	@RequestMapping(value = {"/supervisor"}, method = RequestMethod.GET)
	public String showAllUsers(Model model){
		List<User> allUsers = userService.findAllFromAllCompanies();
		Map<Role, List<User>> usersByRoles = userService.findAndSortUserByRoleWithSupervisor();
		List<Role> allRoles = roleService.findAllWithSupervisor();

		model.addAttribute("allUsers", allUsers);
		model.addAttribute("usersByRoles", usersByRoles);
		model.addAttribute("allRoles", allRoles);
		return "supervisor/supervisor";
	}

	@RequestMapping(value = {"/supervisor/user/edit"}, method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> editUser(@ModelAttribute @Valid User user, BindingResult bindingResult,
									  ExtraUserData extraUserData) {
		if (bindingResult.hasErrors()) {
			String fieldError = bindingResult.getFieldError().getDefaultMessage();
			throw new UserDataException("Не удалось изменить данные пользователя!\n" + fieldError);
		}
		userService.update(user, extraUserData);

		return ResponseEntity.ok("Пользователь успешно обновлен!");
	}

	@RequestMapping(value = {"/supervisor/user/delete/{id}"}, method = RequestMethod.POST)
	public String deleteUser(@PathVariable("id") Long id) throws IOException {
		userService.delete(id);
		return "redirect:/supervisor";
	}

	@RequestMapping(value = {"/supervisor/role/add"}, method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> addRole(Role role) throws IOException {
		roleService.save(role);
		return ResponseEntity.ok("Должность успешно добавлена!");
	}

	@RequestMapping(value = {"/supervisor/role/edit"}, method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> editRole(Role role) throws IOException {
		roleService.update(role);
		return ResponseEntity.ok("Роль успешно обновлена!");
	}

	@RequestMapping(value = {"/supervisor/role/delete/{id}"}, method = RequestMethod.GET)
	public String deleteRole(@PathVariable("id") Long id) throws IOException {
		roleService.delete(id);
		return "redirect:/supervisor";
	}

	@ExceptionHandler(value = UserDataException.class)
	public ResponseEntity<?> handleUserUpdateException(UserDataException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}

	@ExceptionHandler(value = PositionDataException.class)
	public ResponseEntity<?> handleUserUpdateException(RoleDataException ex) {
		return ResponseEntity.badRequest().body(ex.getMessage());
	}
}
