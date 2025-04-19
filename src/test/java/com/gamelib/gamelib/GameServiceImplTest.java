package com.gamelib.gamelib;

import com.gamelib.gamelib.service.impl.GameServiceImpl;
import com.gamelib.gamelib.cache.LruCache;
import com.gamelib.gamelib.exception.ResourceAlreadyExistsException;
import com.gamelib.gamelib.exception.ResourceNotFoundException;
import com.gamelib.gamelib.model.Company;
import com.gamelib.gamelib.model.Game;
import com.gamelib.gamelib.model.Review;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GameServiceImplTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private GameServiceImpl gameService;

    @Mock
    private LruCache<String, List<Game>> gameCache;

    private Game testGame;
    private Company testCompany;

    private Date createDate(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day, 1, 10, 5); // как в выводе
        cal.set(Calendar.MILLISECOND, 0); // ВАЖНО
        return cal.getTime();
    }

    @BeforeEach
    void setUp() {
        testGame = new Game();
        testGame.setId(1L);
        testGame.setTitle("Test Game");
        testGame.setDescription("Test Description");
        testGame.setReleaseDate(createDate(2023, 10, 2)); // 2023-01-01
        testGame.setGenre("Action");
        testGame.setCompanies(new HashSet<>());
        testGame.setReviews(new ArrayList<>());

        testCompany = new Company();
        testCompany.setId(1L);
        testCompany.setName("Test Company");

        // Replace the autowired cache with our mock
        ReflectionTestUtils.setField(gameService, "gameCache", gameCache);
    }

    @Test
    void createGame_ShouldCreateGame_WhenGameDoesNotExist() {
        // Arrange
        when(gameRepository.existsByTitle(anyString())).thenReturn(false);
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        // Act
        Game result = gameService.createGame(testGame);

        // Assert
        assertEquals(testGame, result);
        verify(gameRepository).existsByTitle(testGame.getTitle());
        verify(gameRepository).save(testGame);
        verify(gameCache).clear();
    }

    @Test
    void createGame_ShouldThrowResourceAlreadyExistsException_WhenGameExists() {
        // Arrange
        when(gameRepository.existsByTitle(anyString())).thenReturn(true);

        // Act & Assert
        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> gameService.createGame(testGame));

        assertEquals("Game is already exist", exception.getMessage());
        verify(gameRepository).existsByTitle(testGame.getTitle());
        verify(gameRepository, never()).save(any(Game.class));
        verify(gameCache, never()).clear();
    }

    @Test
    void createGames_ShouldCreateGames_WhenGamesDoNotExist() {
        // Arrange
        List<Game> games = Collections.singletonList(testGame);
        Set<String> titles = Collections.singleton(testGame.getTitle());

        when(gameRepository.findByTitleIn(titles)).thenReturn(Collections.emptyList());
        when(companyRepository.findByNameIn(anySet())).thenReturn(Collections.emptyList());
        when(gameRepository.saveAll(games)).thenReturn(games);

        // Act
        List<Game> result = gameService.createGames(games);

        // Assert
        assertEquals(games, result);
        verify(gameRepository).findByTitleIn(titles);
        verify(companyRepository).findByNameIn(anySet());
        verify(gameRepository).saveAll(games);
        verify(gameCache).clear();
    }

    @Test
    void createGames_ShouldThrowResourceAlreadyExistsException_WhenGameExists() {
        // Arrange
        List<Game> games = Collections.singletonList(testGame);
        Set<String> titles = Collections.singleton(testGame.getTitle());

        when(gameRepository.findByTitleIn(titles)).thenReturn(games);

        // Act & Assert
        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> gameService.createGames(games));

        assertTrue(exception.getMessage().contains("Games with titles"));
        verify(gameRepository).findByTitleIn(titles);
        verify(gameRepository, never()).saveAll(any());
        verify(gameCache, never()).clear();
    }

    @Test
    void getAllGames_ShouldReturnAllGames() {
        // Arrange
        List<Game> games = Collections.singletonList(testGame);
        when(gameRepository.findAll()).thenReturn(games);

        // Act
        List<Game> result = gameService.getAllGames();

        // Assert
        assertEquals(games, result);
        verify(gameRepository).findAll();
    }

    @Test
    void getGamesByTitle_ShouldReturnGamesWithMatchingTitle() {
        // Arrange
        List<Game> games = Collections.singletonList(testGame);
        when(gameRepository.findByTitleIgnoreCase("Test Game")).thenReturn(games);

        // Act
        List<Game> result = gameService.getGamesByTitle("Test Game");

        // Assert
        assertEquals(games, result);
        verify(gameRepository).findByTitleIgnoreCase("Test Game");
    }

    @Test
    void getGameById_ShouldReturnGame_WhenGameExists() {
        // Arrange
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));

        // Act
        Game result = gameService.getGameById(1L);

        // Assert
        assertEquals(testGame, result);
        verify(gameRepository).findById(1L);
    }

    @Test
    void getGameById_ShouldThrowResourceNotFoundException_WhenGameDoesNotExist() {
        // Arrange
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> gameService.getGameById(1L));

        assertEquals("Game not found with id: 1", exception.getMessage());
        verify(gameRepository).findById(1L);
    }

    @Test
    void updateGame_ShouldUpdateGame_WhenGameExists() {
        // Arrange
        Game updatedGame = new Game();
        updatedGame.setTitle("Updated Game");
        updatedGame.setDescription("Updated Description");
        updatedGame.setReleaseDate(createDate(2022, 11, 2));
        updatedGame.setGenre("RPG");

        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(gameRepository.existsByTitle("Updated Game")).thenReturn(false);
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        // Act
        Game result = gameService.updateGame(1L, updatedGame);

        // Assert
        assertEquals("Updated Game", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(createDate(2022, 11, 2), result.getReleaseDate());
        assertEquals("RPG", result.getGenre());
        verify(gameRepository).findById(1L);
        verify(gameRepository).existsByTitle("Updated Game");
        verify(gameRepository).save(testGame);
        verify(gameCache).clear();
    }

    @Test
    void updateGame_ShouldThrowResourceNotFoundException_WhenGameDoesNotExist() {
        // Arrange
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> gameService.updateGame(1L, testGame));

        assertEquals("Game not found with id: 1", exception.getMessage());
        verify(gameRepository).findById(1L);
        verify(gameRepository, never()).save(any(Game.class));
        verify(gameCache, never()).clear();
    }

    @Test
    void updateGame_ShouldThrowResourceAlreadyExistsException_WhenTitleExists() {
        // Arrange
        Game existingGame = new Game();
        existingGame.setId(1L);
        existingGame.setTitle("Existing Game");

        Game updatedGame = new Game();
        updatedGame.setTitle("Updated Game");

        when(gameRepository.findById(1L)).thenReturn(Optional.of(existingGame));
        when(gameRepository.existsByTitle("Updated Game")).thenReturn(true);

        // Act & Assert
        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> gameService.updateGame(1L, updatedGame));

        assertEquals("Game with title Updated Game already exists", exception.getMessage());
        verify(gameRepository).findById(1L);
        verify(gameRepository).existsByTitle("Updated Game");
        verify(gameRepository, never()).save(any(Game.class));
        verify(gameCache, never()).clear();
    }

    @Test
    void updateGame_ShouldUpdateGameWithCompanies_WhenCompaniesProvided() {
        // Arrange
        Game existingGame = new Game();
        existingGame.setId(1L);
        existingGame.setTitle("Existing Game");
        existingGame.setCompanies(new HashSet<>());

        Game updatedGame = new Game();
        updatedGame.setTitle("Existing Game");
        Set<Company> companies = new HashSet<>(Collections.singletonList(testCompany));
        updatedGame.setCompanies(companies);

        when(gameRepository.findById(1L)).thenReturn(Optional.of(existingGame));
        when(gameRepository.save(any(Game.class))).thenReturn(existingGame);

        // Act
        Game result = gameService.updateGame(1L, updatedGame);

        // Assert
        assertEquals(companies, result.getCompanies());
        verify(gameRepository).findById(1L);
        verify(gameRepository).save(existingGame);
        verify(gameCache).clear();
    }

    @Test
    void updateGame_ShouldUpdateGameWithReviews_WhenReviewsProvided() {
        // Arrange
        Game existingGame = new Game();
        existingGame.setId(1L);
        existingGame.setTitle("Existing Game");
        existingGame.setReviews(new ArrayList<>());

        Game updatedGame = new Game();
        updatedGame.setTitle("Existing Game");

        Review review = new Review();
        review.setRating(5);
        review.setText("Great game");
        List<Review> reviews = new ArrayList<>(Collections.singletonList(review));
        updatedGame.setReviews(reviews);

        when(gameRepository.findById(1L)).thenReturn(Optional.of(existingGame));
        when(gameRepository.save(any(Game.class))).thenReturn(existingGame);

        // Act
        Game result = gameService.updateGame(1L, updatedGame);

        // Assert
        assertEquals(1, result.getReviews().size());
        Review addedReview = result.getReviews().get(0);
        assertEquals(5, addedReview.getRating());
        assertEquals("Great game", addedReview.getText());
        assertEquals(existingGame, addedReview.getGame()); // Review has reference to the game
        verify(gameRepository).findById(1L);
        verify(gameRepository).save(existingGame);
        verify(gameCache).clear();
    }

    @Test
    void patchGame_ShouldUpdateOnlyProvidedFields_WhenGameExists() {
        // Arrange
        Game existingGame = new Game();
        existingGame.setId(1L);
        existingGame.setTitle("Existing Game");
        existingGame.setDescription("Original Description");
        existingGame.setReleaseDate(createDate(2020, 8, 10)); // 2023-01-01
        existingGame.setGenre("Action");

        Game partialGame = new Game();
        partialGame.setDescription("Updated Description");
        partialGame.setGenre("RPG");

        when(gameRepository.findById(1L)).thenReturn(Optional.of(existingGame));
        when(gameRepository.save(any(Game.class))).thenReturn(existingGame);

        // Act
        Game result = gameService.patchGame(1L, partialGame);

        // Assert
        assertEquals("Existing Game", result.getTitle()); // Unchanged
        assertEquals("Updated Description", result.getDescription()); // Changed
        assertEquals(createDate(2020, 8, 10), result.getReleaseDate()); // Unchanged
        assertEquals("RPG", result.getGenre()); // Changed
        verify(gameRepository).findById(1L);
        verify(gameRepository).save(existingGame);
        verify(gameCache).clear();
    }

    @Test
    void patchGame_ShouldUpdateTitle_WhenNewTitleDoesNotExist() {
        // Arrange
        Game existingGame = new Game();
        existingGame.setId(1L);
        existingGame.setTitle("Existing Game");

        Game partialGame = new Game();
        partialGame.setTitle("New Title");

        when(gameRepository.findById(1L)).thenReturn(Optional.of(existingGame));
        when(gameRepository.existsByTitle("New Title")).thenReturn(false);
        when(gameRepository.save(any(Game.class))).thenReturn(existingGame);

        // Act
        Game result = gameService.patchGame(1L, partialGame);

        // Assert
        assertEquals("New Title", result.getTitle());
        verify(gameRepository).findById(1L);
        verify(gameRepository).existsByTitle("New Title");
        verify(gameRepository).save(existingGame);
        verify(gameCache).clear();
    }

    @Test
    void patchGame_ShouldThrowResourceAlreadyExistsException_WhenNewTitleExists() {
        // Arrange
        Game existingGame = new Game();
        existingGame.setId(1L);
        existingGame.setTitle("Existing Game");

        Game partialGame = new Game();
        partialGame.setTitle("New Title");

        when(gameRepository.findById(1L)).thenReturn(Optional.of(existingGame));
        when(gameRepository.existsByTitle("New Title")).thenReturn(true);

        // Act & Assert
        ResourceAlreadyExistsException exception = assertThrows(ResourceAlreadyExistsException.class,
                () -> gameService.patchGame(1L, partialGame));

        assertEquals("Game with title New Title already exists", exception.getMessage());
        verify(gameRepository).findById(1L);
        verify(gameRepository).existsByTitle("New Title");
        verify(gameRepository, never()).save(any(Game.class));
        verify(gameCache, never()).clear();
    }

    @Test
    void deleteGame_ShouldDeleteGame_WhenGameExists() {
        // Arrange
        when(gameRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = gameService.deleteGame(1L);

        // Assert
        assertTrue(result);
        verify(gameRepository).existsById(1L);
        verify(gameRepository).deleteById(1L);
        verify(gameCache).clear();
    }

    @Test
    void deleteGame_ShouldReturnFalse_WhenGameDoesNotExist() {
        // Arrange
        when(gameRepository.existsById(1L)).thenReturn(false);

        // Act
        boolean result = gameService.deleteGame(1L);

        // Assert
        assertFalse(result);
        verify(gameRepository).existsById(1L);
        verify(gameRepository, never()).deleteById(anyLong());
        verify(gameCache, never()).clear();
    }

    @Test
    void addCompanyToGame_ShouldAddCompanyToGame_WhenGameAndCompanyExist() {
        // Arrange
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(companyRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        // Act
        Game result = gameService.addCompanyToGame(1L, 1L);

        // Assert
        assertEquals(testGame, result);
        assertTrue(testGame.getCompanies().contains(testCompany));
        verify(gameRepository).findById(1L);
        verify(companyRepository).findById(1L);
        verify(gameRepository).save(testGame);
    }

    @Test
    void addCompanyToGame_ShouldThrowResourceNotFoundException_WhenGameDoesNotExist() {
        // Arrange
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> gameService.addCompanyToGame(1L, 1L));

        assertEquals("Game not found with id: 1", exception.getMessage());
        verify(gameRepository).findById(1L);
        verify(companyRepository, never()).findById(anyLong());
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    void addCompanyToGame_ShouldThrowResourceNotFoundException_WhenCompanyDoesNotExist() {
        // Arrange
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> gameService.addCompanyToGame(1L, 1L));

        assertEquals("Company not found with id: 1", exception.getMessage());
        verify(gameRepository).findById(1L);
        verify(companyRepository).findById(1L);
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    void removeCompanyFromGame_ShouldRemoveCompanyFromGame_WhenGameAndCompanyExist() {
        // Arrange
        testGame.getCompanies().add(testCompany);

        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(companyRepository.findById(1L)).thenReturn(Optional.of(testCompany));
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        // Act
        boolean result = gameService.removeCompanyFromGame(1L, 1L);

        // Assert
        assertTrue(result);
        assertFalse(testGame.getCompanies().contains(testCompany));
        verify(gameRepository).findById(1L);
        verify(companyRepository).findById(1L);
        verify(gameRepository).save(testGame);
    }

    @Test
    void removeCompanyFromGame_ShouldReturnFalse_WhenCompanyNotInGame() {
        // Arrange
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(companyRepository.findById(1L)).thenReturn(Optional.of(testCompany));

        // Act
        boolean result = gameService.removeCompanyFromGame(1L, 1L);

        // Assert
        assertFalse(result);
        verify(gameRepository).findById(1L);
        verify(companyRepository).findById(1L);
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    void getGamesByMinimumRating_ShouldReturnCachedGames_WhenGamesInCache() {
        // Arrange
        String cacheKey = "games:min_rating:4";
        List<Game> games = Collections.singletonList(testGame);

        when(gameCache.containsKey(cacheKey)).thenReturn(true);
        when(gameCache.get(cacheKey)).thenReturn(games);

        // Act
        List<Game> result = gameService.getGamesByMinimumRating(4);

        // Assert
        assertEquals(games, result);
        verify(gameCache).containsKey(cacheKey);
        verify(gameCache).get(cacheKey);
        verify(gameRepository, never()).findGamesByMinimumRating(anyInt());
    }

    @Test
    void getGamesByMinimumRating_ShouldReturnGamesFromRepository_WhenGamesNotInCache() {
        // Arrange
        String cacheKey = "games:min_rating:4";
        List<Game> games = Collections.singletonList(testGame);

        when(gameCache.containsKey(cacheKey)).thenReturn(false);
        when(gameRepository.findGamesByMinimumRating(4)).thenReturn(games);

        // Act
        List<Game> result = gameService.getGamesByMinimumRating(4);

        // Assert
        assertEquals(games, result);
        verify(gameCache).containsKey(cacheKey);
        verify(gameRepository).findGamesByMinimumRating(4);
        verify(gameCache).put(cacheKey, games);
    }

    @Test
    void getGamesByRatingRange_ShouldReturnCachedGames_WhenGamesInCache() {
        // Arrange
        String cacheKey = "games:rating_range:3:5";
        List<Game> games = Collections.singletonList(testGame);

        when(gameCache.containsKey(cacheKey)).thenReturn(true);
        when(gameCache.get(cacheKey)).thenReturn(games);

        // Act
        List<Game> result = gameService.getGamesByRatingRange(3, 5);

        // Assert
        assertEquals(games, result);
        verify(gameCache).containsKey(cacheKey);
        verify(gameCache).get(cacheKey);
        verify(gameRepository, never()).findGamesByRatingRange(anyInt(), anyInt());
    }

    @Test
    void getGamesByRatingRange_ShouldReturnGamesFromRepository_WhenGamesNotInCache() {
        // Arrange
        String cacheKey = "games:rating_range:3:5";
        List<Game> games = Collections.singletonList(testGame);

        when(gameCache.containsKey(cacheKey)).thenReturn(false);
        when(gameRepository.findGamesByRatingRange(3, 5)).thenReturn(games);

        // Act
        List<Game> result = gameService.getGamesByRatingRange(3, 5);

        // Assert
        assertEquals(games, result);
        verify(gameCache).containsKey(cacheKey);
        verify(gameRepository).findGamesByRatingRange(3, 5);
        verify(gameCache).put(cacheKey, games);
    }

    @Test
    void clearCache_ShouldClearCache() {
        // Act
        gameService.clearCache();

        // Assert
        verify(gameCache).clear();
    }
}