import React, { useState, useEffect } from 'react';
import { Typography, Card, Row, Col, Spin, message, Button, Form, Input, Modal, Tag } from 'antd';
import { GameService } from '../services';
import styled from 'styled-components';
import { useNavigate } from 'react-router-dom';
import { PlusOutlined, SearchOutlined, DeleteOutlined } from '@ant-design/icons';
import { Select } from 'antd';
import { CompanyService } from '../services';
import StyledDatePicker from '../components/Styled/StyledDatePicker';

const { Title, Paragraph, Text } = Typography;
const { TextArea } = Input;

// Стилизованные компоненты
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

const StyledCard = styled(Card)`
    background: linear-gradient(135deg, #1B2838 0%, #2a475e 100%) !important;
    border-color: #66c0f4 !important;
    border-width: 1px !important;
    transition: all 0.3s ease !important;
    height: 180px !important;
    position: relative !important;
    overflow: hidden !important;
    cursor: pointer !important;

    &:hover {
        transform: translateY(-3px) !important;
        box-shadow: 0 5px 15px rgba(102, 192, 244, 0.2) !important;
        border-color: #66c0f4 !important;
    }

    &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 3px;
        background: linear-gradient(90deg, #66c0f4 0%, #1B2838 100%);
    }

    .ant-card-meta-title {
        color: #66c0f4 !important;
        font-size: 16px !important; // Было 14px
        font-weight: bold !important;
        margin-bottom: 8px !important;
        white-space: nowrap !important;
        overflow: hidden !important;
        text-overflow: ellipsis !important;
    }

    .game-company {
        background-color: rgba(102, 192, 244, 0.2) !important;
        color: #66c0f4 !important;
        border-color: #66c0f4 !important;
        font-size: 12px !important; // Было 11px
        padding: 0 6px !important;
        margin: 0 !important;
        line-height: 1.5 !important;
        border-radius: 4px !important;
    }

    .game-genre {
        color: #c7d5e0 !important;
        font-size: 12px !important; // Было 11px
        background: none !important;
        border: none !important;
        padding: 0 !important;
        margin: 0 !important;
        line-height: 1.3 !important;
    }

    .game-info {
        display: flex;
        flex-direction: column;
        gap: 8px; /* Увеличено расстояние между блоками */
    }

    .game-companies {
        display: flex;
        flex-wrap: wrap;
        gap: 6px; /* Увеличено расстояние между компаниями */
        margin-bottom: 8px; /* Увеличено расстояние снизу */
    }

    .game-genres {
        display: flex;
        flex-wrap: wrap;
        gap: 4px;
    }

    .delete-btn {
        position: absolute !important;
        bottom: 8px !important;
        right: 8px !important;
        color: #ff6b6b !important;
        border-color: #ff6b6b !important;
        font-size: 12px !important;

        &:hover {
            background-color: rgba(255, 107, 107, 0.1) !important;
        }
    }
`;

const GamesPage = () => {
    const [games, setGames] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [modalVisible, setModalVisible] = useState(false);
    const [form] = Form.useForm();
    const navigate = useNavigate();
    const [filteredGames, setFilteredGames] = useState([]);
    const [searchQuery, setSearchQuery] = useState('');
    const [companies, setCompanies] = useState([]);
    const [deletingId, setDeletingId] = useState(null);
    const [gamesWithCompanies, setGamesWithCompanies] = useState([]);
    const [deleteConfirmVisible, setDeleteConfirmVisible] = useState(false);
    const [gameToDelete, setGameToDelete] = useState(null);

    useEffect(() => {
        fetchGames();
        fetchCompanies();
    }, []);

    const fetchGames = async () => {
        try {
            const data = await GameService.getAllGames();
            setGames(data);
            setFilteredGames(data);

            // Загружаем компании для каждой игры
            const gamesWithCompaniesData = await Promise.all(
                data.map(async game => {
                    try {
                        const companies = await GameService.getGameCompanies(game.id);
                        return { ...game, companies };
                    } catch (err) {
                        console.error(`Failed to load companies for game ${game.id}`, err);
                        return { ...game, companies: [] };
                    }
                })
            );

            setGamesWithCompanies(gamesWithCompaniesData);
        } catch (err) {
            setError('Failed to load games');
            message.error('Failed to load games');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const fetchCompanies = async () => {
        try {
            const data = await CompanyService.getAllCompanies();
            setCompanies(data);
        } catch (err) {
            console.error('Failed to load companies', err);
        }
    };

    const showDeleteConfirm = (game) => {
        setGameToDelete(game);
        setDeleteConfirmVisible(true);
    };

    const handleDeleteConfirm = async () => {
        try {
            if (!gameToDelete?.id) {
                message.error('Game ID is missing');
                return;
            }
            await GameService.deleteGame(gameToDelete.id);
            message.success('Game deleted successfully');
            fetchGames(); // Refresh the list
        } catch (err) {
            message.error('Failed to delete game');
            console.error(err);
        } finally {
            setDeleteConfirmVisible(false);
            setGameToDelete(null);
        }
    };

    const handleDeleteGame = async (id) => {
        setDeletingId(id);
        try {
            await GameService.deleteGame(id);
            message.success('Game deleted successfully');
            fetchGames(); // Обновляем список игр
        } catch (err) {
            message.error('Failed to delete game');
            console.error(err);
        } finally {
            setDeletingId(null);
        }
    };

    const handleSearch = (value) => {
        setSearchQuery(value);
        if (!value) {
            setFilteredGames(games);
            return;
        }
        const filtered = games.filter(game =>
            game.title.toLowerCase().includes(value.toLowerCase())
        );
        setFilteredGames(filtered);
    };

    const handleSubmit = async () => {
        try {
            const values = await form.validateFields();

            const gameData = {
                title: values.title,
                genre: values.genre,
                releaseDate: values.releaseDate.format('YYYY-MM-DD'),
                description: values.description
            };

            const createdGame = await GameService.createGame(gameData);

            if (values.companyNames && values.companyNames.length > 0) {
                for (const companyName of values.companyNames) {
                    try {
                        await GameService.addCompanyToGameByNames(createdGame.title, companyName);
                    } catch (err) {
                        console.error(`Failed to add company ${companyName} to game`, err);
                        message.error(`Failed to add company ${companyName}`);
                    }
                }
            }

            message.success('Game added successfully!');
            setModalVisible(false);
            form.resetFields();
            fetchGames();
        } catch (err) {
            message.error('Failed to add game');
            console.error(err);
        }
    };

    if (loading) return <Spin size="large" />;
    if (error) return <Paragraph style={{ color: '#ff6b6b' }}>{error}</Paragraph>;

    return (
        <div>
            <div style={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                marginBottom: '20px'
            }}>
                <Title level={2} style={{ color: '#66c0f4' }}>Games Catalog</Title>
                <div style={{ display: 'flex', gap: '16px' }}>
                    <Input
                        placeholder="Search game..."
                        prefix={<SearchOutlined />}
                        value={searchQuery}
                        onChange={(e) => handleSearch(e.target.value)}
                        style={{
                            width: 300,
                            background: '#2a475e',
                            borderColor: '#2a475e',
                            color: '#c7d5e0'
                        }}
                    />
                    <Button
                        type="primary"
                        icon={<PlusOutlined />}
                        onClick={() => setModalVisible(true)}
                        style={{
                            backgroundColor: '#66c0f4',
                            borderColor: '#66c0f4',
                            color: '#1B2838'
                        }}
                    >
                        Add Game
                    </Button>
                </div>
            </div>

            <Paragraph style={{
                color: '#c7d5e0',
                marginTop: '16px', // Добавлено это свойство
                marginBottom: '16px'
            }}>
                Browse our collection of games from various developers and publishers.
                {searchQuery && filteredGames.length === 0 && (
                    <>
                        <br />
                        <span style={{ color: '#ff6b6b' }}>
                             No games found matching "{searchQuery}"
                        </span>
                    </>
                )}
            </Paragraph>

            <Row gutter={[12, 12]}>
                {filteredGames.map(game => {
                    const gameWithCompanies = gamesWithCompanies.find(g => g.id === game.id) || game;
                    return (
                        <Col xs={24} sm={12} md={8} lg={6} xl={4.8} key={game.id}>
                            <StyledCard
                                hoverable
                                onClick={() => navigate(`/games/${game.id}`)}
                                bodyStyle={{
                                    padding: '12px',
                                    color: '#c7d5e0',
                                    height: '100%',
                                    display: 'flex',
                                    flexDirection: 'column'
                                }}
                            >
                                <Card.Meta
                                    title={game.title}
                                    description={
                                        <div className="game-info">
                                            {/* Компании с увеличенным расстоянием */}
                                            {gameWithCompanies.companies && gameWithCompanies.companies.length > 0 && (
                                                <div className="game-companies">
                                                    {gameWithCompanies.companies.slice(0, 3).map((company, index) => (
                                                        <Tag key={index} className="game-company">
                                                            {company.name || company}
                                                        </Tag>
                                                    ))}
                                                    {gameWithCompanies.companies.length > 3 && (
                                                        <Tag className="game-company">+{gameWithCompanies.companies.length - 3}</Tag>
                                                    )}
                                                </div>
                                            )}
                                            {/* Жанры */}
                                            {game.genre && (
                                                <div className="game-genres">
                                                    {game.genre.split(',').slice(0, 3).map((genre, index) => (
                                                        <span key={index} className="game-genre">
                                                            {genre.trim()}
                                                        </span>
                                                    ))}
                                                    {game.genre.split(',').length > 3 && (
                                                        <span className="game-genre">...</span>
                                                    )}
                                                </div>
                                            )}
                                        </div>
                                    }
                                />
                                <Button
                                    className="delete-btn"
                                    type="text"
                                    icon={<DeleteOutlined />}
                                    loading={deletingId === game.id}
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        showDeleteConfirm(game);
                                    }}
                                    size="small"
                                />
                            </StyledCard>
                        </Col>
                    );
                })}
            </Row>
            <StyledModal
                title="Add New Game"
                visible={modalVisible}
                onOk={handleSubmit}
                onCancel={() => setModalVisible(false)}
                okText="Add Game"
                cancelText="Cancel"
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
                width={700}
            >
                <Form
                    form={form}
                    layout="vertical"
                    style={{ color: '#c7d5e0' }}
                >
                    <Form.Item
                        name="title"
                        label={<span style={{ color: '#c7d5e0' }}>Title</span>}
                        rules={[{ required: true, message: 'Please input the game title!' }]}
                    >
                        <StyledInput />
                    </Form.Item>
                    <Form.Item
                        name="genre"
                        label={<span style={{ color: '#c7d5e0' }}>Genre</span>}
                        rules={[{ required: true, message: 'Please input the game genre!' }]}
                    >
                        <StyledInput />
                    </Form.Item>
                    <Form.Item
                        name="releaseDate"
                        label={<span style={{ color: '#c7d5e0' }}>Release Date</span>}
                        rules={[{ required: true, message: 'Please select the release date!' }]}
                    >
                        <StyledDatePicker dropdownClassName="custom-datepicker-dropdown" />
                    </Form.Item>
                    <Form.Item
                        name="companyNames"
                        label={<span style={{ color: '#c7d5e0' }}>Developers/Publishers</span>}
                        rules={[{ required: true, message: 'Please select at least one company!' }]}
                    >
                        <StyledSelect
                            mode="multiple"
                            popupClassName="custom-select-dropdown"
                            style={{ width: '100%' }}
                            placeholder="Select companies"
                            optionLabelProp="label"
                        >
                            {companies.map(company => (
                                <Select.Option
                                    key={company.id}
                                    value={company.name}
                                    label={company.name}
                                >
                                    {company.name}
                                </Select.Option>
                            ))}
                        </StyledSelect>
                    </Form.Item>
                    <Form.Item
                        name="description"
                        label={<span style={{ color: '#c7d5e0' }}>Description</span>}
                    >
                        <StyledTextArea rows={4} />
                    </Form.Item>
                </Form>
            </StyledModal>
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
                <p>Are you sure you want to delete "{gameToDelete?.title}" game?</p>
                <p>This action cannot be undone.</p>
            </StyledModal>
        </div>
    );
};

export default GamesPage;