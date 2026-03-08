import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { observer } from 'mobx-react-lite'
import { Card, Button, Space, Typography, Spin } from 'antd'
import { ArrowLeftOutlined } from '@ant-design/icons'
import { useStore } from '../stores/RootStore'
import { marketApi } from '../services/marketApi'
import type { Stock } from '../types'
import PriceChart from '../components/PriceChart'
import TradeDialog from '../components/TradeDialog'

const { Title, Text } = Typography

const StockDetailPage = observer(() => {
  const { symbol } = useParams<{ symbol: string }>()
  const navigate = useNavigate()
  const { playerStore } = useStore()
  const [stock, setStock] = useState<Stock | null>(null)
  const [loading, setLoading] = useState(true)
  const [tradeType, setTradeType] = useState<'BUY' | 'SELL'>('BUY')
  const [tradeOpen, setTradeOpen] = useState(false)

  useEffect(() => {
    if (!symbol) return
    setLoading(true)
    marketApi.getStock(symbol)
      .then((res) => setStock(res.data))
      .finally(() => setLoading(false))
    playerStore.fetchPortfolio()
  }, [symbol, playerStore])

  if (loading || !stock) {
    return <div style={{ textAlign: 'center', padding: 80 }}><Spin size="large" /></div>
  }

  const isPositive = stock.change >= 0

  return (
    <div style={{ padding: 24, maxWidth: 900, margin: '0 auto' }}>
      <Button
        icon={<ArrowLeftOutlined />}
        type="text"
        onClick={() => navigate('/trading')}
        style={{ marginBottom: 16 }}
      >
        Zurück
      </Button>

      <Card>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 24 }}>
          <div>
            <Title level={3} style={{ margin: 0 }}>{stock.name}</Title>
            <Text type="secondary">{stock.symbol} · {stock.sector}</Text>
          </div>
          <div style={{ textAlign: 'right' }}>
            <Title level={2} style={{ margin: 0 }}>${stock.price.toFixed(2)}</Title>
            <Text style={{ color: isPositive ? '#52c41a' : '#ff4d4f', fontSize: 16 }}>
              {isPositive ? '+' : ''}{stock.change.toFixed(2)} ({isPositive ? '+' : ''}{stock.changePercent.toFixed(2)}%)
            </Text>
          </div>
        </div>

        <PriceChart symbol={stock.symbol} initialPrice={stock.price - stock.change} />

        <div style={{ marginTop: 24, display: 'flex', gap: 12 }}>
          <Space>
            <Button type="primary" size="large" onClick={() => { setTradeType('BUY'); setTradeOpen(true) }}>
              Kaufen
            </Button>
            <Button size="large" danger onClick={() => { setTradeType('SELL'); setTradeOpen(true) }}>
              Verkaufen
            </Button>
          </Space>
        </div>
      </Card>

      {tradeOpen && (
        <TradeDialog
          stock={stock}
          type={tradeType}
          onClose={() => {
            setTradeOpen(false)
            if (symbol) {
              marketApi.getStock(symbol).then((res) => setStock(res.data))
            }
          }}
        />
      )}
    </div>
  )
})

export default StockDetailPage
