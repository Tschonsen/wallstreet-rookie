import { useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { observer } from 'mobx-react-lite'
import { Card, Col, Row, Statistic, Typography, Tag, List, Button, Space, App } from 'antd'
import { ArrowUpOutlined, ArrowDownOutlined, RiseOutlined, FallOutlined, ThunderboltOutlined } from '@ant-design/icons'
import { useStore } from '../stores/RootStore'
import type { Stock } from '../types'

const { Text, Title } = Typography

const DashboardPage = observer(() => {
  const { marketStore, playerStore, gameStore, tradeStore } = useStore()
  const { message } = App.useApp()
  const navigate = useNavigate()

  useEffect(() => {
    marketStore.fetchStocks()
    marketStore.fetchNews()
    playerStore.fetchPortfolio()
  }, [marketStore, playerStore])

  const cash = playerStore.player?.cash ?? 0
  const totalValue = playerStore.player?.totalValue ?? 100_000
  const totalPL = totalValue - 100_000

  const sortedByChange = [...marketStore.stocks].sort((a, b) => b.changePercent - a.changePercent)
  const topGainers = sortedByChange.slice(0, 5)
  const topLosers = sortedByChange.slice(-5).reverse()

  const recentNews = marketStore.news.slice(0, 10)

  const handleSkip = async (weeks: number) => {
    if (!gameStore.session) {
      message.warning('Keine aktive Singleplayer-Session')
      return
    }
    try {
      await gameStore.skipTime(weeks)
      await marketStore.fetchStocks()
      await marketStore.fetchNews()
      await playerStore.fetchPortfolio()
      message.success(`${weeks} Woche(n) vorgespult`)
    } catch {
      message.error('Fehler beim Vorspulen')
    }
  }

  return (
    <div style={{ padding: 24 }}>
      <Title level={3}>Dashboard</Title>

      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={6}>
          <Card>
            <Statistic title="Gesamtwert" value={totalValue} precision={2} prefix="$" />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic title="Cash" value={cash} precision={2} prefix="$" />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="Gewinn/Verlust"
              value={totalPL}
              precision={2}
              prefix={totalPL >= 0 ? <ArrowUpOutlined /> : <ArrowDownOutlined />}
              valueStyle={{ color: totalPL >= 0 ? '#52c41a' : '#ff4d4f' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="Woche"
              value={gameStore.session?.currentWeek ?? '-'}
              suffix={gameStore.session ? `/ ${gameStore.session.status}` : ''}
            />
          </Card>
        </Col>
      </Row>

      {gameStore.session?.mode === 'SP' && gameStore.session.status === 'RUNNING' && (
        <Card style={{ marginBottom: 24 }} size="small">
          <Space>
            <Text strong>Zeit vorspulen:</Text>
            <Button icon={<ThunderboltOutlined />} onClick={() => handleSkip(1)}>+1 Woche</Button>
            <Button icon={<ThunderboltOutlined />} onClick={() => handleSkip(4)}>+1 Monat</Button>
          </Space>
        </Card>
      )}

      <Row gutter={24} style={{ marginBottom: 24 }}>
        <Col span={12}>
          <Card title={<><RiseOutlined style={{ color: '#52c41a' }} /> Top Gewinner</>} size="small">
            <StockList stocks={topGainers} onStockClick={(s) => navigate(`/trading/${s.symbol}`)} />
          </Card>
        </Col>
        <Col span={12}>
          <Card title={<><FallOutlined style={{ color: '#ff4d4f' }} /> Top Verlierer</>} size="small">
            <StockList stocks={topLosers} onStockClick={(s) => navigate(`/trading/${s.symbol}`)} />
          </Card>
        </Col>
      </Row>

      <Card title="Aktuelle News" size="small">
        {recentNews.length > 0 ? (
          <List
            dataSource={recentNews}
            renderItem={(news) => (
              <List.Item>
                <List.Item.Meta
                  title={
                    <Space>
                      <Tag color={news.impact >= 0 ? 'green' : 'red'}>
                        {news.impact >= 0 ? '+' : ''}{(news.impact * 100).toFixed(1)}%
                      </Tag>
                      {news.title}
                    </Space>
                  }
                />
              </List.Item>
            )}
          />
        ) : (
          <Text type="secondary">Keine News vorhanden</Text>
        )}
      </Card>
    </div>
  )
})

function StockList({ stocks, onStockClick }: { stocks: Stock[]; onStockClick: (s: Stock) => void }) {
  return (
    <List
      dataSource={stocks}
      renderItem={(stock) => (
        <List.Item
          style={{ cursor: 'pointer' }}
          onClick={() => onStockClick(stock)}
        >
          <List.Item.Meta
            title={<Text strong>{stock.symbol}</Text>}
            description={stock.name}
          />
          <div style={{ textAlign: 'right' }}>
            <div>${stock.price.toFixed(2)}</div>
            <Text style={{ color: stock.changePercent >= 0 ? '#52c41a' : '#ff4d4f' }}>
              {stock.changePercent >= 0 ? '+' : ''}{stock.changePercent.toFixed(2)}%
            </Text>
          </div>
        </List.Item>
      )}
    />
  )
}

export default DashboardPage
