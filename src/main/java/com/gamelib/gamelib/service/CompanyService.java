package com.gamelib.gamelib.service;

import com.gamelib.gamelib.model.Company;
import java.util.List;
import java.util.Optional;

public interface CompanyService {
    Company createCompany(Company company);

    Optional<Company> getCompanyById(Long id);

    List<Company> getAllCompanies();

    List<Company> getCompaniesByName(String name);

    Company updateCompany(Long id, Company updatedCompany);

    boolean deleteCompany(Long id);

    Company addGameToCompany(Long companyId, Long gameId);

    Company getCompanyByNameOrThrow(String name);

    boolean removeGameFromCompany(Long companyId, Long gameId);
}