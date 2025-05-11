import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link, Navigate } from 'react-router-dom';
import { Layout, Menu, Typography } from 'antd';
import GamesPage from './pages/GamesPage';
import CompaniesPage from './pages/CompaniesPage';
import ReviewsPage from './pages/ReviewsPage';
import GameDetail from './pages/GameDetail';
import CompanyDetail from './pages/CompanyDetail';

const { Header, Content, Footer } = Layout;
const { Title } = Typography;

function App() {
  return (
      <Router>
        <Layout style={{
          minHeight: '100vh',
          background: '#1B2838'  // Steam-like dark blue background
        }}>
          <Header style={{
            position: 'sticky',
            top: 0,
            zIndex: 10,
            width: '100%',
            display: 'flex',
            alignItems: 'center',
            background: '#171a21',  // Steam header dark color
            padding: '0 24px',
            borderBottom: '1px solid #2a475e'  // Steam-like border
          }}>
            <Title level={3} style={{
              color: '#c7d5e0',  // Steam light blue text
              marginRight: '24px',
              marginBottom: 0,
              fontWeight: 'bold',
              fontFamily: '"Motiva Sans", sans-serif'
            }}>
              GameLib
            </Title>

            <Menu
                theme="dark"
                mode="horizontal"
                defaultSelectedKeys={['games']}
                style={{
                  flex: 1,
                  background: 'transparent',
                  borderBottom: 'none'
                }}
            >
              <Menu.Item key="games">
                <Link to="/">Games</Link>
              </Menu.Item>
              <Menu.Item key="companies">
                <Link to="/companies">Companies</Link>
              </Menu.Item>
              <Menu.Item key="reviews">
                <Link to="/reviews">Reviews</Link>
              </Menu.Item>
            </Menu>

            <div style={{
              color: '#66c0f4',  // Steam light blue accent
              fontFamily: '"Motiva Sans", sans-serif'
            }}>
              Developer: deliriumTBOI
            </div>
          </Header>

          <Content style={{
            padding: '24px',
            position: 'relative',
            zIndex: 1,
            background: 'radial-gradient(circle at center, #1B2838 0%, #0E1A27 100%)'  // Steam-like gradient
          }}>
            <div style={{
              background: 'rgba(23, 26, 33, 0.8)',  // Semi-transparent dark background
              padding: '24px',
              borderRadius: '4px',
              minHeight: 'calc(100vh - 134px)',
              boxShadow: '0 0 10px rgba(0,0,0,0.5)',
              border: '1px solid #2a475e'
            }}>
              <Routes>
                <Route path="/" element={<GamesPage />} />
                <Route path="/companies" element={<CompaniesPage />} />
                <Route path="/reviews" element={<ReviewsPage />} />
                <Route path="/games" element={<GamesPage />} />
                <Route path="/games/:id" element={<GameDetail />} />
                <Route path="/" element={<Navigate to="/games" replace />} />
                <Route path="/companies/:id" element={<CompanyDetail />} />
              </Routes>
            </div>
          </Content>

          <Footer style={{
            textAlign: 'center',
            background: '#171a21',
            padding: '16px 50px',
            borderTop: '1px solid #2a475e',
            color: '#c7d5e0'
          }}>
            © {new Date().getFullYear()} GameLib Application - Gaming Library Project
          </Footer>
        </Layout>
      </Router>
  );
}

export default App;