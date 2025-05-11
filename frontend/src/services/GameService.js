import ApiClient from './ApiClient';
import ReviewService from './ReviewService';

/**
 * Сервис для работы с играми
 */
class GameService {
    constructor() {
        this.apiClient = new ApiClient();
        this.endpoint = '/games';
    }

    /**
     * Получить все игры
     * @returns {Promise<Array>} - Promise со списком игр
     */
    getAllGames() {
        return this.apiClient.get(this.endpoint);
    }

    /**
     * Получить игры по названию
     * @param {string} title - Название игры
     * @returns {Promise<Array>} - Promise со списком игр
     */
    getGamesByTitle(title) {
        return this.apiClient.get(`${this.endpoint}`, { title });
    }

    /**
     * Найти игру по точному названию
     * @param {string} exactTitle - Точное название игры
     * @returns {Promise<Object>} - Promise с данными игры
     */
    findGameByExactTitle(exactTitle) {
        return this.apiClient.get(`${this.endpoint}/find`, { exactTitle });
    }

    /**
     * Получить игру по ID
     * @param {number} id - ID игры
     * @returns {Promise<Object>} - Promise с данными игры
     */
    getGameById(id) {
        return this.apiClient.get(`${this.endpoint}/${id}`);
    }

    /**
     * Создать новую игру
     * @param {Object} gameData - Данные игры
     * @returns {Promise<Object>} - Promise с данными созданной игры
     */
    createGame(gameData) {
        return this.apiClient.post(this.endpoint, gameData);
    }

    /**
     * Создать несколько игр
     * @param {Array} gamesData - Массив с данными игр
     * @returns {Promise<Array>} - Promise с данными созданных игр
     */
    createGames(gamesData) {
        return this.apiClient.post(`${this.endpoint}/bulk`, gamesData);
    }

    /**
     * Обновить игру по названию
     * @param {string} title - Название игры для обновления
     * @param {Object} gameData - Новые данные игры
     * @returns {Promise<Object>} - Promise с обновленными данными игры
     */
    updateGameByTitle(title, gameData) {
        return this.apiClient.put(`${this.endpoint}/by-title/${encodeURIComponent(title)}`, gameData);
    }

    /**
     * Обновить игру по ID
     * @param {number} id - ID игры
     * @param {Object} gameData - Новые данные игры
     * @returns {Promise<Object>} - Promise с обновленными данными игры
     */
    updateGame(id, gameData) {
        return this.apiClient.put(`${this.endpoint}/${id}`, gameData);
    }

    /**
     * Частично обновить игру по названию
     * @param {string} title - Название игры
     * @param {Object} gameData - Частичные данные для обновления
     * @returns {Promise<Object>} - Promise с обновленными данными игры
     */
    patchGameByTitle(title, gameData) {
        return this.apiClient.patch(`${this.endpoint}/by-title/${encodeURIComponent(title)}`, gameData);
    }

    /**
     * Частично обновить игру по ID
     * @param {number} id - ID игры
     * @param {Object} gameData - Частичные данные для обновления
     * @returns {Promise<Object>} - Promise с обновленными данными игры
     */
    patchGame(id, gameData) {
        return this.apiClient.patch(`${this.endpoint}/${id}`, gameData);
    }

    /**
     * Удалить игру по названию
     * @param {string} title - Название игры
     * @returns {Promise<void>} - Promise с результатом удаления
     */
    deleteGameByTitle(title) {
        return this.apiClient.delete(`${this.endpoint}/by-title/${encodeURIComponent(title)}`);
    }

    /**
     * Удалить игру по ID
     * @param {number} id - ID игры
     * @returns {Promise<void>} - Promise с результатом удаления
     */
    deleteGame(id) {
        return this.apiClient.delete(`${this.endpoint}/${id}`);
    }

    /**
     * Добавить компанию к игре по названию игры
     * @param {string} gameTitle - Название игры
     * @param {string} companyName - Название компании
     * @returns {Promise<Object>} - Promise с обновленными данными игры
     */
    addCompanyToGameByNames(gameTitle, companyName) {
        return this.apiClient.post(
            `${this.endpoint}/by-title/${encodeURIComponent(gameTitle)}/companies/by-name/${encodeURIComponent(companyName)}`
        );
    }

    /**
     * Добавить компанию к игре
     * @param {number} gameId - ID игры
     * @param {number} companyId - ID компании
     * @returns {Promise<Object>} - Promise с обновленными данными игры
     */
    addCompanyToGame(gameId, companyId) {
        return this.apiClient.post(`${this.endpoint}/${gameId}/companies/${companyId}`);
    }

    /**
     * Удалить компанию из игры по названиям
     * @param {string} gameTitle - Название игры
     * @param {string} companyName - Название компании
     * @returns {Promise<void>} - Promise с результатом удаления
     */
    removeCompanyFromGameByNames(gameTitle, companyName) {
        return this.apiClient.delete(
            `${this.endpoint}/by-title/${encodeURIComponent(gameTitle)}/companies/by-name/${encodeURIComponent(companyName)}`
        );
    }

    /**
     * Удалить компанию из игры
     * @param {number} gameId - ID игры
     * @param {number} companyId - ID компании
     * @returns {Promise<void>} - Promise с результатом удаления
     */
    removeCompanyFromGame(gameId, companyId) {
        return this.apiClient.delete(`${this.endpoint}/${gameId}/companies/${companyId}`);
    }

    /**
     * Получить игры по рейтингу
     * @param {Object} options - Параметры фильтрации
     * @param {number} [options.minRating] - Минимальный рейтинг
     * @param {number} [options.maxRating] - Максимальный рейтинг
     * @returns {Promise<Array>} - Promise со списком игр
     */
    getGamesByRating({ minRating, maxRating } = {}) {
        const params = {};

        if (minRating !== undefined) {
            params.minRating = minRating;
        }

        if (maxRating !== undefined) {
            params.maxRating = maxRating;
        }

        return this.apiClient.get(`${this.endpoint}/by-rating`, params);
    }

    /**
     * Получить игру вместе с отзывами
     * @param {number} id - ID игры
     * @returns {Promise<Object>} - Promise с данными игры и отзывами
     */
    getGameWithReviews = async (id) => {
        try {
            const game = await this.getGameById(id);
            const reviews = await ReviewService.getReviewsByGameId(id);
            return { ...game, reviews };
        } catch (error) {
            console.error('Error fetching game with reviews:', error);
            throw error;
        }
    }

    /**
     * Получить игру вместе с компаниями
     * @param {number} id - ID игры
     * @returns {Promise<Object>} - Promise с данными игры и компаниями
     */
    getGameWithCompanies = async (id) => {
        try {
            // Проверяем, что id - это число
            if (!id) {
                throw new Error('Game ID is required');
            }

            console.log(`Fetching game with ID ${id} including companies...`);

            // Сначала пробуем получить игру обычным способом
            const game = await this.getGameById(id);

            if (!game) {
                throw new Error('Game not found');
            }

            // Отдельно запрашиваем компании
            try {
                const companies = await this.getGameCompanies(id);
                console.log('Companies data received:', companies);

                // Обрабатываем возможные форматы ответа
                let companiesArray = [];

                if (Array.isArray(companies)) {
                    companiesArray = companies;
                } else if (companies && typeof companies === 'object') {
                    // Если это объект (например, Set), пробуем преобразовать в массив
                    if (Array.isArray(companies.content)) {
                        companiesArray = companies.content;
                    } else {
                        // Если это просто объект с данными одной компании
                        companiesArray = [companies];
                    }
                }

                // Проверяем, что каждый элемент массива имеет id и name
                companiesArray = companiesArray.filter(company =>
                    company && (company.id !== undefined || company.name)
                );

                console.log('Processed companies array:', companiesArray);

                return {
                    ...game,
                    companies: companiesArray
                };
            } catch (companiesError) {
                console.warn('Failed to fetch companies separately:', companiesError);
                // Если не смогли получить компании отдельно, проверяем есть ли они уже в данных игры
                if (game.companies) {
                    const existingCompanies = Array.isArray(game.companies)
                        ? game.companies
                        : (game.companies ? [game.companies] : []);

                    return {
                        ...game,
                        companies: existingCompanies
                    };
                }

                // Возвращаем игру с пустым массивом компаний
                return {
                    ...game,
                    companies: []
                };
            }
        } catch (error) {
            console.error('Error in getGameWithCompanies:', error);
            throw error;
        }
    }

    /**
     * Получить компании игры по ID игры
     * @param {number} gameId - ID игры
     * @returns {Promise<Array>} - Promise со списком компаний
     */
    getGameCompanies(gameId) {
        return this.apiClient.get(`${this.endpoint}/${gameId}/companies`)
            .then(response => {
                // Самый простой случай - массив компаний
                if (Array.isArray(response)) {
                    return response;
                }

                // Если ответ - объект с полем content (пагинация)
                if (response && response.content && Array.isArray(response.content)) {
                    return response.content;
                }

                // Если это один объект компании
                if (response && (response.id || response.name)) {
                    return [response];
                }

                // По умолчанию возвращаем пустой массив
                return [];
            })
            .catch(error => {
                console.error('Error fetching game companies:', error);
                return [];
            });
    }
}

export default new GameService();