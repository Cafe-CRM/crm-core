package com.cafe.crm.services.impl.menu;

import com.cafe.crm.models.menu.Menu;
import com.cafe.crm.repositories.menu.MenuRepository;
import com.cafe.crm.services.interfaces.company.CompanyService;
import com.cafe.crm.services.interfaces.menu.MenuService;
import com.cafe.crm.utils.CompanyIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MenuServiceImpl implements MenuService {

	private final MenuRepository menuRepository;

	@Autowired
	public MenuServiceImpl(MenuRepository menuRepository) {
		this.menuRepository = menuRepository;
	}

	@Override
	public Menu getOne(Long id) {
		return menuRepository.findOne(id);
	}

}
