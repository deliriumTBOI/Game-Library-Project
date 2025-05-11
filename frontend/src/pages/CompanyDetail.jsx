import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Typography, Card, Button, List, Spin, Divider, message, Modal, Form, Input } from 'antd';
import { CompanyService } from '../services';
import styled from 'styled-components';

const { Title, Paragraph } = Typography;
const { TextArea } = Input;

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
  }

  .ant-modal-close {
    color: #c7d5e0 !important;

    &:hover {
      color: #66c0f4 !important;
    }
  }
`;

const CompanyDetail = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [company, setCompany] = useState(null);
    const [loading, setLoading] = useState(true);
    const [isEditing, setIsEditing] = useState(false);
    const [form] = Form.useForm();
    const [isSaving, setIsSaving] = useState(false);

    const fetchCompany = async (forceRefresh = false) => {
        setLoading(true);
        try {
            const data = await CompanyService.getCompanyWithGames(id, forceRefresh);
            setCompany(data);
            form.setFieldsValue({
                name: data.name,
                description: data.description,
                foundedYear: data.foundedYear,
                website: data.website
            });
        } catch (error) {
            console.error('Failed to fetch company:', error);
            message.error('Failed to load company data');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchCompany(true);
    }, [id]);

    const handleEditClick = () => {
        setIsEditing(true);
    };

    const handleCancel = () => {
        setIsEditing(false);
    };

    const handleSave = async () => {
        try {
            setIsSaving(true);
            const values = await form.validateFields();

            // Проверка уникальности названия компании
            if (values.name !== company.name) {
                try {
                    const existingCompany = await CompanyService.findCompanyByExactName(values.name);
                    if (existingCompany && existingCompany.id !== company.id) {
                        message.error('A company with this name already exists');
                        setIsSaving(false);
                        return;
                    }
                } catch (error) {
                    console.error('Error checking company name:', error);
                }
            }

            // Проверка года основания
            if (values.foundedYear) {
                const year = parseInt(values.foundedYear);
                if (year < 1950 || year > new Date().getFullYear()) {
                    message.error(`Year must be between 1950 and ${new Date().getFullYear()}`);
                    setIsSaving(false);
                    return;
                }
            }

            // Обновление информации о компании
            await CompanyService.updateCompany(company.id, values);
            await fetchCompany(true);

            message.success('Company updated successfully');
            setIsEditing(false);
        } catch (error) {
            console.error('Error updating company:', error);
            message.error('Failed to update company');
        } finally {
            setIsSaving(false);
        }
    };

    if (loading && !company) return (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh' }}>
            <Spin size="large" tip="Loading company details..." />
        </div>
    );

    if (!company) return (
        <div style={{ padding: '24px' }}>
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
            <div style={{ marginTop: '20px' }}>
                <Typography.Title level={3} style={{ color: '#c7d5e0' }}>Company not found</Typography.Title>
                <Typography.Text style={{ color: '#c7d5e0' }}>
                    The requested company could not be found. Please check the URL and try again.
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
                            {company.name}
                        </Title>
                    </div>
                }
            >
                <div style={{ color: '#c7d5e0', padding: '16px' }}>
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
                                <span style={{ color: '#8F98A0' }}>Founded:</span>
                                <span style={{ color: '#ffffff' }}>{company.foundedYear || 'Unknown'}</span>

                                {company.website && (
                                    <>
                                        <span style={{ color: '#8F98A0' }}>Website:</span>
                                        <a
                                            href={company.website}
                                            target="_blank"
                                            rel="noopener noreferrer"
                                            style={{ color: '#66c0f4' }}
                                        >
                                            {company.website}
                                        </a>
                                    </>
                                )}
                            </div>
                        </div>

                        <div style={{ flex: 2, minWidth: '300px' }}>
                            <Title level={4} style={{
                                color: '#66c0f4',
                                marginBottom: '12px',
                                borderBottom: '2px solid #2a475e',
                                paddingBottom: '8px'
                            }}>
                                About
                            </Title>
                            <Paragraph style={{
                                marginBottom: 0,
                                color: '#e5e5e5',
                                lineHeight: '1.6'
                            }}>
                                {company.description || 'No description available'}
                            </Paragraph>
                        </div>
                    </div>

                    <Divider style={{
                        borderColor: '#2a475e',
                        margin: '24px 0'
                    }} />

                    <div style={{ marginBottom: '24px' }}>
                        <Title level={4} style={{
                            color: '#66c0f4',
                            marginBottom: '12px',
                            borderBottom: '2px solid #2a475e',
                            paddingBottom: '8px'
                        }}>
                            Games
                        </Title>
                        {company.games?.length > 0 ? (
                            <List
                                dataSource={company.games}
                                renderItem={game => (
                                    <List.Item style={{ padding: '8px 0' }}>
                                        <Button
                                            type="link"
                                            onClick={() => navigate(`/games/${game.id}`)}
                                            style={{
                                                color: '#66c0f4',
                                                padding: 0,
                                                height: 'auto'
                                            }}
                                        >
                                            {game.title || 'Untitled Game'}
                                        </Button>
                                    </List.Item>
                                )}
                            />
                        ) : (
                            <p style={{ color: '#8F98A0' }}>No games found for this company</p>
                        )}
                    </div>
                </div>
            </Card>

            {/* Модальное окно редактирования */}
            <StyledModal
                title="Edit Company Information"
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
                cancelButtonProps={{
                    style: {
                        color: '#c7d5e0',
                        borderColor: '#2a475e',
                        background: 'transparent'
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
                        name="name"
                        label={<span style={{ color: '#c7d5e0' }}>Name</span>}
                        rules={[
                            { required: true, message: 'Please input the company name!' },
                            { max: 100, message: 'Name must be less than 100 characters' }
                        ]}
                    >
                        <Input
                            style={{
                                backgroundColor: '#2a475e',
                                borderColor: '#2a475e',
                                color: '#c7d5e0'
                            }}
                        />
                    </Form.Item>

                    <Form.Item
                        name="description"
                        label={<span style={{ color: '#c7d5e0' }}>Description</span>}
                        rules={[{ max: 2000, message: 'Description must be less than 2000 characters' }]}
                    >
                        <TextArea
                            rows={4}
                            style={{
                                backgroundColor: '#2a475e',
                                borderColor: '#2a475e',
                                color: '#c7d5e0'
                            }}
                        />
                    </Form.Item>

                    <Form.Item
                        name="foundedYear"
                        label={<span style={{ color: '#c7d5e0' }}>Founded Year</span>}
                        rules={[
                            { required: true, message: 'Please input the founded year!' },
                            {
                                pattern: /^(19[5-9]\d|20[0-2]\d|2023)$/,
                                message: 'Year must be between 1950 and current year'
                            }
                        ]}
                    >
                        <Input
                            style={{
                                backgroundColor: '#2a475e',
                                borderColor: '#2a475e',
                                color: '#c7d5e0'
                            }}
                            placeholder="1950-2023"
                        />
                    </Form.Item>

                    <Form.Item
                        name="website"
                        label={<span style={{ color: '#c7d5e0' }}>Website</span>}
                        rules={[{ type: 'url', message: 'Please enter a valid URL' }]}
                    >
                        <Input
                            style={{
                                backgroundColor: '#2a475e',
                                borderColor: '#2a475e',
                                color: '#c7d5e0'
                            }}
                        />
                    </Form.Item>
                </Form>
            </StyledModal>
        </div>
    );
};

export default CompanyDetail;