package com.cafe.crm.services.impl.missing;

import com.cafe.crm.models.company.Company;
import com.cafe.crm.models.missingModel.MissingProduct;
import com.cafe.crm.models.shift.Shift;
import com.cafe.crm.repositories.missing.MissingProductRepository;
import com.cafe.crm.services.interfaces.company.CompanyService;
import com.cafe.crm.services.interfaces.missing.MissingProductService;
import com.cafe.crm.utils.CompanyIdCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MissingProductServiceImpl implements MissingProductService {

    private final MissingProductRepository missingProductRepository;
    private final CompanyIdCache companyIdCache;
    private final CompanyService companyService;

    @Autowired
    public MissingProductServiceImpl(MissingProductRepository missingProductRepository, CompanyIdCache companyIdCache,
                                     CompanyService companyService) {
        this.missingProductRepository = missingProductRepository;
        this.companyIdCache = companyIdCache;
        this.companyService = companyService;
    }

    @Override
    public List<MissingProduct> findAll() {
        return missingProductRepository.findAll();
    }

    /*@Override
    public List<MissingProduct> getAllWithFetchGraph(String graphName) {
        return missingProductRepository.readByShiftAndCompanyId(graphName, companyIdCache.getCompanyId());
    }*/

    @Override
    public List<MissingProduct> saveAll(List<MissingProduct> products) {
        for (MissingProduct product : products) {
            setCompany(product);
        }
        return missingProductRepository.save(products);
    }

    @Override
    public MissingProduct saveAndFlush(MissingProduct product) {
        setCompany(product);
        return missingProductRepository.save(product);
    }

    @Override
    public MissingProduct findOne(Long id) {
        return missingProductRepository.findOne(id);
    }

    @Override
    public void delete(Long id) {
        missingProductRepository.delete(id);
    }

    @Override
    public List<MissingProduct> findByShift(Shift shift) {
        //return missingProductRepository.findAll();
        return missingProductRepository.findByShiftAndCompanyId(shift, companyIdCache.getCompanyId());
    }

    @Override
    public List<MissingProduct> findByShiftWithCategory(Shift shift) {
        //return missingProductRepository.findAll();
        return missingProductRepository.readByShiftAndCompanyId(shift, companyIdCache.getCompanyId());
    }

    @Override
    public void deleteByShift(Shift shift) {
        missingProductRepository.deleteAllByShiftAndCompanyId(shift, companyIdCache.getCompanyId());
    }

    @Override
    public void deleteByShiftIdIn(long[] ids) {
        missingProductRepository.deleteAllByShiftIdInAndCompanyId(ids, companyIdCache.getCompanyId());
    }

    private void setCompany(MissingProduct missingProduct) {
        Long companyId = companyIdCache.getCompanyId();
        Company company = companyService.findOne(companyId);
        missingProduct.setCompany(company);
    }
}
