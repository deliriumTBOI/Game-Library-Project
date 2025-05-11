import ApiClient from './ApiClient';
import GameService from './GameService';
import CompanyService from './CompanyService';
import ReviewService from './ReviewService';

// Экспорт всех сервисов
export {
    ApiClient,
    GameService,
    CompanyService,
    ReviewService
};

// Экспорт объекта с сервисами
export default {
    game: GameService,
    company: CompanyService,
    review: ReviewService
};