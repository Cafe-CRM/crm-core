package com.cafe.crm.controllers.boss.settings;

import com.cafe.crm.models.board.Board;
import com.cafe.crm.models.client.Calculate;
import com.cafe.crm.services.interfaces.board.BoardService;
import com.cafe.crm.services.interfaces.calculate.CalculateService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/boss/settings/board-setting")
public class BoardController {

	private final BoardService boardService;
	private final CalculateService calculateService;

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(BoardController.class);

	@Autowired
	public BoardController(BoardService boardService, CalculateService calculateService) {
		this.boardService = boardService;
		this.calculateService = calculateService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView bardSettingPage() {
		ModelAndView modelAndView = new ModelAndView("settingPages/boardSettingPage");
		modelAndView.addObject("boards", boardService.getAllOpen());
		return modelAndView;
	}

	@RequestMapping(value = "/new", method = RequestMethod.POST)
	public String newBoard(Board board, HttpServletRequest request) {
		Board savedBoard = boardService.save(board);
		String referrer = request.getHeader("Referer");

		logger.info("Открытие стола " + board.getName() + " с id: " + savedBoard.getId());

		return "redirect:" + referrer;
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
	public String deleteBoard(@PathVariable("id") Long id, HttpServletRequest request) {
		String referrer = request.getHeader("Referer");
		Board board = boardService.getOne(id);
		List<Calculate> calc = calculateService.getAllOpen();
		boolean flag = false;
		for (Calculate calculate : calc) {
			if (calculate.getBoard() != null) {
				if (calculate.getBoard().equals(board)) {
					flag = true;
					break;
				}
			}

		}
		if (!flag) {
			board.setIsOpen(false);
			boardService.save(board);
		}

		logger.info("Удаление стола " + board.getName() + " с id: " + id);

		return "redirect:" + referrer;
	}

}