import React, { useState, useEffect } from 'react';
import { Typography, List, Rate, Spin, message } from 'antd';
import { ReviewService } from '../services';

const { Title, Paragraph } = Typography;

const ReviewsPage = () => {
    const [reviews, setReviews] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchReviews = async () => {
            try {
                // Здесь можно использовать другой метод, если нужно получить все отзывы
                // Например, если есть метод getAllReviews в ReviewService
                const data = await ReviewService.getReviewsByGameId(1); // Пример для демонстрации
                setReviews(data);
            } catch (err) {
                setError('Failed to load reviews');
                message.error('Failed to load reviews');
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        fetchReviews();
    }, []);

    if (loading) return <Spin size="large" />;
    if (error) return <Paragraph style={{ color: '#ff6b6b' }}>{error}</Paragraph>;

    return (
        <div>
            <Title level={2} style={{ color: '#66c0f4' }}>Game Reviews</Title>
            <Paragraph style={{ color: '#c7d5e0' }}>
                Read what players think about different games.
            </Paragraph>

            <List
                dataSource={reviews}
                renderItem={review => (
                    <List.Item style={{ color: '#c7d5e0', borderBottom: '1px solid #2a475e' }}>
                        <List.Item.Meta
                            title={<span style={{ color: '#66c0f4' }}>{review.game?.title || 'Unknown Game'}</span>}
                            description={
                                <>
                                    <Rate disabled defaultValue={review.rating} style={{ color: '#66c0f4' }} />
                                    <p>{review.text}</p>
                                    <small>By: {review.author}</small>
                                </>
                            }
                        />
                    </List.Item>
                )}
            />
        </div>
    );
};

export default ReviewsPage;