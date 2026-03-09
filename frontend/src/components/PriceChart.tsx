import { useState, useEffect, useMemo } from 'react'
import { Segmented, Spin } from 'antd'
import { AreaChart, Area, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts'
import dayjs from 'dayjs'
import { marketApi } from '../services/marketApi'
import type { PricePoint } from '../types'

interface PriceChartProps {
  symbol: string
  initialPrice: number
}

const TIME_RANGES = ['1W', '1M', '3M', 'Alle'] as const

export default function PriceChart({ symbol, initialPrice }: PriceChartProps) {
  const [history, setHistory] = useState<PricePoint[]>([])
  const [loading, setLoading] = useState(true)
  const [range, setRange] = useState<string>('Alle')

  useEffect(() => {
    setLoading(true)
    marketApi.getStockHistory(symbol)
      .then((res) => setHistory(res.data))
      .finally(() => setLoading(false))
  }, [symbol])

  const filteredData = useMemo(() => {
    const sorted = [...history].reverse()
    if (range === 'Alle') return sorted

    const now = dayjs()
    const cutoff = range === '1W'
      ? now.subtract(1, 'week')
      : range === '1M'
        ? now.subtract(1, 'month')
        : now.subtract(3, 'month')

    return sorted.filter((p) => dayjs(p.timestamp).isAfter(cutoff))
  }, [history, range])

  const chartData = filteredData.map((p) => ({
    time: dayjs(p.timestamp).format('DD.MM.YY HH:mm'),
    price: Number(p.price.toFixed(2)),
  }))

  const currentPrice = chartData.length > 0 ? chartData[chartData.length - 1].price : initialPrice
  const isPositive = currentPrice >= initialPrice

  if (loading) {
    return <div style={{ textAlign: 'center', padding: 40 }}><Spin /></div>
  }

  return (
    <div>
      <div style={{ marginBottom: 16, textAlign: 'right' }}>
        <Segmented
          options={[...TIME_RANGES]}
          value={range}
          onChange={(val) => setRange(val as string)}
        />
      </div>

      <ResponsiveContainer width="100%" height={300}>
        <AreaChart data={chartData}>
          <defs>
            <linearGradient id="colorPrice" x1="0" y1="0" x2="0" y2="1">
              <stop offset="5%" stopColor={isPositive ? '#52c41a' : '#ff4d4f'} stopOpacity={0.3} />
              <stop offset="95%" stopColor={isPositive ? '#52c41a' : '#ff4d4f'} stopOpacity={0} />
            </linearGradient>
          </defs>
          <XAxis
            dataKey="time"
            tick={{ fontSize: 11, fill: 'rgba(255,255,255,0.45)' }}
            tickLine={false}
            axisLine={false}
          />
          <YAxis
            domain={['auto', 'auto']}
            tick={{ fontSize: 11, fill: 'rgba(255,255,255,0.45)' }}
            tickLine={false}
            axisLine={false}
            tickFormatter={(v: number) => `$${v}`}
          />
          <Tooltip
            contentStyle={{ background: '#1f1f1f', border: '1px solid #303030', borderRadius: 6 }}
            labelStyle={{ color: 'rgba(255,255,255,0.65)' }}
            formatter={(value: number) => [`$${value.toFixed(2)}`, 'Kurs']}
          />
          <Area
            type="monotone"
            dataKey="price"
            stroke={isPositive ? '#52c41a' : '#ff4d4f'}
            strokeWidth={2}
            fill="url(#colorPrice)"
          />
        </AreaChart>
      </ResponsiveContainer>
    </div>
  )
}
