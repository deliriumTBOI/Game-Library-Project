import axios from 'axios';

/**
 * Базовый класс для API-запросов
 */
class ApiClient {
    constructor(baseURL) {
        this.api = axios.create({
            baseURL: baseURL || 'http://localhost:8080',
            headers: {
                'Content-Type': 'application/json',
            },
        });

        // Добавляем обработчики ошибок
        this.api.interceptors.response.use(
            (response) => response,
            (error) => {
                // Обработка ошибок API
                const errorMessage = error.response?.data?.message || error.message || 'Unknown error';
                console.error('API Error:', errorMessage);
                return Promise.reject(error);
            }
        );
    }

    /**
     * GET запрос
     * @param {string} url - URL запроса
     * @param {Object} params - Параметры запроса
     * @returns {Promise} - Promise с данными ответа
     */
    get(url, params = {}) {
        return this.api.get(url, { params }).then((response) => response.data);
    }

    /**
     * POST запрос
     * @param {string} url - URL запроса
     * @param {Object} data - Данные для отправки
     * @returns {Promise} - Promise с данными ответа
     */
    post(url, data) {
        return this.api.post(url, data).then((response) => response.data);
    }

    /**
     * PUT запрос
     * @param {string} url - URL запроса
     * @param {Object} data - Данные для обновления
     * @returns {Promise} - Promise с данными ответа
     */
    put(url, data) {
        return this.api.put(url, data).then((response) => response.data);
    }

    /**
     * PATCH запрос
     * @param {string} url - URL запроса
     * @param {Object} data - Данные для частичного обновления
     * @returns {Promise} - Promise с данными ответа
     */
    patch(url, data) {
        return this.api.patch(url, data).then((response) => response.data);
    }

    /**
     * DELETE запрос
     * @param {string} url - URL запроса
     * @returns {Promise} - Promise с данными ответа
     */
    delete(url) {
        return this.api.delete(url).then((response) => response.data);
    }
}

export default ApiClient;