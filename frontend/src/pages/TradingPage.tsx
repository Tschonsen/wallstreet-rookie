import { useEffect, useState } from 'react'
import { observer } from 'mobx-react-lite'
import { Table, Tag, Input, Button, Space } from 'antd'
import { SearchOutlined } from '@ant-design/icons'
import type { ColumnsType } from 'antd/es/table'
import { useStore } from '../stores/RootStore'
import type { Stock } from '../types'
import TradeDialog from '../components/TradeDialog'

const SECTOR_COLORS: Record<string, string> = {
  Technologie: 'blue',
  Finanzen: 'gold',
  Gesundheit: 'green',
  Energie: 'orange',
  Konsum: 'purple',
  Industrie: 'cyan',
  'Krypto/FinTech': 'magenta',
}

const TradingPage = observer(() => {
  const { marketStore, playerStore } = useStore()
  const [search, setSearch] = useState('')
  const [tradeStock, setTradeStock] = useState<Stock | null>(null)
  const [tradeType, setTradeType] = useState<'BUY' | 'SELL'>('BUY')

  useEffect(() => {
    marketStore.fetchStocks()
    playerStore.fetchPortfolio()
  }, [marketStore, playerStore])

  const filteredStocks = marketStore.stocks.filter(
    (s) =>
      s.symbol.toLowerCase().includes(search.toLowerCase()) ||
      s.name.toLowerCase().includes(search.toLowerCase()) ||
      s.sector.toLowerCase().includes(search.toLowerCase())
  )

  const openTrade = (stock: Stock, type: 'BUY' | 'SELL') => {
    setTradeStock(stock)
    setTradeType(type)
  }

  const closeTrade = () => {
    setTradeStock(null)
  }

  const columns: ColumnsType<Stock> = [
    {
      title: 'Symbol',
      dataIndex: 'symbol',
      key: 'symbol',
      sorter: (a, b) => a.symbol.localeCompare(b.symbol),
      render: (symbol: string) => <strong>{symbol}</strong>,
    },
    {
      title: 'Name',
      dataIndex: 'name',
      key: 'name',
      sorter: (a, b) => a.name.localeCompare(b.name),
    },
    {
      title: 'Sektor',
      dataIndex: 'sector',
      key: 'sector',
      sorter: (a, b) => a.sector.localeCompare(b.sector),
      render: (sector: string) => (
        <Tag color={SECTOR_COLORS[sector] || 'default'}>{sector}</Tag>
      ),
    },
    {
      title: 'Kurs',
      dataIndex: 'price',
      key: 'price',
      sorter: (a, b) => a.price - b.price,
      render: (price: number) => `$${price.toFixed(2)}`,
      align: 'right' as const,
    },
    {
      title: 'Änderung',
      dataIndex: 'changePercent',
      key: 'changePercent',
      sorter: (a, b) => a.changePercent - b.changePercent,
      render: (pct: number, record: Stock) => (
        <span style={{ color: record.change >= 0 ? '#52c41a' : '#ff4d4f' }}>
          {record.change >= 0 ? '+' : ''}
          {record.change.toFixed(2)} ({pct >= 0 ? '+' : ''}
          {pct.toFixed(2)}%)
        </span>
      ),
      align: 'right' as const,
    },
    {
      title: 'Aktion',
      key: 'action',
      render: (_: unknown, record: Stock) => (
        <Space>
          <Button type="primary" size="small" onClick={() => openTrade(record, 'BUY')}>
            Kaufen
          </Button>
          <Button size="small" danger onClick={() => openTrade(record, 'SELL')}>
            Verkaufen
          </Button>
        </Space>
      ),
    },
  ]

  return (
    <div style={{ padding: 24 }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <h2 style={{ margin: 0 }}>Aktienmarkt</h2>
        <Input
          placeholder="Suche nach Symbol, Name oder Sektor..."
          prefix={<SearchOutlined />}
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          style={{ width: 320 }}
          allowClear
        />
      </div>

      <Table
        columns={columns}
        dataSource={filteredStocks}
        rowKey="symbol"
        loading={marketStore.loading}
        pagination={{ pageSize: 25, showSizeChanger: false }}
        size="middle"
      />

      <TradeDialog
        stock={tradeStock}
        type={tradeType}
        onClose={closeTrade}
      />
    </div>
  )
})

export default TradingPage
