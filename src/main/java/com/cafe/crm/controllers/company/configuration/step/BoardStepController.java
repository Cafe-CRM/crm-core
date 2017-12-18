package com.cafe.crm.controllers.company.configuration.step;

import com.cafe.crm.models.board.Board;
import com.cafe.crm.services.interfaces.board.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/company/configuration/step/board")
public class BoardStepController {

	private final BoardService boardService;

	@Autowired
	public BoardStepController(BoardService boardService) {
		this.boardService = boardService;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ResponseEntity<?> addBoard(@Valid Board board, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			String fieldError = bindingResult.getFieldError().getDefaultMessage();
			return ResponseEntity.badRequest().body(fieldError);
		}
		Board savedBoard = boardService.save(board);
		return ResponseEntity.ok(savedBoard);
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public ResponseEntity<?> deleteBoard(@RequestParam(name = "boardId") Long boardId) {
		boardService.deleteById(boardId);
		return ResponseEntity.ok("");
	}

}
