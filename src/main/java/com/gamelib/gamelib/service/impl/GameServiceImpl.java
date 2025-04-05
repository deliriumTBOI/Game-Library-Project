package com.gamelib.gamelib.service.impl;

import com.gamelib.gamelib.exception.ResourceAlreadyExistsException;
import com.gamelib.gamelib.exception.ResourceNotFoundException;
import com.gamelib.gamelib.model.Company;
import com.gamelib.gamelib.model.Game;
import com.gamelib.gamelib.repository.CompanyRepository;
import com.gamelib.gamelib.repository.GameRepository;
import com.gamelib.gamelib.service.GameService;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;
    private final CompanyRepository companyRepository;

    public GameServiceImpl(GameRepository gameRepository, CompanyRepository companyRepository) {
        this.gameRepository = gameRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    @Transactional
    public Game createGame(Game game) {
        // Проверяем уникальность игры по названию
        if (gameRepository.existsByTitle(game.getTitle())) {
            throw new ResourceAlreadyExistsException("Game is already exist");
        }
        return gameRepository.save(game);
    }

    @Override
    public Optional<Game> getGameById(Long id) {
        return gameRepository.findById(id);
    }

    @Override
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    @Override
    public List<Game> getGamesByTitle(String title) {
        return gameRepository.findByTitleContainingIgnoreCase(title);
    }

    @Override
    @Transactional
    public Game updateGame(Long id, Game updatedGame) {
        Game existingGame = gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + id));

        // Проверка на дублирование, если название меняется
        if (!existingGame.getTitle().equals(updatedGame.getTitle()) &&
                gameRepository.existsByTitle(updatedGame.getTitle())) {
            throw new ResourceAlreadyExistsException("Game with title " + updatedGame.getTitle() + " already exists");
        }

        // Обновляем все поля
        existingGame.setTitle(updatedGame.getTitle());
        existingGame.setDescription(updatedGame.getDescription());
        existingGame.setReleaseDate(updatedGame.getReleaseDate());
        existingGame.setGenre(updatedGame.getGenre());

        // Сохраняем существующие связи
        // НЕ обновляем компании через этот метод
        // НЕ обновляем отзывы через этот метод

        return gameRepository.save(existingGame);
    }

    @Override
    @Transactional
    public Game patchGame(Long id, Game partialGame) {
        Game existingGame = gameRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + id));

        // Обновляем только не null поля
        if (StringUtils.hasText(partialGame.getTitle())) {
            // Проверка на дублирование при изменении названия
            if (!existingGame.getTitle().equals(partialGame.getTitle()) &&
                    gameRepository.existsByTitle(partialGame.getTitle())) {
                throw new ResourceAlreadyExistsException("Game with title " + partialGame.getTitle() + " already exists");
            }
            existingGame.setTitle(partialGame.getTitle());
        }

        if (StringUtils.hasText(partialGame.getDescription())) {
            existingGame.setDescription(partialGame.getDescription());
        }

        if (partialGame.getReleaseDate() != null) {
            existingGame.setReleaseDate(partialGame.getReleaseDate());
        }

        if (StringUtils.hasText(partialGame.getGenre())) {
            existingGame.setGenre(partialGame.getGenre());
        }

        return gameRepository.save(existingGame);
    }

    @Override
    @Transactional
    public boolean deleteGame(Long id) {
        if (gameRepository.existsById(id)) {
            gameRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public Game addCompanyToGame(Long gameId, Long companyId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        // Добавляем компанию к игре
        game.getCompanies().add(company);

        return gameRepository.save(game);
    }

    @Override
    @Transactional
    public boolean removeCompanyFromGame(Long gameId, Long companyId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found with id: " + gameId));

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        // Удаляем компанию из игры
        boolean removed = game.getCompanies().remove(company);
        if (removed) {
            gameRepository.save(game);
        }

        return removed;
    }
}