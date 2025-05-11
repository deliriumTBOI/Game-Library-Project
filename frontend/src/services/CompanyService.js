import ApiClient from './ApiClient';

/**
 * Сервис для работы с компаниями
 */
class CompanyService {
    constructor() {
        this.apiClient = new ApiClient();
        this.endpoint = '/companies';
    }

    /**
     * Получить все компании
     * @returns {Promise<Array>} - Promise со списком компаний
     */
    getAllCompanies() {
        return this.apiClient.get(this.endpoint);
    }

    getCompanyWithGames = async (id) => {
        const company = await this.getCompanyById(id);
        const games = await this.getGamesForCompany(id);
        return { ...company, games };
    }

    /**
     * Получить компании по названию (частичное совпадение)
     * @param {string} name - Название компании
     * @returns {Promise<Array>} - Promise со списком компаний
     */
    getCompaniesByName(name) {
        return this.apiClient.get(this.endpoint, { name });
    }

    /**
     * Найти компанию по точному названию
     * @param {string} exactName - Точное название компании
     * @returns {Promise<Object>} - Promise с данными компании
     */
    findCompanyByExactName(exactName) {
        return this.apiClient.get(`${this.endpoint}/find`, { exactName });
    }

    /**
     * Получить компанию по ID
     * @param {number} id - ID компании
     * @returns {Promise<Object>} - Promise с данными компании
     */
    getCompanyById(id) {
        return this.apiClient.get(`${this.endpoint}/${id}`);
    }

    /**
     * Создать новую компанию
     * @param {Object} companyData - Данные компании
     * @returns {Promise<Object>} - Promise с данными созданной компании
     */
    createCompany(companyData) {
        return this.apiClient.post(this.endpoint, companyData);
    }

    /**
     * Создать несколько компаний
     * @param {Array} companiesData - Массив с данными компаний
     * @returns {Promise<Array>} - Promise с данными созданных компаний
     */
    createCompanies(companiesData) {
        return this.apiClient.post(`${this.endpoint}/bulk`, companiesData);
    }

    /**
     * Обновить компанию по названию
     * @param {string} name - Название компании
     * @param {Object} companyData - Новые данные компании
     * @returns {Promise<Object>} - Promise с обновленными данными компании
     */
    updateCompanyByName(name, companyData) {
        return this.apiClient.put(`${this.endpoint}/by-name/${encodeURIComponent(name)}`, companyData);
    }

    /**
     * Обновить компанию по ID
     * @param {number} id - ID компании
     * @param {Object} companyData - Новые данные компании
     * @returns {Promise<Object>} - Promise с обновленными данными компании
     */
    updateCompany(id, companyData) {
        return this.apiClient.put(`${this.endpoint}/${id}`, companyData);
    }

    /**
     * Удалить компанию по названию
     * @param {string} name - Название компании
     * @returns {Promise<void>} - Promise с результатом удаления
     */
    deleteCompanyByName(name) {
        return this.apiClient.delete(`${this.endpoint}/by-name/${encodeURIComponent(name)}`);
    }

    /**
     * Удалить компанию по ID
     * @param {number} id - ID компании
     * @returns {Promise<void>} - Promise с результатом удаления
     */
    deleteCompany(id) {
        return this.apiClient.delete(`${this.endpoint}/${id}`);
    }

    /**
     * Получить игры компании по названию компании
     * @param {string} companyName - Название компании
     * @returns {Promise<Array>} - Promise со списком игр компании
     */
    getGamesForCompanyByName(companyName) {
        return this.apiClient.get(`${this.endpoint}/by-name/${encodeURIComponent(companyName)}/games`);
    }

    /**
     * Получить игры компании по ID
     * @param {number} companyId - ID компании
     * @returns {Promise<Array>} - Promise со списком игр компании
     */
    getGamesForCompany(companyId) {
        return this.apiClient.get(`${this.endpoint}/${companyId}/games`);
    }

    /**
     * Добавить игру к компании по названию
     * @param {string} companyName - Название компании
     * @param {string} gameTitle - Название игры
     * @returns {Promise<Object>} - Promise с обновленными данными компании
     */
    addGameToCompanyByNames(companyName, gameTitle) {
        return this.apiClient.post(
            `${this.endpoint}/by-name/${encodeURIComponent(companyName)}/games/by-title/${encodeURIComponent(gameTitle)}`
        );
    }

    /**
     * Добавить игру к компании по ID
     * @param {number} companyId - ID компании
     * @param {number} gameId - ID игры
     * @returns {Promise<Object>} - Promise с обновленными данными компании
     */
    addGameToCompany(companyId, gameId) {
        return this.apiClient.post(`${this.endpoint}/${companyId}/games/${gameId}`);
    }

    /**
     * Удалить игру из компании по названию
     * @param {string} companyName - Название компании
     * @param {string} gameTitle - Название игры
     * @returns {Promise<void>} - Promise с результатом удаления
     */
    removeGameFromCompanyByNames(companyName, gameTitle) {
        return this.apiClient.delete(
            `${this.endpoint}/by-name/${encodeURIComponent(companyName)}/games/by-title/${encodeURIComponent(gameTitle)}`
        );
    }

    /**
     * Удалить игру из компании по ID
     * @param {number} companyId - ID компании
     * @param {number} gameId - ID игры
     * @returns {Promise<void>} - Promise с результатом удаления
     */
    removeGameFromCompany(companyId, gameId) {
        return this.apiClient.delete(`${this.endpoint}/${companyId}/games/${gameId}`);
    }
}

export default new CompanyService();