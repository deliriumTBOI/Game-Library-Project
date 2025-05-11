import React, { useState, useEffect } from 'react';
import { Typography, Card, Row, Col, Spin, message, Button, Form, Input, DatePicker, Modal } from 'antd';
import { GameService } from '../services';
import styled from 'styled-components';
import { useNavigate } from 'react-router-dom';
import { PlusOutlined, SearchOutlined } from '@ant-design/icons';
import { Select } from 'antd';
import { CompanyService } from '../services';
import StyledDatePicker from '../components/Styled/StyledDatePicker';


const { Title, Paragraph } = Typography;
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
    padding: 16px 24px !important; /* Добавляем отступы */
    margin-top: 8px !important; /* Добавляем отступ сверху */
    
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

    useEffect(() => {
        fetchGames();
    }, []);

    useEffect(() => {
        const fetchCompanies = async () => {
            try {
                const data = await CompanyService.getAllCompanies();
                setCompanies(data);
            } catch (err) {
                console.error('Failed to load companies', err);
            }
        };
        fetchCompanies();
    }, []);

    const fetchGames = async () => {
        try {
            const data = await GameService.getAllGames();
            setGames(data);
            setFilteredGames(data); // Инициализируем filteredGames
        } catch (err) {
            setError('Failed to load games');
            message.error('Failed to load games');
            console.error(err);
        } finally {
            setLoading(false);
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

            // Сначала создаем игру без компаний
            const gameData = {
                title: values.title,
                genre: values.genre,
                releaseDate: values.releaseDate.format('YYYY-MM-DD'),
                description: values.description
            };

            const createdGame = await GameService.createGame(gameData);

            // Затем добавляем компании по одной
            if (values.companyNames && values.companyNames.length > 0) {
                for (const companyName of values.companyNames) {
                    try {
                        await GameService.addCompanyToGameByNames(createdGame.title, companyName);
                    } catch (err) {
                        console.error(`Failed to add company ${companyName} to game`, err);
                        // Можно добавить уведомление об ошибке для конкретной компании
                        message.error(`Failed to add company ${companyName}`);
                    }
                }
            }

            message.success('Game added successfully!');
            setModalVisible(false);
            form.resetFields();
            fetchGames(); // Обновляем список игр
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

            <Paragraph style={{ color: '#c7d5e0' }}>
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

            <Row gutter={[16, 16]}>
                {filteredGames.map(game => (
                    <Col span={6} key={game.id}>
                        <Card
                            hoverable
                            onClick={() => navigate(`/games/${game.id}`)}
                            style={{
                                background: '#1B2838',
                                borderColor: '#2a475e',
                                height: '180px' // Уменьшенная высота
                            }}
                            bodyStyle={{
                                padding: '12px',
                                color: '#c7d5e0'
                            }}
                        >
                            <Card.Meta
                                title={game.title}
                                description={
                                    <>
                                        <p>Genre: {game.genre}</p>
                                        <p>Release: {new Date(game.releaseDate).getFullYear()}</p>
                                    </>
                                }
                            />
                        </Card>
                    </Col>
                ))}
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
        </div>
    );
};

export default GamesPage;