package com.cafe.crm.services.impl.company.configuration.step;

import com.cafe.crm.services.interfaces.board.BoardService;
import com.cafe.crm.services.interfaces.company.configuration.ConfigurationStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BoardStep implements ConfigurationStep {

	private static final String NAME = "board";

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

}
