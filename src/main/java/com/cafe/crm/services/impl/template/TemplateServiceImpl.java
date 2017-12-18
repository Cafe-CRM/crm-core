package com.cafe.crm.services.impl.template;

import com.cafe.crm.models.company.Company;
import com.cafe.crm.models.template.Template;
import com.cafe.crm.repositories.template.TemplateRepository;
import com.cafe.crm.services.interfaces.company.CompanyService;
import com.cafe.crm.services.interfaces.template.TemplateService;
import com.cafe.crm.utils.CompanyIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository templateRepository;

    @Autowired
    public TemplateServiceImpl(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    public Template findByName(String name) {
        return templateRepository.findByName(name);
    }

    @Override
    public void save(Template template) {
        templateRepository.save(template);
    }
}
