package com.cafe.crm.repositories.company;


import com.cafe.crm.models.company.Company;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CompanyRepository extends JpaRepository<Company, Long> {
}