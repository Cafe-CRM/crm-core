package com.cafe.crm.controllers.boss.settings;

import com.cafe.crm.models.checklist.Checklist;
import com.cafe.crm.services.interfaces.checklist.ChecklistService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/boss/settings/checklists")
public class ChecklistController {

	private final ChecklistService checklistService;

	private final org.slf4j.Logger logger = LoggerFactory.getLogger(ChecklistController.class);

	@Autowired
	public ChecklistController(ChecklistService checklistService) {
		this.checklistService = checklistService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showChecklistSettingsPage(Model model) {
		model.addAttribute("openChecklist", checklistService.getAllForOpenShift());
		model.addAttribute("closeChecklist", checklistService.getAllForCloseShift());
		model.addAttribute("checklistForm", new Checklist());
		return "settingPages/checklistSettingPage";
	}

	@RequestMapping(value = "/addOpen", method = RequestMethod.POST)
	public String addToOpenChecklist(@RequestParam(name = "text") String text) {
		Checklist checklist = checklistService.saveChecklistOnOpenShift(text);

		logger.info("Чеклист с id " + checklist.getId() + " описанием: \"" + text + "\" был добавлен на открытие смены");

		return "redirect:/boss/settings/checklists";
	}

	@RequestMapping(value = "/addClose", method = RequestMethod.POST)
	public String addToCloseChecklist(@RequestParam(name = "text") String text) {
		Checklist checklist = checklistService.saveChecklistOnCloseShift(text);

		logger.info("Чеклист с id " + checklist.getId() + " описанием: \"" + text + "\" был добавлен на закрытие смены");

		return "redirect:/boss/settings/checklists";
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String updateChecklist(Checklist checklist) {
		checklistService.update(checklist);
		String description = checklist.getOnCloseShiftText() == null ? checklist.getOnOpenShiftText() : checklist.getOnOpenShiftText();

		logger.info("Чеклист с описанием: \"" + checklist.toString() + "\" был добавлен на закрытие смены");

		return "redirect:/boss/settings/checklists";
	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
	public String deleteChecklist(@PathVariable("id") Long id) {
		Checklist checklist = checklistService.find(id);
		String description;

		if (checklist.getOnCloseShiftText() == null) {
			description = checklist.getOnOpenShiftText();
			checklistService.delete(id);

			logger.info("Чеклист c id: " + id + " и описанием: \"" + description + "\" был удалён с открытия смены");

			return "redirect:/boss/settings/checklists";
		}

		description = checklist.getOnCloseShiftText();
		checklistService.delete(id);

		logger.info("Чеклист c id: " + id + " и описанием: \"" + description + "\" был удалён с закрытия смены");

		return "redirect:/boss/settings/checklists";
	}
}
