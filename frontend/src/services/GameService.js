import ApiClient from './ApiClient';
import ReviewService from './ReviewService';
import CompanyService from './CompanyService';

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
     * @param {boolean} forceRefresh - Принудительное обновление кэша
     * @returns {Promise<Object>} - Promise с данными игры
     */
    getGameById(id, forceRefresh = false) {
        const params = forceRefresh ? { _cache: Date.now() } : {};
        return this.apiClient.get(`${this.endpoint}/${id}`, params);
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
        return this.apiClient.put(`${this.endpoint}/${id}`, gameData)
            .then(response => {
                // После обновления игры, также инвалидируем кэш для всех связанных компаний
                this.invalidateRelatedCompaniesCache(id);
                return response;
            });
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
        return this.apiClient.patch(`${this.endpoint}/${id}`, gameData)
            .then(response => {
                // После обновления игры, также инвалидируем кэш для всех связанных компаний
                this.invalidateRelatedCompaniesCache(id);
                return response;
            });
    }

    /**
     * Инвалидировать кэш для всех компаний, связанных с игрой
     * @param {number} gameId - ID игры
     * @private
     */
    async invalidateRelatedCompaniesCache(gameId) {
        try {
            console.log(`Invalidating cache for companies related to game ${gameId}`);
            // Получаем текущие компании игры
            const companies = await this.getGameCompanies(gameId, true);

            // Инвалидируем кэш для каждой компании
            if (Array.isArray(companies)) {
                companies.forEach(company => {
                    if (company && company.id) {
                        console.log(`Invalidating cache for company ${company.id}`);
                        CompanyService.invalidateCompanyCache(company.id);
                    }
                });
            }
        } catch (error) {
            console.error(`Error invalidating related companies cache:`, error);
        }
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
        // Сначала сохраняем список связанных компаний
        return this.getGameCompanies(id, true)
            .then(companies => {
                return this.apiClient.delete(`${this.endpoint}/${id}`)
                    .then(() => {
                        // После удаления инвалидируем кэш всех связанных компаний
                        if (Array.isArray(companies)) {
                            companies.forEach(company => {
                                if (company && company.id) {
                                    CompanyService.invalidateCompanyCache(company.id);
                                }
                            });
                        }
                    });
            });
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
        return this.apiClient.post(`${this.endpoint}/${gameId}/companies/${companyId}`)
            .then(response => {
                // Инвалидируем кэш компании
                CompanyService.invalidateCompanyCache(companyId);
                return response;
            });
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
        return this.apiClient.delete(`${this.endpoint}/${gameId}/companies/${companyId}`)
            .then(response => {
                // Инвалидируем кэш компании
                CompanyService.invalidateCompanyCache(companyId);
                return response;
            });
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
     * @param {boolean} forceRefresh - Принудительное обновление кэша
     * @returns {Promise<Object>} - Promise с данными игры и компаниями
     */
    getGameWithCompanies = async (id, forceRefresh = false) => {
        try {
            // Проверяем, что id - это число
            if (!id) {
                throw new Error('Game ID is required');
            }

            console.log(`Fetching game with ID ${id} including companies, forceRefresh=${forceRefresh}...`);

            // Сначала пробуем получить игру обычным способом
            const game = await this.getGameById(id, forceRefresh);

            if (!game) {
                throw new Error('Game not found');
            }

            // Отдельно запрашиваем компании с принудительным обновлением
            try {
                const companies = await this.getGameCompanies(id, forceRefresh);
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
     * @param {boolean} [forceRefresh=false] - Принудительное обновление данных (добавление noCache)
     * @returns {Promise<Array>} - Promise со списком компаний
     */
    getGameCompanies(gameId, forceRefresh = false) {
        const params = forceRefresh ? { noCache: Date.now() } : {};

        return this.apiClient.get(`${this.endpoint}/${gameId}/companies`, params)
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

    /**
     * Полностью обновить информацию о компаниях игры
     * @param {number} gameId - ID игры
     * @param {Array<number>} companyIds - Массив ID компаний
     * @returns {Promise<Object>} - Promise с обновленными данными игры
     */
    updateGameCompanies = async (gameId, companyIds) => {
        try {
            // Сначала получаем текущие компании игры
            const currentCompanies = await this.getGameCompanies(gameId, true);
            const currentCompanyIds = currentCompanies.map(c => c.id);

            // Выявляем компании для удаления
            const companiesToRemove = currentCompanyIds.filter(id => !companyIds.includes(id));

            // Выявляем компании для добавления
            const companiesToAdd = companyIds.filter(id => !currentCompanyIds.includes(id));

            // Последовательно удаляем компании
            for (const companyId of companiesToRemove) {
                await this.removeCompanyFromGame(gameId, companyId);
            }

            // Последовательно добавляем компании
            for (const companyId of companiesToAdd) {
                await this.addCompanyToGame(gameId, companyId);
            }

            // Возвращаем обновленную игру с компаниями
            return this.getGameWithCompanies(gameId);
        } catch (error) {
            console.error('Error updating game companies:', error);
            throw error;
        }
    }
}

export default new GameService();