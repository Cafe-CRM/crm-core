package com.cafe.crm.controllers.rest;

import com.cafe.crm.models.client.Calculate;
import com.cafe.crm.models.user.User;
import com.cafe.crm.services.interfaces.board.BoardService;
import com.cafe.crm.services.interfaces.calculate.CalculateService;
import com.cafe.crm.services.interfaces.client.ClientService;
import com.cafe.crm.services.interfaces.user.UserService;
import com.cafe.crm.utils.JsonField;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.cafe.crm.utils.PatternMatcherHandler.matchEmail;
import static com.cafe.crm.utils.PatternMatcherHandler.matchPhone;

@RestController
public class TelegramBotController {

	private final BoardService boardService;
	private final UserService userService;
	private final ClientService clientService;

	@Autowired
	public TelegramBotController(BoardService boardService, UserService userService, ClientService clientService) {
		this.boardService = boardService;
		this.userService = userService;
		this.clientService = clientService;
	}

	@Autowired
	private CalculateService calculateService;

	@RequestMapping(value = "/manager/rest/Table", method = RequestMethod.GET)
	@ResponseBody
	@JsonView(JsonField.Board.class)
	public List<com.cafe.crm.models.board.Board> getListOpenTables() {
		return boardService.getAllOpen();
	}

	@RequestMapping(value = "/manager/rest/clientsNumber", method = RequestMethod.GET)
	@ResponseBody
	public Integer getIdLastClient() {
		return Integer.parseInt(clientService.getLast().getId().toString());
	}

	@RequestMapping(value = "/authenticationTelegramBotUsers", method = RequestMethod.POST)
	@ResponseBody
	public User checkAuthCredentials(@RequestParam(value = "username") String username,
	                                        @RequestParam(value = "password") String password) {
		User userInDB;
		if (matchEmail(username) || matchPhone(username)) {
			userInDB = userService.findByUsername(username);
		} else {
			return new User();
		}
		if (userService.isValidPassword(userInDB.getEmail(), password)) {
			return userInDB;
		} else {
			return new User();
		}
	}
}
