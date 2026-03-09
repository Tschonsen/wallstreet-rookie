import { useEffect, useState } from 'react'
import { observer } from 'mobx-react-lite'
import { Card, Col, Row, Statistic, Table, Button, App } from 'antd'
import { ArrowUpOutlined, ArrowDownOutlined } from '@ant-design/icons'
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip } from 'recharts'
import type { ColumnsType } from 'antd/es/table'
import { useStore } from '../stores/RootStore'
import { playerApi } from '../services/playerApi'
import type { PortfolioPosition, PortfolioResponse } from '../types'
import TradeDialog from '../components/TradeDialog'

const SECTOR_COLORS: Record<string, string> = {
  Technologie: '#1890ff',
  Finanzen: '#faad14',
  Gesundheit: '#52c41a',
  Energie: '#fa8c16',
  Konsum: '#722ed1',
  Industrie: '#13c2c2',
  'Krypto/FinTech': '#eb2f96',
}

const STARTING_CASH = 100_000

const PortfolioPage = observer(() => {
  const { marketStore, tradeStore, playerStore } = useStore()
  const { message } = App.useApp()
  const [portfolio, setPortfolio] = useState<PortfolioResponse | null>(null)
  const [loading, setLoading] = useState(true)
  const [sellStock, setSellStock] = useState<PortfolioPosition | null>(null)

  const fetchData = () => {
    setLoading(true)
    playerApi.getPortfolio()
      .then((res) => setPortfolio(res.data))
      .finally(() => setLoading(false))
    marketStore.fetchStocks()
  }

  useEffect(() => {
    fetchData()
  }, [])

  const totalPL = portfolio ? portfolio.totalValue - STARTING_CASH : 0
  const totalPLPercent = (totalPL / STARTING_CASH) * 100

  const sectorData = portfolio?.positions.reduce((acc, pos) => {
    const stock = marketStore.stocks.find((s) => s.symbol === pos.symbol)
    const sector = stock?.sector ?? 'Sonstige'
    const existing = acc.find((s) => s.name === sector)
    if (existing) {
      existing.value += pos.positionValue
    } else {
      acc.push({ name: sector, value: pos.positionValue })
    }
    return acc
  }, [] as { name: string; value: number }[]) ?? []

  const columns: ColumnsType<PortfolioPosition> = [
    {
      title: 'Symbol',
      dataIndex: 'symbol',
      key: 'symbol',
      render: (symbol: string) => <strong>{symbol}</strong>,
    },
    {
      title: 'Menge',
      dataIndex: 'quantity',
      key: 'quantity',
      align: 'right' as const,
    },
    {
      title: 'Kaufpreis (Ø)',
      dataIndex: 'averageBuyPrice',
      key: 'averageBuyPrice',
      render: (v: number) => `$${v.toFixed(2)}`,
      align: 'right' as const,
    },
    {
      title: 'Aktueller Kurs',
      dataIndex: 'currentPrice',
      key: 'currentPrice',
      render: (v: number) => `$${v.toFixed(2)}`,
      align: 'right' as const,
    },
    {
      title: 'Positionswert',
      dataIndex: 'positionValue',
      key: 'positionValue',
      render: (v: number) => `$${v.toFixed(2)}`,
      align: 'right' as const,
    },
    {
      title: 'G/V',
      dataIndex: 'profitLoss',
      key: 'profitLoss',
      render: (v: number) => (
        <span style={{ color: v >= 0 ? '#52c41a' : '#ff4d4f' }}>
          {v >= 0 ? '+' : ''}${v.toFixed(2)}
        </span>
      ),
      align: 'right' as const,
    },
    {
      title: 'G/V %',
      dataIndex: 'profitLossPercent',
      key: 'profitLossPercent',
      render: (v: number) => (
        <span style={{ color: v >= 0 ? '#52c41a' : '#ff4d4f' }}>
          {v >= 0 ? '+' : ''}{v.toFixed(2)}%
        </span>
      ),
      align: 'right' as const,
    },
    {
      title: 'Aktion',
      key: 'action',
      render: (_: unknown, record: PortfolioPosition) => (
        <Button size="small" danger onClick={() => setSellStock(record)}>
          Verkaufen
        </Button>
      ),
    },
  ]

  const sellStockObj = sellStock ? marketStore.stocks.find((s) => s.symbol === sellStock.symbol) ?? {
    symbol: sellStock.symbol,
    name: sellStock.symbol,
    sector: '',
    price: sellStock.currentPrice,
    change: 0,
    changePercent: 0,
  } : null

  return (
    <div style={{ padding: 24 }}>
      <h2 style={{ marginBottom: 16 }}>Portfolio</h2>

      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={6}>
          <Card>
            <Statistic title="Gesamtwert" value={portfolio?.totalValue ?? 0} precision={2} prefix="$" />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic title="Cash" value={portfolio?.cash ?? 0} precision={2} prefix="$" />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="Gesamt G/V"
              value={totalPL}
              precision={2}
              prefix={totalPL >= 0 ? <ArrowUpOutlined /> : <ArrowDownOutlined />}
              suffix={`(${totalPLPercent >= 0 ? '+' : ''}${totalPLPercent.toFixed(2)}%)`}
              valueStyle={{ color: totalPL >= 0 ? '#52c41a' : '#ff4d4f' }}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic title="Positionen" value={portfolio?.positions.length ?? 0} />
          </Card>
        </Col>
      </Row>

      <Row gutter={24}>
        <Col span={16}>
          <Table
            columns={columns}
            dataSource={portfolio?.positions ?? []}
            rowKey="symbol"
            loading={loading}
            pagination={false}
            size="middle"
          />
        </Col>
        <Col span={8}>
          <Card title="Sektorverteilung">
            {sectorData.length > 0 ? (
              <ResponsiveContainer width="100%" height={250}>
                <PieChart>
                  <Pie
                    data={sectorData}
                    dataKey="value"
                    nameKey="name"
                    cx="50%"
                    cy="50%"
                    outerRadius={80}
                    label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
                  >
                    {sectorData.map((entry) => (
                      <Cell key={entry.name} fill={SECTOR_COLORS[entry.name] || '#666'} />
                    ))}
                  </Pie>
                  <Tooltip formatter={(value: number) => `$${value.toFixed(2)}`} />
                </PieChart>
              </ResponsiveContainer>
            ) : (
              <div style={{ textAlign: 'center', padding: 40, color: 'rgba(255,255,255,0.45)' }}>
                Keine Positionen vorhanden
              </div>
            )}
          </Card>
        </Col>
      </Row>

      {sellStockObj && (
        <TradeDialog
          stock={sellStockObj}
          type="SELL"
          onClose={() => {
            setSellStock(null)
            fetchData()
          }}
        />
      )}
    </div>
  )
})

export default PortfolioPage
