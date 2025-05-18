import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Typography, Card, Button, Divider, Tag, Spin, message, Modal, Form, Input, Select } from 'antd';
import { GameService, CompanyService, ReviewService } from '../services';
import StyledDatePicker from '../components/Styled/StyledDatePicker';
import dayjs from 'dayjs';
import styled from 'styled-components';

const { Title, Paragraph, Text } = Typography;
const { TextArea } = Input;
const { Option } = Select;

// Функция для форматирования даты в серверный формат
const toServerDate = (date) => {
    if (!date) return null;
    // Поддержка как dayjs, так и стандартных объектов Date
    if (date.format) { // если это объект dayjs
        return date.format('YYYY-MM-DD');
    } else if (date instanceof Date) {
        return date.toISOString().split('T')[0];
    } else {
        // Для строк или других типов попробуем преобразовать в dayjs
        const dayjsDate = dayjs(date);
        return dayjsDate.isValid() ? dayjsDate.format('YYYY-MM-DD') : null;
    }
};

const getColor = (value) => {
    const colors = [
        '#ff0000', '#ff3300', '#ff6600', '#ff9900',
        '#ffcc00', '#ffff00', '#ccff00', '#99ff00',
        '#66ff00', '#33ff00', '#00ff00'
    ];
    return colors[Math.min(Math.max(Math.floor(value), 0), 10)];
};

// Функция форматирования даты для отображения
const formatDisplayDate = (dateStr) => {
    if (!dateStr) return 'Unknown';
    try {
        return dayjs(dateStr).format('MM/DD/YYYY');
    } catch (e) {
        return 'Invalid Date';
    }
};

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

const StyledInput = styled(Input)`
    background-color: #2a475e !important;
    border-color: #2a475e !important;
    color: #c7d5e0 !important;
`;

const StyledTextArea = styled(TextArea)`
    background-color: #2a475e !important;
    border-color: #2a475e !important;
    color: #c7d5e0 !important;
`;

const StyledSelect = styled(Select)`
    .ant-select-selector {
        background-color: #2a475e !important;
        border-color: #2a475e !important;
        color: #c7d5e0 !important;
    }

    .ant-select-selection-placeholder {
        color: #6b8a9e !important;
    }

    .ant-select-arrow {
        color: #c7d5e0 !important;
    }
`;

const StyledReviewCard = styled(Card)`
    background-color: #1B2838;
    border: 1px solid #2a475e;
    margin-bottom: 16px;
    border-radius: 8px;
    width: 100%;

    .ant-card-body {
        padding: 16px;
    }
`;

const StyledRatingContainer = styled.div`
    display: flex;
    align-items: center;
    margin: 8px 0;
`;

const StyledRatingCircle = styled.div`
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

// Add this component inside GameDetail.jsx (before the main component)
const RatingInput = ({ value, onChange }) => {
    return (
        <StyledRatingContainer>
            {[...Array(10)].map((_, i) => (
                <StyledRatingCircle
                    key={i}
                    active={i < value}
                    value={i+1}
                    onClick={() => onChange(i+1)}
                >
                    {i+1}
                </StyledRatingCircle>
            ))}
            <Text strong style={{ marginLeft: 8, color: '#66c0f4' }}>{value}/10</Text>
        </StyledRatingContainer>
    );
};

const GameDetail = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [game, setGame] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isEditing, setIsEditing] = useState(false);
    const [form] = Form.useForm();
    const [allCompanies, setAllCompanies] = useState([]);
    const [companiesLoading, setCompaniesLoading] = useState(false);
    const [isSaving, setIsSaving] = useState(false);
    const [isReviewModalVisible, setIsReviewModalVisible] = useState(false);
    const [reviewForm] = Form.useForm();
    const [deleteConfirmVisible, setDeleteConfirmVisible] = useState(false);
    const [reviewToDelete, setReviewToDelete] = useState(null);

    useEffect(() => {
        const fetchGameData = async () => {
            try {
                setLoading(true);
                setError(null);

                if (!id) {
                    throw new Error('Game ID is missing');
                }

                // Используем готовый метод getGameWithCompanies из GameService
                const gameData = await GameService.getGameWithCompanies(id, true);

                if (!gameData) {
                    throw new Error('No game data returned');
                }

                // Нормализуем структуру данных компаний
                const normalizedGame = {
                    ...gameData,
                    companies: Array.isArray(gameData.companies)
                        ? gameData.companies.map(c => ({
                            id: c?.id || null,
                            name: c?.name || 'Unknown Company'
                        }))
                        : []
                };

                setGame(normalizedGame);

                // Установим значения формы для редактирования
                form.setFieldsValue({
                    title: normalizedGame.title,
                    description: normalizedGame.description,
                    releaseDate: normalizedGame.releaseDate ? dayjs(normalizedGame.releaseDate) : null,
                    genre: normalizedGame.genre,
                    companies: normalizedGame.companies.map(c => c.id)
                });
            } catch (err) {
                console.error('Error fetching game data:', err);
                setError(err.message || 'Failed to load game data');
                message.error('Failed to load game data');
            } finally {
                setLoading(false);
            }
        };

        fetchGameData();
    }, [id, form]);

    const fetchAllCompanies = async () => {
        try {
            setCompaniesLoading(true);
            // Используем CompanyService вместо прямого вызова apiClient
            const response = await CompanyService.getAllCompanies();
            setAllCompanies(response || []);
        } catch (error) {
            console.error('Error fetching companies:', error);
            message.error('Failed to load companies');
        } finally {
            setCompaniesLoading(false);
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
            await ReviewService.deleteReview(id, reviewToDelete.id);
            message.success('Review deleted successfully');

            // Обновляем данные игры после удаления отзыва
            const updatedGame = await GameService.getGameWithCompanies(id, true);
            setGame({
                ...updatedGame,
                companies: Array.isArray(updatedGame.companies)
                    ? updatedGame.companies.map(c => ({
                        id: c?.id || null,
                        name: c?.name || 'Unknown Company'
                    }))
                    : []
            });
        } catch (err) {
            message.error('Failed to delete review');
            console.error(err);
        } finally {
            setDeleteConfirmVisible(false);
            setReviewToDelete(null);
        }
    };

    const handleReviewSubmit = async (values) => {
        try {
            await ReviewService.createReview(id, values);
            message.success('Review added successfully');

            // Refresh the game data to show the new review
            const updatedGame = await GameService.getGameWithCompanies(id, true);
            setGame({
                ...updatedGame,
                companies: Array.isArray(updatedGame.companies)
                    ? updatedGame.companies.map(c => ({
                        id: c?.id || null,
                        name: c?.name || 'Unknown Company'
                    }))
                    : []
            });

            setIsReviewModalVisible(false);
            reviewForm.resetFields();
        } catch (err) {
            message.error('Failed to add review');
            console.error(err);
        }
    };

    const handleEditClick = () => {
        fetchAllCompanies();
        setIsEditing(true);
    };

    const handleCancel = () => {
        setIsEditing(false);
    };

    const handleSave = async () => {
        try {
            setIsSaving(true);
            const values = await form.validateFields();

            // Проверка уникальности названия игры
            if (values.title !== game.title) {
                try {
                    const existingGame = await GameService.findGameByExactTitle(values.title);
                    if (existingGame && existingGame.id !== game.id) {
                        message.error('A game with this title already exists');
                        setIsSaving(false);
                        return;
                    }
                } catch (error) {
                    console.error('Error checking game title:', error);
                }
            }

            // Подготовка данных для обновления
            const updateData = {
                title: values.title,
                description: values.description,
                releaseDate: toServerDate(values.releaseDate),
                genre: values.genre
            };

            // Обновление основной информации об игре
            await GameService.updateGame(game.id, updateData);

            // Обновление компаний с использованием улучшенного метода
            const newCompanyIds = values.companies || [];
            await GameService.updateGameCompanies(game.id, newCompanyIds);

            // Обновление данных игры с принудительным обновлением кэша
            const updatedGame = await GameService.getGameWithCompanies(game.id, true);

            setGame({
                ...updatedGame,
                companies: Array.isArray(updatedGame.companies)
                    ? updatedGame.companies.map(c => ({
                        id: c?.id || null,
                        name: c?.name || 'Unknown Company'
                    }))
                    : []
            });

            message.success('Game updated successfully');
            setIsEditing(false);
        } catch (error) {
            console.error('Error updating game:', error);
            message.error('Failed to update game');
        } finally {
            setIsSaving(false);
        }
    };

    if (loading) return (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}>
            <Spin size="large" tip="Loading game details..." />
        </div>
    );

    if (error) return (
        <div style={{ padding: '24px' }}>
            <Button onClick={() => navigate(-1)}>Back to Games</Button>
            <div style={{ marginTop: '20px' }}>
                <Typography.Title level={3} style={{ color: '#c7d5e0' }}>Error</Typography.Title>
                <Typography.Text style={{ color: '#c7d5e0' }}>{error}</Typography.Text>
            </div>
        </div>
    );

    if (!game) return (
        <div style={{ padding: '24px' }}>
            <Button onClick={() => navigate(-1)}>Back to Games</Button>
            <div style={{ marginTop: '20px' }}>
                <Typography.Title level={3} style={{ color: '#c7d5e0' }}>Game not found</Typography.Title>
                <Typography.Text style={{ color: '#c7d5e0' }}>
                    The requested game could not be found. Please check the URL and try again.
                </Typography.Text>
            </div>
        </div>
    );

    return (
        <div style={{ padding: '24px', maxWidth: '1200px', margin: '0 auto' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
                <Button
                    onClick={() => navigate(-1)}
                    style={{
                        background: '#2a475e',
                        color: '#66c0f4',
                        borderColor: '#2a475e'
                    }}
                >
                    Back
                </Button>
                <Button
                    type="primary"
                    onClick={handleEditClick}
                    disabled={loading}
                    style={{ background: '#1a9fff', borderColor: '#1a9fff' }}
                >
                    Edit Info
                </Button>
            </div>

            <Card
                style={{ marginTop: '20px', background: '#1B2838', borderColor: '#2a475e' }}
                cover={
                    <div style={{
                        height: '200px',
                        background: 'linear-gradient(135deg, #1B2838 0%, #2a475e 100%)',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        position: 'relative',
                        overflow: 'hidden'
                    }}>
                        <Title
                            level={2}
                            style={{
                                color: '#66c0f4',
                                textShadow: '0 2px 4px rgba(0,0,0,0.5)',
                                padding: '0 20px',
                                textAlign: 'center',
                                zIndex: 1
                            }}
                        >
                            {game.title}
                        </Title>
                    </div>
                }
            >
                <div style={{ color: '#c7d5e0', padding: '16px' }}>
                    {/* Первая строка с основными деталями */}
                    <div style={{
                        display: 'flex',
                        gap: '40px',
                        marginBottom: '24px',
                        flexWrap: 'wrap'
                    }}>
                        <div style={{ flex: 1, minWidth: '250px' }}>
                            <Title level={4} style={{
                                color: '#66c0f4',
                                marginBottom: '12px',
                                borderBottom: '2px solid #2a475e',
                                paddingBottom: '8px'
                            }}>
                                Details
                            </Title>
                            <div style={{ display: 'grid', gridTemplateColumns: '100px 1fr', gap: '8px' }}>
                                <span style={{ color: '#8F98A0' }}>Genre:</span>
                                <span style={{ color: '#ffffff' }}>{game.genre || 'Unknown'}</span>

                                <span style={{ color: '#8F98A0' }}>Release Date:</span>
                                <span style={{ color: '#ffffff' }}>{formatDisplayDate(game.releaseDate)}</span>
                            </div>
                        </div>

                        <div style={{ flex: 2, minWidth: '300px' }}>
                            <Title level={4} style={{
                                color: '#66c0f4',
                                marginBottom: '12px',
                                borderBottom: '2px solid #2a475e',
                                paddingBottom: '8px'
                            }}>
                                Description
                            </Title>
                            <Paragraph style={{
                                marginBottom: 0,
                                color: '#e5e5e5',
                                lineHeight: '1.6'
                            }}>
                                {game.description || 'No description available'}
                            </Paragraph>
                        </div>
                    </div>

                    <Divider style={{
                        borderColor: '#2a475e',
                        margin: '24px 0'
                    }} />

                    {/* Блок с компаниями */}
                    <div style={{ marginBottom: '24px' }}>
                        <Title level={4} style={{
                            color: '#66c0f4',
                            marginBottom: '12px',
                            borderBottom: '2px solid #2a475e',
                            paddingBottom: '8px'
                        }}>
                            Developers & Publishers
                        </Title>
                        {game.companies?.length > 0 ? (
                            <div style={{
                                display: 'flex',
                                flexWrap: 'wrap',
                                gap: '8px',
                                backgroundColor: '#16202D',
                                padding: '12px',
                                borderRadius: '4px'
                            }}>
                                {game.companies.map((company, index) => (
                                    <Tag
                                        key={company?.id || `company-${index}`}
                                        style={{
                                            cursor: 'pointer',
                                            background: '#2a475e',
                                            color: '#66c0f4',
                                            borderColor: '#66c0f4',
                                            padding: '4px 8px',
                                            margin: 0
                                        }}
                                        onClick={() => company?.id && navigate(`/companies/${company.id}`)}
                                    >
                                        {company?.name || 'Unknown Company'}
                                    </Tag>
                                ))}
                            </div>
                        ) : (
                            <p style={{ color: '#8F98A0' }}>No company information available</p>
                        )}
                    </div>

                    <Divider style={{
                        borderColor: '#2a475e',
                        margin: '24px 0'
                    }} />

                    {/* Блок с отзывами */}
                    <div>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
                            <Title level={4} style={{
                                color: '#66c0f4',
                                borderBottom: '2px solid #2a475e',
                                paddingBottom: '8px',
                                margin: 0
                            }}>
                                Reviews
                            </Title>
                            <Button
                                type="primary"
                                onClick={() => setIsReviewModalVisible(true)}
                                style={{
                                    background: '#4582a4',
                                    borderColor: '#4582a4',
                                    height: '32px',
                                    padding: '0 15px'
                                }}
                            >
                                Add Review
                            </Button>
                        </div>
                        {game.reviews?.length > 0 ? (
                            <div>
                                {game.reviews.map(review => (
                                    <StyledReviewCard key={review.id}>
                                        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                                            <Text strong style={{ color: '#66c0f4', fontSize: 16 }}>
                                                {review.author || 'Anonymous'}
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
                                            <Text strong style={{
                                                color: getColor(review.rating),
                                                marginRight: 8
                                            }}>
                                                {review.rating}/10
                                            </Text>
                                        </div>

                                        <Paragraph style={{
                                            color: '#e5e5e5',
                                            marginBottom: 0,
                                            lineHeight: '1.6'
                                        }}>
                                            {review.text}
                                        </Paragraph>
                                    </StyledReviewCard>
                                ))}
                            </div>
                        ) : (
                            <p style={{ color: '#8F98A0' }}>No reviews yet</p>
                        )}
                    </div>
                </div>
            </Card>

            {/* Модальное окно редактирования */}
            <StyledModal
                title="Edit Game Information"
                open={isEditing}
                onCancel={handleCancel}
                onOk={handleSave}
                width={700}
                okText="Save"
                cancelText="Cancel"
                confirmLoading={isSaving}
                okButtonProps={{
                    style: {
                        backgroundColor: '#66c0f4',
                        borderColor: '#66c0f4',
                        color: '#1B2838'
                    }
                }}
                bodyStyle={{
                    background: '#1B2838',
                    padding: '24px'
                }}
            >
                <Form
                    form={form}
                    layout="vertical"
                    style={{ color: '#c7d5e0' }}
                >
                    <Form.Item
                        name="title"
                        label={<span style={{ color: '#c7d5e0' }}>Title</span>}
                        rules={[
                            { required: true, message: 'Please input the game title!' },
                            { max: 100, message: 'Title must be less than 100 characters' }
                        ]}
                    >
                        <StyledInput />
                    </Form.Item>

                    <Form.Item
                        name="description"
                        label={<span style={{ color: '#c7d5e0' }}>Description</span>}
                        rules={[{ max: 2000, message: 'Description must be less than 2000 characters' }]}
                    >
                        <StyledTextArea rows={4} />
                    </Form.Item>

                    <Form.Item
                        name="releaseDate"
                        label={<span style={{ color: '#c7d5e0' }}>Release Date</span>}
                        rules={[
                            { required: true, message: 'Please select the release date!' }
                        ]}
                    >
                        <StyledDatePicker dropdownClassName="custom-datepicker-dropdown" />
                    </Form.Item>

                    <Form.Item
                        name="genre"
                        label={<span style={{ color: '#c7d5e0' }}>Genre</span>}
                        rules={[{ max: 50, message: 'Genre must be less than 50 characters' }]}
                    >
                        <StyledInput />
                    </Form.Item>

                    <Form.Item
                        name="companies"
                        label={<span style={{ color: '#c7d5e0' }}>Companies</span>}
                    >
                        <StyledSelect
                            mode="multiple"
                            placeholder="Select companies"
                            loading={companiesLoading}
                            style={{ width: '100%' }}
                            optionFilterProp="children"
                            filterOption={(input, option) =>
                                option.children.toLowerCase().includes(input.toLowerCase())
                            }
                        >
                            {allCompanies.map(company => (
                                <Option key={company.id} value={company.id}>
                                    {company.name}
                                </Option>
                            ))}
                        </StyledSelect>
                    </Form.Item>
                </Form>
            </StyledModal>
            <StyledModal
                title="Add Review"
                open={isReviewModalVisible}
                onCancel={() => setIsReviewModalVisible(false)}
                onOk={() => reviewForm.submit()}
                width={700}
                okText="Submit"
                cancelText="Cancel"
                okButtonProps={{
                    style: {
                        backgroundColor: '#4582a4',
                        borderColor: '#4582a4',
                        color: '#ffffff'
                    }
                }}
                bodyStyle={{
                    background: '#1B2838',
                    padding: '24px'
                }}
            >
                <Form
                    form={reviewForm}
                    layout="vertical"
                    onFinish={handleReviewSubmit}
                    style={{ color: '#c7d5e0' }}
                >
                    <Form.Item
                        name="author"
                        label={<span style={{ color: '#c7d5e0' }}>Your Name</span>}
                        rules={[{ required: true, message: 'Please input your name!' }]}
                    >
                        <StyledInput />
                    </Form.Item>
                    <Form.Item
                        name="rating"
                        label={<span style={{ color: '#c7d5e0' }}>Rating</span>}
                        rules={[{ required: true, message: 'Please select a rating!' }]}
                    >
                        <RatingInput
                            value={reviewForm.getFieldValue('rating') || 0}
                            onChange={(value) => reviewForm.setFieldsValue({ rating: value })}
                        />
                    </Form.Item>
                    <Form.Item
                        name="text"
                        label={<span style={{ color: '#c7d5e0' }}>Review</span>}
                        rules={[{ required: true, message: 'Please write your review!' }]}
                    >
                        <StyledTextArea rows={4} />
                    </Form.Item>
                </Form>
            </StyledModal>
            <StyledModal
                title="Delete Confirmation"
                open={deleteConfirmVisible}
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

export default GameDetail;