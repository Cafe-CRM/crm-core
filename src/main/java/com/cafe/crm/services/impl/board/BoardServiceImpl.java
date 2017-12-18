package com.cafe.crm.services.impl.board;

import com.cafe.crm.models.board.Board;
import com.cafe.crm.models.company.Company;
import com.cafe.crm.repositories.board.BoardRepository;
import com.cafe.crm.services.interfaces.board.BoardService;
import com.cafe.crm.services.interfaces.company.CompanyService;
import com.cafe.crm.utils.CompanyIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardServiceImpl implements BoardService {

	private final BoardRepository boardRepository;
	private final CompanyService companyService;
	private final CompanyIdCache companyIdCache;

	@Autowired
	public BoardServiceImpl(BoardRepository boardRepository, CompanyService companyService, CompanyIdCache companyIdCache) {
		this.boardRepository = boardRepository;
		this.companyService = companyService;
		this.companyIdCache = companyIdCache;
	}

	private void setCompany(Board board){
		Long companyId = companyIdCache.getCompanyId();
		Company company = companyService.findOne(companyId);
		board.setCompany(company);
	}

	@Override
	public Board save(Board board) {
		setCompany(board);
		return boardRepository.saveAndFlush(board);
	}

	@Override
	public void delete(Board board) {
		boardRepository.delete(board);
	}

	@Override
	public List<Board> getAll() {
		return boardRepository.findByCompanyId(companyIdCache.getCompanyId());
	}

	@Override
	public Board getOne(Long id) {
		return boardRepository.findOne(id);
	}

	@Override
	public void deleteById(Long id) {
		boardRepository.delete(id);
	}

	@Override
	public List<Board> getAllOpen() {
		return boardRepository.getAllOpen(companyIdCache.getCompanyId());
	}

	@Override
	public boolean isExist() {
		Long count = boardRepository.countByCompanyIdAndIsOpenTrue(companyIdCache.getCompanyId());
		return count > 0L;
	}
}
