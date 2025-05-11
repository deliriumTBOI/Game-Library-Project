import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Typography, List, Spin, message, Button, Form, Input, Divider, Card, Modal } from 'antd';
import { ReviewService, GameService } from '../services';
import styled from 'styled-components';

const { Title, Text, Paragraph } = Typography;
const { TextArea } = Input;

const ReviewCard = styled(Card)`
    background-color: #1B2838;
    border: 1px solid #2a475e;
    margin-bottom: 16px;
    border-radius: 8px;

    .ant-card-body {
        padding: 16px;
    }
`;

const StyledForm = styled(Form)`
    .ant-form-item-label > label {
        color: #c7d5e0 !important;
    }

    .ant-input, .ant-input-affix-wrapper {
        background-color: #16202d;
        border-color: #2a475e;
        color: #c7d5e0;

        &:hover, &:focus {
            border-color: #66c0f4;
            box-shadow: 0 0 0 2px rgba(102, 192, 244, 0.2);
        }
    }

    .ant-input::placeholder {
        color: #5a7a99;
    }
`;

const RatingContainer = styled.div`
    display: flex;
    align-items: center;
    margin: 8px 0;
`;

const RatingCircle = styled.div`
    width: 24px;
    height: 24px;
    border-radius: 50%;
    border: 1px solid ${props => props.active ? getColor(props.value) : '#2a475e'};
    margin-right: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: ${props => props.active ? getColor(props.value) : 'transparent'};
    font-size: 12px;
    font-weight: bold;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
        transform: scale(1.1);
    }
`;

const StyledModal = styled(Modal)`
  .ant-modal-content {
    background-color: #1B2838 !important;
    border: 1px solid #2a475e !important;
    color: #c7d5e0 !important;
  }

  .ant-modal-header {
    background-color: #1B2838 !important;
    border-bottom: 1px solid #2a475e !important;
    
    .ant-modal-title {
      color: #66c0f4 !important;
    }
  }

  .ant-modal-footer {
    background-color: #1B2838 !important;
    border-top: 1px solid #2a475e !important;
    padding: 16px 24px !important;
    margin-top: 8px !important;
    
    .ant-btn-default {
      color: #c7d5e0 !important;
      border-color: #2a475e !important;
      background: transparent !important;
    }
  }

  .ant-modal-close {
    color: #c7d5e0 !important;
    
    &:hover {
      color: #66c0f4 !important;
    }
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

const RatingInput = ({ value, onChange }) => {
    return (
        <RatingContainer>
            {[...Array(10)].map((_, i) => (
                <RatingCircle
                    key={i}
                    active={i < value}
                    value={i+1}
                    onClick={() => onChange(i+1)}
                >
                    {i+1}
                </RatingCircle>
            ))}
            <Text strong style={{ marginLeft: 8, color: '#66c0f4' }}>{value}/10</Text>
        </RatingContainer>
    );
};

const ReviewDetail = () => {
    const { gameId } = useParams();
    const navigate = useNavigate();
    const [reviews, setReviews] = useState([]);
    const [game, setGame] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [form] = Form.useForm();
    const [deleteConfirmVisible, setDeleteConfirmVisible] = useState(false);
    const [reviewToDelete, setReviewToDelete] = useState(null);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [gameData, reviewsData] = await Promise.all([
                    GameService.getGameById(gameId),
                    ReviewService.getReviewsByGameId(gameId)
                ]);
                setGame(gameData);
                setReviews(reviewsData);
            } catch (err) {
                setError('Failed to load data');
                message.error('Failed to load data');
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        fetchData();
    }, [gameId]);

    const onFinish = async (values) => {
        try {
            await ReviewService.createReview(gameId, values);
            message.success('Review added successfully');
            const updatedReviews = await ReviewService.getReviewsByGameId(gameId);
            setReviews(updatedReviews);
            form.resetFields();
        } catch (err) {
            message.error('Failed to add review');
            console.error(err);
        }
    };

    const showDeleteConfirm = (review) => {
        setReviewToDelete(review);
        setDeleteConfirmVisible(true);
    };

    const handleDeleteConfirm = async () => {
        try {
            if (!reviewToDelete?.id) {
                message.error('Review ID is missing');
                return;
            }
            await ReviewService.deleteReview(gameId, reviewToDelete.id);
            message.success('Review deleted successfully');
            setReviews(reviews.filter(review => review.id !== reviewToDelete.id));
        } catch (err) {
            message.error('Failed to delete review');
            console.error(err);
        } finally {
            setDeleteConfirmVisible(false);
            setReviewToDelete(null);
        }
    };

    const handleDeleteReview = async (reviewId) => {
        try {
            await ReviewService.deleteReview(gameId, reviewId);
            message.success('Review deleted successfully');
            setReviews(reviews.filter(review => review.id !== reviewId));
        } catch (err) {
            message.error('Failed to delete review');
            console.error(err);
        }
    };

    if (loading) return <Spin size="large" />;
    if (error) return <Paragraph style={{ color: '#ff6b6b' }}>{error}</Paragraph>;

    return (
        <div style={{ padding: '20px', maxWidth: '1200px', margin: '0 auto' }}>
            <Button
                onClick={() => navigate(-1)}
                style={{
                    background: '#2a475e',
                    color: '#66c0f4',
                    borderColor: '#2a475e',
                    marginBottom: 20
                }}
            >
                Back
            </Button>

            <Title level={2} style={{ color: '#66c0f4', marginBottom: 24 }}>
                Reviews for {game?.title}
            </Title>

            <Divider style={{ borderColor: '#2a475e' }} />

            <div style={{ marginBottom: 40 }}>
                <Card
                    title={<Text strong style={{ color: '#66c0f4', fontSize: 18 }}>Add Your Review</Text>}
                    style={{ background: '#1B2838', borderColor: '#2a475e', marginBottom: 24 }}
                >
                    <StyledForm form={form} onFinish={onFinish} layout="vertical">
                        <Form.Item
                            name="author"
                            label="Your Name"
                            rules={[{ required: true, message: 'Please input your name!' }]}
                        >
                            <Input />
                        </Form.Item>
                        <Form.Item
                            name="rating"
                            label="Rating"
                            rules={[{ required: true, message: 'Please select a rating!' }]}
                        >
                            <RatingInput
                                value={form.getFieldValue('rating') || 0}
                                onChange={(value) => form.setFieldsValue({ rating: value })}
                            />
                        </Form.Item>
                        <Form.Item
                            name="text"
                            label="Review"
                            rules={[{ required: true, message: 'Please write your review!' }]}
                        >
                            <TextArea rows={4} />
                        </Form.Item>
                        <Form.Item>
                            <Button
                                type="primary"
                                htmlType="submit"
                                style={{
                                    background: '#4582a4',  // Более темный синий вместо яркого #66c0f4
                                    borderColor: '#4582a4',
                                    color: '#ffffff',      // Белый текст для лучшей читаемости
                                    fontWeight: 500       // Чуть более жирный текст
                                }}
                                onMouseEnter={(e) => e.target.style.background = '#3a6d8a'}  // Темнее при наведении
                                onMouseLeave={(e) => e.target.style.background = '#4582a4'}  // Возвращаем исходный
                            >
                                Submit Review
                            </Button>
                        </Form.Item>
                    </StyledForm>
                </Card>
            </div>

            <Title level={3} style={{ color: '#66c0f4', marginBottom: 16 }}>
                Player Reviews
            </Title>

            {reviews.length === 0 ? (
                <Text style={{ color: '#8F98A0' }}>No reviews yet. Be the first to review!</Text>
            ) : (
                <div>
                    {reviews.map(review => (
                        <ReviewCard key={review.id}>
                            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                                <Text strong style={{ color: '#66c0f4', fontSize: 16 }}>
                                    {review.author}
                                </Text>
                                <Button
                                    type="text"
                                    danger
                                    style={{ padding: 0, height: 'auto' }}
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        showDeleteConfirm(review);
                                    }}
                                >
                                    Delete
                                </Button>
                            </div>

                            <div style={{ display: 'flex', alignItems: 'center', margin: '8px 0' }}>
                                <Text strong style={{ color: getColor(review.rating), marginRight: 8 }}>
                                    {review.rating}/10
                                </Text>
                            </div>

                            <Paragraph style={{ color: '#e5e5e5', marginBottom: 0 }}>
                                {review.text}
                            </Paragraph>
                        </ReviewCard>
                    ))}
                </div>
            )}
            <StyledModal
                title="Delete Confirmation"
                visible={deleteConfirmVisible}
                onOk={handleDeleteConfirm}
                onCancel={() => setDeleteConfirmVisible(false)}
                okText="Yes"
                cancelText="No"
                okButtonProps={{
                    danger: true,
                    style: {
                        backgroundColor: '#ff4d4f',
                        borderColor: '#ff4d4f'
                    }
                }}
                cancelButtonProps={{
                    style: {
                        color: '#c7d5e0',
                        borderColor: '#2a475e',
                        backgroundColor: 'transparent'
                    }
                }}
                bodyStyle={{
                    background: '#1B2838',
                    padding: '24px',
                    color: '#c7d5e0'
                }}
            >
                <p>Are you sure you want to delete this review?</p>
                <p>This action cannot be undone.</p>
            </StyledModal>
        </div>
    );
};

export default ReviewDetail;