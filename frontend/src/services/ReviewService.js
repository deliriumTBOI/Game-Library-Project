import ApiClient from './ApiClient';

/**
 * Сервис для работы с отзывами
 */
class ReviewService {
    constructor() {
        this.apiClient = new ApiClient();
    }

    /**
     * Создать новый отзыв для игры, указанной по названию
     * @param {string} gameTitle - Название игры
     * @param {Object} reviewData - Данные отзыва
     * @returns {Promise<Object>} - Promise с данными созданного отзыва
     */
    createReviewForGameByTitle(gameTitle, reviewData) {
        return this.apiClient.post(`/games/by-title/${encodeURIComponent(gameTitle)}/reviews`, reviewData);
    }

    /**
     * Создать новый отзыв для игры
     * @param {number} gameId - ID игры
     * @param {Object} reviewData - Данные отзыва
     * @returns {Promise<Object>} - Promise с данными созданного отзыва
     */
    createReview(gameId, reviewData) {
        return this.apiClient.post(`/games/${gameId}/reviews`, reviewData);
    }

    /**
     * Создать несколько отзывов для игры по названию
     * @param {string} gameTitle - Название игры
     * @param {Array} reviewsData - Массив с данными отзывов
     * @returns {Promise<Array>} - Promise с данными созданных отзывов
     */
    createReviewsForGameByTitle(gameTitle, reviewsData) {
        return this.apiClient.post(`/games/by-title/${encodeURIComponent(gameTitle)}/reviews/bulk`, reviewsData);
    }

    /**
     * Создать несколько отзывов для игры
     * @param {number} gameId - ID игры
     * @param {Array} reviewsData - Массив с данными отзывов
     * @returns {Promise<Array>} - Promise с данными созданных отзывов
     */
    createReviews(gameId, reviewsData) {
        return this.apiClient.post(`/games/${gameId}/reviews/bulk`, reviewsData);
    }

    /**
     * Получить отзыв по автору и игре
     * @param {string} gameTitle - Название игры
     * @param {string} author - Автор отзыва
     * @returns {Promise<Object>} - Promise с данными отзыва
     */
    getReviewByAuthorAndGameTitle(gameTitle, author) {
        return this.apiClient.get(`/games/by-title/${encodeURIComponent(gameTitle)}/reviews/by-author/${encodeURIComponent(author)}`);
    }

    /**
     * Получить отзыв по ID
     * @param {number} gameId - ID игры
     * @param {number} reviewId - ID отзыва
     * @returns {Promise<Object>} - Promise с данными отзыва
     */
    getReviewById(gameId, reviewId) {
        return this.apiClient.get(`/games/${gameId}/reviews/${reviewId}`);
    }

    /**
     * Получить все отзывы для игры по названию
     * @param {string} gameTitle - Название игры
     * @returns {Promise<Array>} - Promise со списком отзывов
     */
    getReviewsByGameTitle(gameTitle) {
        return this.apiClient.get(`/games/by-title/${encodeURIComponent(gameTitle)}/reviews`);
    }

    /**
     * Получить все отзывы для игры
     * @param {number} gameId - ID игры
     * @returns {Promise<Array>} - Promise со списком отзывов
     */
    getReviewsByGameId(gameId) {
        return this.apiClient.get(`/games/${gameId}/reviews`);
    }

    /**
     * Обновить отзыв по автору
     * @param {string} gameTitle - Название игры
     * @param {string} author - Автор отзыва
     * @param {Object} reviewData - Новые данные отзыва
     * @returns {Promise<Object>} - Promise с обновленными данными отзыва
     */
    updateReviewByAuthor(gameTitle, author, reviewData) {
        return this.apiClient.put(
            `/games/by-title/${encodeURIComponent(gameTitle)}/reviews/by-author/${encodeURIComponent(author)}`,
            reviewData
        );
    }

    /**
     * Обновить отзыв
     * @param {number} gameId - ID игры
     * @param {number} reviewId - ID отзыва
     * @param {Object} reviewData - Новые данные отзыва
     * @returns {Promise<Object>} - Promise с обновленными данными отзыва
     */
    updateReview(gameId, reviewId, reviewData) {
        return this.apiClient.put(`/games/${gameId}/reviews/${reviewId}`, reviewData);
    }

    /**
     * Удалить отзыв по автору
     * @param {string} gameTitle - Название игры
     * @param {string} author - Автор отзыва
     * @returns {Promise<void>} - Promise с результатом удаления
     */
    deleteReviewByAuthor(gameTitle, author) {
        return this.apiClient.delete(`/games/by-title/${encodeURIComponent(gameTitle)}/reviews/by-author/${encodeURIComponent(author)}`);
    }

    /**
     * Удалить отзыв
     * @param {number} gameId - ID игры
     * @param {number} reviewId - ID отзыва
     * @returns {Promise<void>} - Promise с результатом удаления
     */
    deleteReview(gameId, reviewId) {
        return this.apiClient.delete(`/games/${gameId}/reviews/${reviewId}`);
    }
}

export default new ReviewService();