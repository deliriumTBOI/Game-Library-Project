package com.gamelib.gamelib;

import com.gamelib.gamelib.service.impl.ReviewServiceImpl;
import com.gamelib.gamelib.cache.LruCache;
import com.gamelib.gamelib.exception.ResourceNotFoundException;
import com.gamelib.gamelib.model.Game;
import com.gamelib.gamelib.model.Review;
import com.gamelib.gamelib.dto.ReviewDto;
import com.gamelib.gamelib.mapper.ReviewMapper;
import com.gamelib.gamelib.repository.GameRepository;
import com.gamelib.gamelib.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Mock
    private LruCache<String, Review> reviewCache;

    private Game testGame;
    private Review testReview;
    private ReviewDto testReviewDto;
    private final ReviewMapper reviewMapper = new ReviewMapper();

    @BeforeEach
    void setUp() {
        testGame = new Game();
        testGame.setId(1L);
        testGame.setTitle("Test Game");

        testReview = new Review();
        testReview.setId(1L);
        testReview.setRating(5);
        testReview.setText("Test Review");
        testReview.setAuthor("Test Author");
        testReview.setGame(testGame);

        testReviewDto = new ReviewDto();
        testReviewDto.setId(1L);
        testReviewDto.setRating(5);
        testReviewDto.setText("Test Review");
        testReviewDto.setAuthor("Test Author");
        testReviewDto.setGameId(1L);

        // Replace the autowired cache with our mock
        ReflectionTestUtils.setField(reviewService, "reviewCache", reviewCache);
    }

    @Test
    void createReview_ShouldCreateReview_WhenGameExists() {
        // Arrange
        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // Act
        Review result = reviewService.createReview(1L, testReview);

        // Assert
        assertEquals(testReview, result);
        assertEquals(testGame, result.getGame());
        verify(gameRepository).findById(1L);
        verify(reviewRepository).save(testReview);
    }

    @Test
    void createReview_ShouldThrowResourceNotFoundException_WhenGameDoesNotExist() {
        // Arrange
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.createReview(1L, testReview));

        assertEquals("Game not found with id: 1", exception.getMessage());
        verify(gameRepository).findById(1L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void createReviews_ShouldCreateReviews_WhenGameExists() {
        // Arrange
        Review review1 = new Review();
        review1.setRating(4);
        review1.setText("Review 1");

        Review review2 = new Review();
        review2.setRating(5);
        review2.setText("Review 2");

        List<Review> reviews = Arrays.asList(review1, review2);

        when(gameRepository.findById(1L)).thenReturn(Optional.of(testGame));
        when(reviewRepository.saveAll(reviews)).thenReturn(reviews);

        // Act
        List<Review> result = reviewService.createReviews(1L, reviews);

        // Assert
        assertEquals(reviews, result);
        for (Review review : result) {
            assertEquals(testGame, review.getGame());
        }
        verify(gameRepository).findById(1L);
        verify(reviewRepository).saveAll(reviews);
    }

    @Test
    void createReviews_ShouldThrowResourceNotFoundException_WhenGameDoesNotExist() {
        // Arrange
        List<Review> reviews = Collections.singletonList(testReview);
        when(gameRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.createReviews(1L, reviews));

        assertEquals("Game not found with id: 1", exception.getMessage());
        verify(gameRepository).findById(1L);
        verify(reviewRepository, never()).saveAll(any());
    }

    @Test
    void getReviewById_ShouldReturnCachedReview_WhenReviewInCache() {
        // Arrange
        String cacheKey = "review:id:1";
        when(reviewCache.containsKey(cacheKey)).thenReturn(true);
        when(reviewCache.get(cacheKey)).thenReturn(testReview);

        // Act
        Optional<Review> result = reviewService.getReviewById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testReview, result.get());
        verify(reviewCache).containsKey(cacheKey);
        verify(reviewCache).get(cacheKey);
        verify(reviewRepository, never()).findById(anyLong());
    }

    @Test
    void getReviewById_ShouldReturnReviewFromRepository_WhenReviewNotInCache() {
        // Arrange
        String cacheKey = "review:id:1";
        when(reviewCache.containsKey(cacheKey)).thenReturn(false);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        // Act
        Optional<Review> result = reviewService.getReviewById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testReview, result.get());
        verify(reviewCache).containsKey(cacheKey);
        verify(reviewRepository).findById(1L);
        verify(reviewCache).put(cacheKey, testReview);
    }

    @Test
    void getReviewsByGameId_ShouldReturnReviews_WhenGameExists() {
        // Arrange
        List<Review> reviews = Collections.singletonList(testReview);
        when(gameRepository.existsById(1L)).thenReturn(true);
        when(reviewRepository.findByGameId(1L)).thenReturn(reviews);

        // Act
        List<Review> result = reviewService.getReviewsByGameId(1L);

        // Assert
        assertEquals(reviews, result);
        verify(gameRepository).existsById(1L);
        verify(reviewRepository).findByGameId(1L);
    }

    @Test
    void getReviewsByGameId_ShouldThrowResourceNotFoundException_WhenGameDoesNotExist() {
        // Arrange
        when(gameRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.getReviewsByGameId(1L));

        assertEquals("Game not found with id: 1", exception.getMessage());
        verify(gameRepository).existsById(1L);
        verify(reviewRepository, never()).findByGameId(anyLong());
    }

    @Test
    void updateReview_ShouldUpdateReview_WhenReviewExistsForGame() {
        // Arrange
        Review updatedReview = new Review();
        updatedReview.setRating(4);
        updatedReview.setText("Updated Review");
        updatedReview.setAuthor("Updated Author");

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // Act
        Review result = reviewService.updateReview(1L, 1L, updatedReview);

        // Assert
        assertEquals(4, result.getRating());
        assertEquals("Updated Review", result.getText());
        assertEquals("Updated Author", result.getAuthor());
        verify(reviewRepository).findById(1L);
        verify(reviewRepository).save(testReview);
        verify(reviewCache).put("review:id:1", result);
    }

    @Test
    void updateReview_ShouldThrowResourceNotFoundException_WhenReviewDoesNotExist() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.updateReview(1L, 1L, testReview));

        assertEquals("Review not found with id: 1", exception.getMessage());
        verify(reviewRepository).findById(1L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void updateReview_ShouldThrowResourceNotFoundException_WhenReviewNotBelongToGame() {
        // Arrange
        Game differentGame = new Game();
        differentGame.setId(2L);

        Review reviewFromDifferentGame = new Review();
        reviewFromDifferentGame.setId(1L);
        reviewFromDifferentGame.setGame(differentGame);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(reviewFromDifferentGame));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.updateReview(1L, 1L, testReview));

        assertEquals("Review with id 1 does not belong to game with id 1", exception.getMessage());
        verify(reviewRepository).findById(1L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void deleteReview_ShouldDeleteReview_WhenReviewExistsForGame() {
        // Arrange
        when(gameRepository.existsById(1L)).thenReturn(true);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        // Act
        boolean result = reviewService.deleteReview(1L, 1L);

        // Assert
        assertTrue(result);
        verify(gameRepository).existsById(1L);
        verify(reviewRepository).findById(1L);
        verify(reviewRepository).deleteById(1L);
        verify(reviewCache).remove("review:id:1");
    }

    @Test
    void deleteReview_ShouldThrowResourceNotFoundException_WhenGameDoesNotExist() {
        // Arrange
        when(gameRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> reviewService.deleteReview(1L, 1L));

        assertEquals("Game not found with id: 1", exception.getMessage());
        verify(gameRepository).existsById(1L);
        verify(reviewRepository, never()).findById(anyLong());
        verify(reviewRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteReview_ShouldReturnFalse_WhenReviewDoesNotExist() {
        // Arrange
        when(gameRepository.existsById(1L)).thenReturn(true);
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        boolean result = reviewService.deleteReview(1L, 1L);

        // Assert
        assertFalse(result);
        verify(gameRepository).existsById(1L);
        verify(reviewRepository).findById(1L);
        verify(reviewRepository, never()).deleteById(anyLong());
        verify(reviewCache, never()).remove(anyString());
    }

    @Test
    void deleteReview_ShouldReturnFalse_WhenReviewNotBelongToGame() {
        // Arrange
        Game differentGame = new Game();
        differentGame.setId(2L);

        Review reviewFromDifferentGame = new Review();
        reviewFromDifferentGame.setId(1L);
        reviewFromDifferentGame.setGame(differentGame);

        when(gameRepository.existsById(1L)).thenReturn(true);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(reviewFromDifferentGame));

        // Act
        boolean result = reviewService.deleteReview(1L, 1L);

        // Assert
        assertFalse(result);
        verify(gameRepository).existsById(1L);
        verify(reviewRepository).findById(1L);
        verify(reviewRepository, never()).deleteById(anyLong());
        verify(reviewCache, never()).remove(anyString());
    }

    @Test
    void reviewMapper_toDto_ShouldCorrectlyMapReviewToDto() {
        // Act
        ReviewDto result = reviewMapper.toDto(testReview);

        // Assert
        assertEquals(testReview.getId(), result.getId());
        assertEquals(testReview.getRating(), result.getRating());
        assertEquals(testReview.getText(), result.getText());
        assertEquals(testReview.getAuthor(), result.getAuthor());
        assertEquals(testReview.getGame().getId(), result.getGameId());
    }

    @Test
    void reviewMapper_toEntity_ShouldCorrectlyMapDtoToReview() {
        // Act
        Review result = reviewMapper.toEntity(testReviewDto);

        // Assert
        assertEquals(testReviewDto.getId(), result.getId());
        assertEquals(testReviewDto.getRating(), result.getRating());
        assertEquals(testReviewDto.getText(), result.getText());
        assertEquals(testReviewDto.getAuthor(), result.getAuthor());

        assertNull(result.getGame());
    }
}