import React, { useState, useEffect } from 'react';
import { Typography, List, Spin, message, Button, Input, Modal, Form } from 'antd';
import { CompanyService } from '../services';
import { useNavigate } from 'react-router-dom';
import { PlusOutlined, SearchOutlined } from '@ant-design/icons';
import styled from 'styled-components';

const { Title, Paragraph } = Typography;
const { TextArea } = Input;

// Стилизованные компоненты для модального окна
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

const CompaniesPage = () => {
    const [companies, setCompanies] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [modalVisible, setModalVisible] = useState(false);
    const [form] = Form.useForm();
    const navigate = useNavigate();
    const [filteredCompanies, setFilteredCompanies] = useState([]);
    const [searchQuery, setSearchQuery] = useState('');
    const [deleteConfirmVisible, setDeleteConfirmVisible] = useState(false);
    const [companyToDelete, setCompanyToDelete] = useState(null);

    useEffect(() => {
        fetchCompanies();
    }, []);

    const fetchCompanies = async () => {
        try {
            const data = await CompanyService.getAllCompanies();
            setCompanies(data);
            setFilteredCompanies(data); // Инициализируем filteredCompanies
        } catch (err) {
            setError('Failed to load companies');
            message.error('Failed to load companies');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const showDeleteConfirm = (company) => {
        setCompanyToDelete(company);
        setDeleteConfirmVisible(true);
    };

    const handleDeleteConfirm = async () => {
        try {
            if (!companyToDelete?.id) {
                message.error('Company ID is missing');
                return;
            }
            await CompanyService.deleteCompany(companyToDelete.id);
            message.success('Company deleted successfully');
            fetchCompanies(); // Refresh the list
        } catch (err) {
            message.error('Failed to delete company');
            console.error(err);
        } finally {
            setDeleteConfirmVisible(false);
            setCompanyToDelete(null);
        }
    };

    const handleSearch = (value) => {
        setSearchQuery(value);
        if (!value) {
            setFilteredCompanies(companies);
            return;
        }
        const filtered = companies.filter(company =>
            company.name.toLowerCase().includes(value.toLowerCase())
        );
        setFilteredCompanies(filtered);
    };

    const handleSubmit = async () => {
        try {
            const values = await form.validateFields();
            await CompanyService.createCompany(values);
            message.success('Company added successfully!');
            setModalVisible(false);
            form.resetFields();
            fetchCompanies(); // Обновляем список компаний
        } catch (err) {
            message.error('Failed to add company');
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
                <Title level={2} style={{ color: '#66c0f4' }}>Game Companies</Title>
                <div style={{ display: 'flex', gap: '16px' }}>
                    <Input
                        placeholder="Search company..."
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
                        Add Company
                    </Button>
                </div>
            </div>

            <Paragraph style={{ color: '#c7d5e0' }}>
                Information about game developers and publishers.
                {searchQuery && filteredCompanies.length === 0 && (
                    <>
                        <br />
                        <span style={{ color: '#ff6b6b' }}>
                       No companies found matching "{searchQuery}"
                    </span>
                    </>
                )}
            </Paragraph>

            <List
                dataSource={filteredCompanies}
                renderItem={company => (
                    <List.Item
                        style={{
                            color: '#c7d5e0',
                            borderBottom: '1px solid #2a475e',
                            padding: '16px 0'
                        }}
                        actions={[
                            <Button
                                type="default"
                                onClick={() => {
                                    if (!company.id) {
                                        message.error('Company ID is missing');
                                        return;
                                    }
                                    navigate(`/companies/${company.id}`);
                                }}
                                style={{
                                    color: '#66c0f4',
                                    border: '1px solid #66c0f4',
                                    backgroundColor: 'transparent'
                                }}
                            >
                                More
                            </Button>,
                            <Button
                                danger
                                onClick={() => showDeleteConfirm(company)}
                                style={{
                                    color: '#ff4d4f',
                                    border: '1px solid #ff4d4f',
                                    backgroundColor: 'transparent'
                                }}
                            >
                                Delete
                            </Button>
                        ]}
                    >
                        <List.Item.Meta
                            title={<span style={{ color: '#66c0f4', fontSize: '18px' }}>{company.name}</span>}
                            description={
                                <div style={{ color: '#c7d5e0' }}>
                                    <p>{company.description}</p>
                                    <p><small>Founded: {company.foundedYear}</small></p>
                                </div>
                            }
                        />
                    </List.Item>
                )}
            />

            <StyledModal
                title="Add New Company"
                visible={modalVisible}
                onOk={handleSubmit}
                onCancel={() => setModalVisible(false)}
                okText="Add Company"
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
                        name="name"
                        label={<span style={{ color: '#c7d5e0' }}>Name</span>}
                        rules={[{ required: true, message: 'Please input the company name!' }]}
                    >
                        <StyledInput />
                    </Form.Item>
                    <Form.Item
                        name="foundedYear"
                        label={<span style={{ color: '#c7d5e0' }}>Founded Year</span>}
                        rules={[
                            { required: true, message: 'Please input the founded year!' },
                            {
                                validator: (_, value) => {
                                    if (value && value < 1950) {
                                        return Promise.reject(new Error('Year is too early'));
                                    }
                                    return Promise.resolve();
                                }
                            }
                        ]}
                    >
                        <StyledInput type="number" />
                    </Form.Item>
                    <Form.Item
                        name="website"
                        label={<span style={{ color: '#c7d5e0' }}>Website</span>}
                    >
                        <StyledInput />
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
                <p>Are you sure you want to delete "{companyToDelete?.name}" company?</p>
                <p>This action cannot be undone.</p>
            </StyledModal>
        </div>
    );
};

export default CompaniesPage;