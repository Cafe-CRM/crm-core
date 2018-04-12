package com.cafe.crm.services.impl.company.configuration.step;

import com.cafe.crm.models.board.Board;
import com.cafe.crm.services.interfaces.board.BoardService;
import com.cafe.crm.services.interfaces.company.configuration.ConfigurationStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BoardStep implements ConfigurationStep<List<Board>> {

	private static final String NAME = "board";

	private static final String ATTR_NAME = "boards";

	private final BoardService boardService;

	@Autowired
	public BoardStep(BoardService boardService) {
		this.boardService = boardService;
	}

	@Override
	public boolean isConfigured() {
		return boardService.isExist();
	}

	@Override
	public String getStepName() {
		return NAME;
	}

	@Override
	public String getAttrName() {
		return ATTR_NAME;
	}

	@Override
	public List<Board> getAttribute() {
		return boardService.getAll();
	}

}
