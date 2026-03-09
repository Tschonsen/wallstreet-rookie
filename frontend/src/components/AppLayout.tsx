import { useState } from 'react'
import { useNavigate, useLocation, Outlet } from 'react-router-dom'
import { observer } from 'mobx-react-lite'
import { Layout, Menu, Button, Typography, Space, Tag } from 'antd'
import {
  DashboardOutlined,
  StockOutlined,
  WalletOutlined,
  TrophyOutlined,
  LogoutOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
} from '@ant-design/icons'
import { useStore } from '../stores/RootStore'
import NewsTicker from './NewsTicker'

const { Sider, Header, Content } = Layout
const { Text } = Typography

const AppLayout = observer(() => {
  const { playerStore, gameStore, marketStore } = useStore()
  const navigate = useNavigate()
  const location = useLocation()
  const [collapsed, setCollapsed] = useState(false)

  const menuItems = [
    { key: '/dashboard', icon: <DashboardOutlined />, label: 'Dashboard' },
    { key: '/trading', icon: <StockOutlined />, label: 'Trading' },
    { key: '/portfolio', icon: <WalletOutlined />, label: 'Portfolio' },
    ...(gameStore.session?.mode === 'MP'
      ? [{ key: '/leaderboard', icon: <TrophyOutlined />, label: 'Rangliste' }]
      : []),
  ]

  const modeLabel = gameStore.session?.mode === 'SP' ? 'Singleplayer' : 'Multiplayer'
  const modeColor = gameStore.session?.mode === 'SP' ? 'blue' : 'green'

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider
        collapsible
        collapsed={collapsed}
        trigger={null}
        style={{ borderRight: '1px solid #303030' }}
      >
        <div style={{ padding: '16px', textAlign: 'center' }}>
          {!collapsed && <Text strong style={{ fontSize: 16 }}>WallStreet Rookie</Text>}
          {collapsed && <Text strong>WR</Text>}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>

      <Layout>
        <Header style={{
          padding: '0 24px',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          borderBottom: '1px solid #303030',
          background: '#141414',
        }}>
          <Space>
            <Button
              type="text"
              icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
              onClick={() => setCollapsed(!collapsed)}
            />
            {gameStore.session && <Tag color={modeColor}>{modeLabel}</Tag>}
            {gameStore.session?.mode === 'SP' && (
              <Text type="secondary">Woche {gameStore.session.currentWeek}</Text>
            )}
          </Space>

          <Space size="large">
            <NewsTicker news={marketStore.news} />
            <Text strong>
              ${(playerStore.player?.cash ?? 0).toLocaleString('de-DE', { minimumFractionDigits: 2 })}
            </Text>
            <Text type="secondary">{playerStore.username}</Text>
            <Button
              type="text"
              icon={<LogoutOutlined />}
              onClick={() => {
                playerStore.logout()
                navigate('/login')
              }}
            >
              Logout
            </Button>
          </Space>
        </Header>

        <Content style={{ background: '#141414' }}>
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  )
})

export default AppLayout
