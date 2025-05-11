/**
 * Модели данных для фронтенда, соответствующие бэкенд-моделям
 */

/**
 * @typedef {Object} Game
 * @property {number} id - ID игры
 * @property {string} title - Название игры
 * @property {string} description - Описание игры
 * @property {string} releaseDate - Дата выпуска
 * @property {string} genre - Жанр игры
 * @property {Array<Company>} companies - Компании, связанные с игрой
 * @property {Array<Review>} reviews - Отзывы на игру
 */

/**
 * @typedef {Object} Company
 * @property {number} id - ID компании
 * @property {string} name - Название компании
 * @property {string} description - Описание компании
 * @property {number} foundedYear - Год основания
 * @property {string} website - Веб-сайт компании
 * @property {Array<Game>} games - Игры компании
 */

/**
 * @typedef {Object} Review
 * @property {number} id - ID отзыва
 * @property {number} rating - Рейтинг (оценка)
 * @property {string} text - Текст отзыва
 * @property {string} author - Автор отзыва
 * @property {number} gameId - ID игры, к которой относится отзыв
 */

/**
 * Пустые структуры для создания новых объектов
 */
export const emptyGame = {
    title: '',
    description: '',
    releaseDate: null,
    genre: '',
    companies: [],
    reviews: []
};

export const emptyCompany = {
    name: '',
    description: '',
    foundedYear: null,
    website: '',
    games: []
};

export const emptyReview = {
    rating: 0,
    text: '',
    author: '',
    gameId: null
};

export default {
    emptyGame,
    emptyCompany,
    emptyReview
};