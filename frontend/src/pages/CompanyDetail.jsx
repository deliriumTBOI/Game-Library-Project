import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Typography, Card, Button, List, Spin, Divider } from 'antd';
import { CompanyService } from '../services';

const { Title, Paragraph } = Typography;

const CompanyDetail = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [company, setCompany] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchCompany = async () => {
            try {
                const data = await CompanyService.getCompanyWithGames(id);
                setCompany(data);
            } catch (error) {
                console.error('Error fetching company:', error);
            } finally {
                setLoading(false);
            }
        };
        fetchCompany();
    }, [id]);

    if (loading) return <Spin size="large" />;
    if (!company) return <Paragraph>Company not found</Paragraph>;

    return (
        <div style={{ padding: '24px' }}>
            <Button onClick={() => navigate(-1)} style={{ marginBottom: '20px' }}>
                Back to Companies
            </Button>

            <Card style={{ background: '#1B2838', borderColor: '#2a475e' }}>
                <div style={{ color: '#c7d5e0' }}>
                    <Title level={2} style={{ color: '#66c0f4' }}>{company.name}</Title>

                    <Divider style={{ borderColor: '#2a475e' }} />

                    <Title level={4}>About</Title>
                    <p>{company.description}</p>
                    <p><strong>Founded:</strong> {company.foundedYear}</p>
                    {company.website && <p><strong>Website:</strong> <a href={company.website} target="_blank" rel="noopener noreferrer" style={{ color: '#66c0f4' }}>{company.website}</a></p>}

                    <Divider style={{ borderColor: '#2a475e' }} />

                    <Title level={4}>Games</Title>
                    {company.games?.length > 0 ? (
                        <List
                            dataSource={company.games}
                            renderItem={game => (
                                <List.Item>
                                    <Button
                                        type="link"
                                        onClick={() => navigate(`/games/${game.id}`)}
                                        style={{ color: '#66c0f4', padding: 0 }}
                                    >
                                        {game.title || 'Untitled Game'}
                                    </Button>
                                </List.Item>
                            )}
                        />
                    ) : (
                        <Paragraph>No games found for this company</Paragraph>
                    )}
                </div>
            </Card>
        </div>
    );
};

export default CompanyDetail;