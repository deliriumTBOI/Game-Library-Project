package com.gamelib.gamelib.service;

import com.gamelib.gamelib.dto.CompanyDto;
import com.gamelib.gamelib.model.Company;
import com.gamelib.gamelib.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    // Получить все компании
    public List<CompanyDto> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(CompanyDto::new)  // преобразуем Company в CompanyDto
                .collect(Collectors.toList());
    }

    // Получить компанию по ID
    public Optional<Company> getCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    // Создать новую компанию
    public Company createCompany(CompanyDto companyDto) {
        // Проверка на уникальность названия компании
        if (isCompanyNameExists(companyDto.getName())) {
            throw new IllegalArgumentException("Company already exist");
        }

        Company company = new Company(companyDto.getName(), companyDto.getCountry());
        return companyRepository.save(company);
    }


    // Обновить компанию
    public Company updateCompany(Long id, CompanyDto companyDto) {
        return companyRepository.findById(id)
                .map(company -> {
                    if (companyDto.getName() != null) {
                        company.setName(companyDto.getName());
                    }
                    if (companyDto.getCountry() != null) {
                        company.setCountry(companyDto.getCountry());
                    }

                    return companyRepository.save(company);
                })
                .orElse(null);  // если компания не найдена, вернуть null
    }

    // Удалить компанию
    public boolean deleteCompany(Long id) {
        if (companyRepository.existsById(id)) {
            companyRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean isCompanyNameExists(String name) {
        return companyRepository.existsByName(name);
    }

}
