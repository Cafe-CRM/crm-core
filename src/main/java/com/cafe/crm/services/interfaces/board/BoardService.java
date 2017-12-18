package com.cafe.crm.services.interfaces.board;

import com.cafe.crm.models.board.Board;

import java.util.List;

public interface BoardService {

	Board save(Board board);

	void delete(Board board);

	List<Board> getAll();

	Board getOne(Long id);

	void deleteById(Long id);

	List<Board> getAllOpen();

	boolean isExist();
}
