package com.cafe.crm.services.interfaces.position;


import com.cafe.crm.models.user.Position;

import java.util.List;

public interface PositionService {

	Position save(Position position);

	List<Position> findAll();

	List<Position> findAllWithEnabledPercent();

	void update(Position position);

	void delete(Long id);

	Position findByName(String name);

	List<Position> findByIdIn(Long[] ids);

	Position findById(Long id);

}
