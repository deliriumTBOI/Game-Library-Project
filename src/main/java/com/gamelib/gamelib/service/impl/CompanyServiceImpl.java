package com.gamelib.gamelib.service.impl;

import com.gamelib.gamelib.exception.ResourceAlreadyExistsException;
import com.gamelib.gamelib.exception.ResourceNotFoundException;
import com.gamelib.gamelib.model.Company;
import com.gamelib.gamelib.model.Game;
import com.gamelib.gamelib.repository.CompanyRepository;
import com.gamelib.gamelib.repository.GameRepository;
import com.gamelib.gamelib.service.CompanyService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyServiceImpl implements CompanyService {
    private static final String COMPANY_NOT_FOUND_WITH_ID = "Company not found with id: ";
    private static final String GAME_NOT_FOUND_WITH_ID = "Game not found with id: ";

    private final CompanyRepository companyRepository;
    private final GameRepository gameRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository, GameRepository gameRepository) {
        this.companyRepository = companyRepository;
        this.gameRepository = gameRepository;
    }

    @Override
    @Transactional
    public Company createCompany(Company company) {
        if (companyRepository.existsByName(company.getName())) {
            throw new ResourceAlreadyExistsException("Company is already exist");
        }
        return companyRepository.save(company);
    }

    @Override
    public Optional<Company> getCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    @Override
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Override
    public List<Company> getCompaniesByName(String name) {
        return companyRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    @Transactional
    public Company updateCompany(Long id, Company updatedCompany) {
        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(COMPANY_NOT_FOUND_WITH_ID + id));

        if (!existingCompany.getName().equals(updatedCompany.getName())
                && companyRepository.existsByName(updatedCompany.getName())) {
            throw new ResourceAlreadyExistsException("Company with name "
                    + updatedCompany.getName() + " already exists");
        }

        existingCompany.setName(updatedCompany.getName());
        existingCompany.setDescription(updatedCompany.getDescription());
        existingCompany.setFoundedYear(updatedCompany.getFoundedYear());
        existingCompany.setWebsite(updatedCompany.getWebsite());

        return companyRepository.save(existingCompany);
    }

    @Override
    @Transactional
    public boolean deleteCompany(Long id) {
        if (companyRepository.existsById(id)) {
            companyRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Company addGameToCompany(Long companyId, Long gameId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException(COMPANY_NOT_FOUND_WITH_ID
                        + companyId));

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException(GAME_NOT_FOUND_WITH_ID + gameId));

        company.getGames().add(game);

        return companyRepository.save(company);
    }

    @Override
    public Company getCompanyByIdOrThrow(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(COMPANY_NOT_FOUND_WITH_ID + id));
    }

    @Override
    @Transactional
    public boolean removeGameFromCompany(Long companyId, Long gameId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException(COMPANY_NOT_FOUND_WITH_ID
                        + companyId));

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException(GAME_NOT_FOUND_WITH_ID + gameId));

        boolean removed = company.getGames().remove(game);
        if (removed) {
            companyRepository.save(company);
        }

        return removed;
    }
}
