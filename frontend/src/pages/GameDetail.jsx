import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Typography, Card, Button, Divider, Rate, List, Tag, Spin, message, Modal, Form, Input, Select } from 'antd';
import { GameService } from '../services';
import { CompanyService } from '../services';
import StyledDatePicker from '../components/Styled/StyledDatePicker';
// Импортируем dayjs как более легковесную замену moment
import dayjs from 'dayjs';

const { Title, Paragraph } = Typography;
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

// Функция форматирования даты для отображения
const formatDisplayDate = (dateStr) => {
    if (!dateStr) return 'Unknown';
    try {
        return dayjs(dateStr).format('MM/DD/YYYY');
    } catch (e) {
        return 'Invalid Date';
    }
};

// Удаляем ненужный валидатор, так как DatePicker уже возвращает валидные объекты Date

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

    useEffect(() => {
        const fetchGameData = async () => {
            try {
                setLoading(true);
                setError(null);

                if (!id) {
                    throw new Error('Game ID is missing');
                }

                // Используем готовый метод getGameWithCompanies из GameService
                const gameData = await GameService.getGameWithCompanies(id);

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

    const handleEditClick = () => {
        fetchAllCompanies();
        setIsEditing(true);
    };

    const handleCancel = () => {
        setIsEditing(false);
    };

    const handleSave = async () => {
        try {
            const values = await form.validateFields();

            // Проверка уникальности названия игры
            if (values.title !== game.title) {
                try {
                    const existingGame = await GameService.findGameByExactTitle(values.title);
                    if (existingGame && existingGame.id !== game.id) {
                        message.error('A game with this title already exists');
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

            // Обновление компаний
            const currentCompanyIds = game.companies.map(c => c.id);
            const newCompanyIds = values.companies || [];

            // Удаление компаний, которые были удалены
            const companiesToRemove = currentCompanyIds.filter(id => !newCompanyIds.includes(id));
            for (const companyId of companiesToRemove) {
                await GameService.removeCompanyFromGame(game.id, companyId);
            }

            // Добавление новых компаний
            const companiesToAdd = newCompanyIds.filter(id => !currentCompanyIds.includes(id));
            for (const companyId of companiesToAdd) {
                await GameService.addCompanyToGame(game.id, companyId);
            }

            // Обновление данных игры
            const updatedGame = await GameService.getGameWithCompanies(game.id);
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
        <div style={{ padding: '24px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '20px' }}>
                <Button onClick={() => navigate(-1)}>Back to Games</Button>
                <Button type="primary" onClick={handleEditClick}>Edit Info</Button>
            </div>

            <Card
                style={{ marginTop: '20px', background: '#1B2838', borderColor: '#2a475e' }}
                cover={
                    <div style={{
                        height: '300px',
                        background: '#2a475e',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center'
                    }}>
                        <Title level={2} style={{ color: '#66c0f4' }}>{game.title}</Title>
                    </div>
                }
            >
                <div style={{ color: '#c7d5e0' }}>
                    <Title level={4}>Details</Title>
                    <p><strong>Genre:</strong> {game.genre || 'Unknown'}</p>
                    <p><strong>Release Date:</strong> {formatDisplayDate(game.releaseDate)}</p>
                    <p><strong>Description:</strong> {game.description || 'No description available'}</p>

                    <Divider />
                    <Title level={4}>Developers & Publishers</Title>
                    {game && game.companies && Array.isArray(game.companies) && game.companies.length > 0 ? (
                        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px' }}>
                            {game.companies.map((company, index) => (
                                <Tag
                                    key={company?.id || `company-${index}`}
                                    style={{
                                        cursor: 'pointer',
                                        background: '#2a475e',
                                        color: '#66c0f4',
                                        borderColor: '#2a475e',
                                        padding: '4px 8px'
                                    }}
                                    onClick={() => company?.id && navigate(`/companies/${company.id}`)}
                                >
                                    {company?.name || 'Unknown Company'}
                                </Tag>
                            ))}
                        </div>
                    ) : (
                        <p>No company information available</p>
                    )}

                    <Divider />

                    <Title level={4}>Reviews</Title>
                    {game.reviews && Array.isArray(game.reviews) && game.reviews.length > 0 ? (
                        <List
                            dataSource={game.reviews}
                            renderItem={review => (
                                <List.Item>
                                    <Card style={{ width: '100%', background: '#2a475e', borderColor: '#2a475e' }}>
                                        <Rate disabled defaultValue={review.rating} />
                                        <p>{review.text}</p>
                                        <p><em>- {review.author}</em></p>
                                    </Card>
                                </List.Item>
                            )}
                        />
                    ) : (
                        <p>No reviews yet</p>
                    )}
                </div>
            </Card>

            {/* Модальное окно редактирования */}
            <Modal
                title="Edit Game Information"
                open={isEditing}
                onCancel={handleCancel}
                onOk={handleSave}
                width={700}
                okText="Save"
                cancelText="Cancel"
            >
                <Form
                    form={form}
                    layout="vertical"
                >
                    <Form.Item
                        name="title"
                        label="Title"
                        rules={[
                            { required: true, message: 'Please input the game title!' },
                            { max: 100, message: 'Title must be less than 100 characters' }
                        ]}
                    >
                        <Input />
                    </Form.Item>

                    <Form.Item
                        name="description"
                        label="Description"
                        rules={[{ max: 2000, message: 'Description must be less than 2000 characters' }]}
                    >
                        <TextArea rows={4} />
                    </Form.Item>

                    <Form.Item
                        name="releaseDate"
                        label="Release Date"
                        rules={[
                            { required: true, message: 'Please select the release date!' }
                        ]}
                    >
                        <StyledDatePicker />
                    </Form.Item>

                    <Form.Item
                        name="genre"
                        label="Genre"
                        rules={[{ max: 50, message: 'Genre must be less than 50 characters' }]}
                    >
                        <Input />
                    </Form.Item>

                    <Form.Item
                        name="companies"
                        label="Companies"
                    >
                        <Select
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
                        </Select>
                    </Form.Item>
                </Form>
            </Modal>
        </div>
    );
};

export default GameDetail;