package com.gamelib.gamelib;

import com.gamelib.gamelib.service.impl.CompanyServiceImpl;
import com.gamelib.gamelib.cache.LruCache;
import com.gamelib.gamelib.exception.ResourceAlreadyExistsException;
import com.gamelib.gamelib.exception.ResourceNotFoundException;
import com.gamelib.gamelib.model.Company;
import com.gamelib.gamelib.model.Game;
import com.gamelib.gamelib.repository.CompanyRepository;
import com.gamelib.gamelib.repository.GameRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceImplTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private CompanyServiceImpl companyService;

    @Mock
    private LruCache<String, Company> companyCache;

    private Company testCompany;
    private Game testGame;

    @BeforeEach
    void setUp() {
        testCompany = new Company();
        testCompany.setId(1L);
        testCompany.setName("Test Company");
        testCompany.setDescription("Test Description");
        testCompany.setFoundedYear(2000);
        testCompany.setWebsite("https://testcompany.com");

        testGame = new Game();
        testGame.setId(1L);
        testGame.setTitle("Test Game");

        // Replace the autowired cache with our mock
        ReflectionTestUtils.setField(companyService, "companyCache", companyCache);
    }

    @Test
    void createCompany_ShouldCreateCompany_WhenCompanyDoesNotExist() {
        // Arrange
        when(companyRepository.existsByName(anyString())).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenReturn(testCompany);

        // Act
        Company result = companyService.createCompany(testCompany);

        // Assert
        assertEquals(testCompany, result);
        verify(companyRepository).existsByName(testCompany.getName());
        verify(companyRepository).save(testCompany);
    }

    @Test
    void createCompany_ShouldThrowResourceAlreadyExistsException_WhenCompanyExists() {
        // Arrange
        when(companyRepository.existsByName(anyString())).thenReturn(true);

        // Act & Assert
        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> companyService.createCompany(testCompany));

        assertEquals("Company is already exist", exception.getMessage());
        verify(companyRepository).existsByName(testCompany.getName());
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void createCompanies_ShouldCreateCompanies_WhenCompaniesDoNotExist() {
        // Arrange
        List<Company> companies = Collections.singletonList(testCompany);
        Set<String> names = Collections.singleton(testCompany.getName());

        when(companyRepository.findByNameIn(names)).thenReturn(Collections.emptyList());
        when(companyRepository.saveAll(companies)).thenReturn(companies);

        // Act
        List<Company> result = companyService.createCompanies(companies);

        // Assert
        assertEquals(companies, result);
        verify(companyRepository).findByNameIn(names);
        verify(companyRepository).saveAll(companies);
    }

    @Test
    void createCompanies_ShouldThrowResourceAlreadyExistsException_WhenCompanyExists() {
        // Arrange
        List<Company> companies = Collections.singletonList(testCompany);
        Set<String> names = Collections.singleton(testCompany.getName());

        when(companyRepository.findByNameIn(names)).thenReturn(companies);

        // Act & Assert
        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> companyService.createCompanies(companies));

        assertTrue(exception.getMessage().contains("Companies with names"));
        verify(companyRepository).findByNameIn(names);
        verify(companyRepository, never()).saveAll(any());
    }

    @Test
    void getCompanyById_ShouldReturnCachedCompany_WhenCompanyInCache() {
        // Arrange
        String cacheKey = "company:id:1";
        when(companyCache.containsKey(cacheKey)).thenReturn(true);
        when(companyCache.get(cacheKey)).thenReturn(testCompany);

        // Act
        Optional<Company> result = companyService.getCompanyById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testCompany, result.get());
        verify(companyCache).containsKey(cacheKey);
        verify(companyCache).get(cacheKey);
        verify(companyRepository, never()).findById(anyLong());
    }

    @Test
    void getCompanyById_ShouldReturnCompanyFromRepository_WhenCompanyNotInCache() {
        // Arrange
        String cacheKey = "company:id:1";
        when(companyCache.containsKey(cacheKey)).thenReturn(false);
        when(companyRepository.findById(1L)).thenReturn(Optional.of(testCompany));

        // Act
        Optional<Company> result = companyService.getCompanyById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testCompany, result.get());
        verify(companyCache).containsKey(cacheKey);
        verify(companyRepository).findById(1L);
        verify(companyCache).put(cacheKey, testCompany);
    }

    @Test
    void getAllCompanies_ShouldReturnAllCompanies() {
        // Arrange
        List<Company> companies = Collections.singletonList(testCompany);
        when(companyRepository.findAll()).thenReturn(companies);

        // Act
        List<Company> result = companyService.getAllCompanies();

        // Assert
        assertEquals(companies, result);
        verify(companyRepository).findAll();
    }

    @Test
    void getCompaniesByName_ShouldReturnCompaniesWithMatchingName() {
        // Arrange
        List<Company> companies = Collections.singletonList(testCompany);
        when(companyRepository.findByNameContainingIgnoreCase("Test")).thenReturn(companies);

        // Act
        List<Company> result = companyService.getCompaniesByName("Test");

        // Assert
        assertEquals(companies, result);
        verify(companyRepository).findByNameContainingIgnoreCase("Test");
    }

    @Test
    void updateCompany_ShouldUpdateCompany_WhenCompanyExists() {
        // Arrange
        Company updatedCompany = new Company();
        updatedCompany.setName("Updated Company");
        updatedCompany.setDescription("Updated Description");
        updatedCompany.setFoundedYear(2010);
        updatedCompany.setWebsite("https://updatedcompany.com");

        when(companyRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(companyRepository.existsByName("Updated Company")).thenReturn(false);
        when(companyRepository.save(any(Company.class))).thenReturn(testCompany);

        // Act
        Company result = companyService.updateCompany(1L, updatedCompany);

        // Assert
        assertEquals("Updated Company", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(2010, result.getFoundedYear());
        assertEquals("https://updatedcompany.com", result.getWebsite());
        verify(companyRepository).findById(1L);
        verify(companyRepository).existsByName("Updated Company");
        verify(companyRepository).save(testCompany);
        verify(companyCache).put("company:id:1", result);
    }

    @Test
    void updateCompany_ShouldThrowResourceNotFoundException_WhenCompanyDoesNotExist() {
        // Arrange
        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> companyService.updateCompany(1L, testCompany));

        assertEquals("Company not found with id: 1", exception.getMessage());
        verify(companyRepository).findById(1L);
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void updateCompany_ShouldThrowResourceAlreadyExistsException_WhenNameExists() {
        // Arrange
        Company existingCompany = new Company();
        existingCompany.setId(1L);
        existingCompany.setName("Existing Company");

        Company updatedCompany = new Company();
        updatedCompany.setName("Updated Company");

        when(companyRepository.findById(1L)).thenReturn(Optional.of(existingCompany));
        when(companyRepository.existsByName("Updated Company")).thenReturn(true);

        // Act & Assert
        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> companyService.updateCompany(1L, updatedCompany));

        assertEquals("Company with name Updated Company already exists", exception.getMessage());
        verify(companyRepository).findById(1L);
        verify(companyRepository).existsByName("Updated Company");
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void deleteCompany_ShouldDeleteCompany_WhenCompanyExists() {
        // Arrange
        when(companyRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = companyService.deleteCompany(1L);

        // Assert
        assertTrue(result);
        verify(companyRepository).existsById(1L);
        verify(companyRepository).deleteById(1L);
        verify(companyCache).remove("company:id:1");
    }

    @Test
    void deleteCompany_ShouldReturnFalse_WhenCompanyDoesNotExist() {
        // Arrange
        when(companyRepository.existsById(1L)).thenReturn(false);

        // Act
        boolean result = companyService.deleteCompany(1L);

        // Assert
        assertFalse(result);
        verify(companyRepository).existsById(1L);
        verify(companyRepository, never()).deleteById(anyLong());
        verify(companyCache, never()).remove(anyString());
    }

    @Test
    void addGameToCompany_ShouldAddGameToCompany_WhenCompanyAndGameExist() {
        // Arrange
        when(companyRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(companyRepository.save(any(Company.class))).thenReturn(testCompany);

        // Act
        Company result = companyService.addGameToCompany(1L, 1L);

        // Assert
        assertEquals(testCompany, result);
        assertTrue(testCompany.getGames().contains(testGame));
        verify(companyRepository).findById(1L);
        verify(gameRepository).findById(1L);
        verify(companyRepository).save(testCompany);
        verify(companyCache).put("company:id:1", result);
    }

    @Test
    void addGameToCompany_ShouldThrowResourceNotFoundException_WhenCompanyDoesNotExist() {
        // Arrange
        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> companyService.addGameToCompany(1L, 1L));

        assertEquals("Company not found with id: 1", exception.getMessage());
        verify(companyRepository).findById(1L);
        verify(gameRepository, never()).findById(anyLong());
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void addGameToCompany_ShouldThrowResourceNotFoundException_WhenGameDoesNotExist() {
        // Arrange
        when(companyRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> companyService.addGameToCompany(1L, 1L));

        assertEquals("Game not found with id: 1", exception.getMessage());
        verify(companyRepository).findById(1L);
        verify(gameRepository).findById(1L);
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void getCompanyByNameOrThrow_ShouldReturnCompany_WhenCompanyExists() {
        // Arrange
        when(companyRepository.findByName("Test Company")).thenReturn(Optional.of(testCompany));

        // Act
        Company result = companyService.getCompanyByNameOrThrow("Test Company");

        // Assert
        assertEquals(testCompany, result);
        verify(companyRepository).findByName("Test Company");
    }

    @Test
    void getCompanyByNameOrThrow_ShouldThrowResourceNotFoundException_WhenCompanyDoesNotExist() {
        // Arrange
        when(companyRepository.findByName("Test Company")).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> companyService.getCompanyByNameOrThrow("Test Company"));

        assertEquals("Company not found with name: Test Company", exception.getMessage());
        verify(companyRepository).findByName("Test Company");
    }

    @Test
    void removeGameFromCompany_ShouldRemoveGameFromCompany_WhenCompanyAndGameExist() {
        // Arrange
        testCompany.setGames(new HashSet<>(Collections.singletonList(testGame)));

        when(companyRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(companyRepository.save(any(Company.class))).thenReturn(testCompany);

        // Act
        boolean result = companyService.removeGameFromCompany(1L, 1L);

        // Assert
        assertTrue(result);
        assertFalse(testCompany.getGames().contains(testGame));
        verify(companyRepository).findById(1L);
        verify(gameRepository).findById(1L);
        verify(companyRepository).save(testCompany);
        verify(companyCache).put("company:id:1", testCompany);
    }

    @Test
    void removeGameFromCompany_ShouldReturnFalse_WhenGameNotInCompany() {
        // Arrange
        testCompany.setGames(new HashSet<>());

        when(companyRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));

        // Act
        boolean result = companyService.removeGameFromCompany(1L, 1L);

        // Assert
        assertFalse(result);
        verify(companyRepository).findById(1L);
        verify(gameRepository).findById(1L);
        verify(companyRepository, never()).save(any(Company.class));
        verify(companyCache, never()).put(anyString(), any(Company.class));
    }
}