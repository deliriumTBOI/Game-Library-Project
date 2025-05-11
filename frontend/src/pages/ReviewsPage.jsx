import React, { useState, useEffect } from 'react';
import { Typography, List, Spin, message, Input, Row, Col } from 'antd';
import { Link } from 'react-router-dom';
import styled from 'styled-components';
import { GameService, ReviewService } from '../services';

const { Title, Paragraph } = Typography;
const { Search } = Input;

const RatingContainer = styled.div`
    display: flex;
    align-items: center;
    margin-top: 8px;
`;

const RatingCircle = styled.div`
    width: 20px;
    height: 20px;
    border-radius: 50%;
    border: 1px solid ${props => props.active ? getColor(props.value) : '#2a475e'};
    margin-right: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: ${props => props.active ? getColor(props.value) : 'transparent'};
    font-size: 10px;
`;

const StyledSearchContainer = styled.div`
    width: 100%;
    max-width: 500px;
    margin-bottom: 24px;

    .ant-input-affix-wrapper {
        background-color: #16202d;
        border: 1px solid #2a475e;
        border-radius: 4px;
        padding: 6px 11px;
        transition: all 0.3s;

        &:hover, &:focus-within {
            border-color: #66c0f4;
            box-shadow: 0 0 0 2px rgba(102, 192, 244, 0.2);
        }

        .ant-input {
            background: transparent;
            color: #c7d5e0;
            &::placeholder {
                color: #5a7a99;
            }
        }

        .ant-input-clear-icon {
            color: #66c0f4;
            margin-right: 8px;
        }

        .ant-input-prefix {
            margin-right: 8px;
            color: #66c0f4;
        }
    }
`;

const NoResultsMessage = styled.div`
  padding: 20px;
  text-align: center;
  color: #ff6b6b;
  font-size: 16px;
  background-color: #1b2838;
  border-radius: 4px;
  margin-top: 20px;
`;

const GameItem = styled.div`
    padding: 16px;
    margin-bottom: 8px;
    background-color: #1b2838;
    border-radius: 4px;
    transition: all 0.3s;
    border-left: 3px solid #66c0f4;

    &:hover {
        background-color: #2a475e;
        transform: translateX(5px);
    }
`;

const getColor = (value) => {
    const colors = [
        '#ff0000', '#ff3300', '#ff6600', '#ff9900',
        '#ffcc00', '#ffff00', '#ccff00', '#99ff00',
        '#66ff00', '#33ff00', '#00ff00'
    ];
    return colors[Math.min(Math.max(Math.floor(value), 0), 10)];
};

const ReviewsPage = () => {
    const [games, setGames] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');

    useEffect(() => {
        const fetchGamesWithRatings = async () => {
            try {
                const gamesData = await GameService.getAllGames();
                const gamesWithRatings = await Promise.all(
                    gamesData.map(async game => {
                        try {
                            const reviews = await ReviewService.getReviewsByGameId(game.id);
                            const avgRating = reviews.length > 0
                                ? reviews.reduce((sum, review) => sum + review.rating, 0) / reviews.length
                                : 0;

                            return {
                                ...game,
                                rating: Math.round(avgRating * 10) / 10
                            };
                        } catch (err) {
                            console.error(`Failed to load reviews for game ${game.id}`, err);
                            return {
                                ...game,
                                rating: 0
                            };
                        }
                    })
                );
                setGames(gamesWithRatings);
            } catch (err) {
                setError('Failed to load games');
                message.error('Failed to load games');
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        fetchGamesWithRatings();
    }, []);

    const filteredGames = games.filter(game =>
        game.title.toLowerCase().includes(searchTerm.toLowerCase())
    );

    if (loading) return <Spin size="large" />;
    if (error) return <Paragraph style={{ color: '#ff6b6b' }}>{error}</Paragraph>;

    return (
        <div style={{ padding: '20px', maxWidth: '1200px', margin: '0 auto' }}>
            <Title level={2} style={{ color: '#66c0f4', marginBottom: '24px' }}>
                Select Game to View Reviews
            </Title>

            <StyledSearchContainer>
                <Input.Search
                    placeholder="Search games..."
                    allowClear
                    onChange={(e) => setSearchTerm(e.target.value)}
                    style={{ width: '100%' }}
                />
            </StyledSearchContainer>

            {filteredGames.length === 0 ? (
                <NoResultsMessage>
                    No games found matching "{searchTerm}"
                </NoResultsMessage>
            ) : (
                <List
                    dataSource={filteredGames}
                    renderItem={game => (
                        <Link to={`/reviews/${game.id}`} style={{ textDecoration: 'none' }}>
                            <GameItem>
                                <Row justify="space-between" align="middle">
                                    <Col>
                                        <Typography.Text strong style={{ color: '#66c0f4', fontSize: '16px' }}>
                                            {game.title}
                                        </Typography.Text>
                                    </Col>
                                    <Col>
                                        <Typography.Text style={{ color: '#c7d5e0' }}>
                                            Average rating: {game.rating}/10
                                        </Typography.Text>
                                    </Col>
                                </Row>

                                <RatingContainer>
                                    {[...Array(10)].map((_, i) => (
                                        <RatingCircle
                                            key={i}
                                            active={i < game.rating}
                                            value={i}
                                        >
                                            {i+1}
                                        </RatingCircle>
                                    ))}
                                </RatingContainer>
                            </GameItem>
                        </Link>
                    )}
                />
            )}
        </div>
    );
};

export default ReviewsPage;