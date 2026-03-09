import { useEffect, useState } from 'react'
import { observer } from 'mobx-react-lite'
import { Table, Typography, Tag, Card, Space } from 'antd'
import { TrophyOutlined } from '@ant-design/icons'
import type { ColumnsType } from 'antd/es/table'
import { useStore } from '../stores/RootStore'
import { leaderboardApi } from '../services/leaderboardApi'
import type { LeaderboardEntry } from '../types'

const { Title } = Typography

const medalColors: Record<number, string> = {
  1: '#ffd700',
  2: '#c0c0c0',
  3: '#cd7f32',
}

const LeaderboardPage = observer(() => {
  const { playerStore, marketStore } = useStore()
  const [leaderboard, setLeaderboard] = useState<LeaderboardEntry[]>([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    setLoading(true)
    leaderboardApi
      .getLeaderboard()
      .then((res) => setLeaderboard(res.data))
      .finally(() => setLoading(false))
  }, [])

  // Use WebSocket data if available (from MarketStore)
  const data = marketStore.leaderboard.length > 0 ? marketStore.leaderboard : leaderboard

  const columns: ColumnsType<LeaderboardEntry> = [
    {
      title: 'Rang',
      dataIndex: 'rank',
      key: 'rank',
      width: 80,
      align: 'center',
      render: (rank: number) => {
        if (rank <= 3) {
          return (
            <Space>
              <TrophyOutlined style={{ color: medalColors[rank], fontSize: 18 }} />
              <span style={{ color: medalColors[rank], fontWeight: 'bold' }}>{rank}</span>
            </Space>
          )
        }
        return rank
      },
    },
    {
      title: 'Spieler',
      dataIndex: 'username',
      key: 'username',
      render: (username: string) => {
        const isCurrentUser = username === playerStore.username
        return isCurrentUser ? <Tag color="blue">{username} (Du)</Tag> : username
      },
    },
    {
      title: 'Gesamtwert',
      dataIndex: 'totalValue',
      key: 'totalValue',
      align: 'right',
      sorter: (a, b) => a.totalValue - b.totalValue,
      defaultSortOrder: 'descend',
      render: (value: number) =>
        `$${value.toLocaleString('de-DE', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`,
    },
  ]

  return (
    <div style={{ padding: 24 }}>
      <Title level={3}>
        <TrophyOutlined /> Rangliste
      </Title>

      <Card>
        <Table
          columns={columns}
          dataSource={data}
          rowKey="rank"
          loading={loading}
          pagination={{ pageSize: 20 }}
          rowClassName={(record) =>
            record.username === playerStore.username ? 'leaderboard-highlight' : ''
          }
        />
      </Card>

      <style>{`
        .leaderboard-highlight td {
          background: rgba(22, 119, 255, 0.15) !important;
        }
      `}</style>
    </div>
  )
})

export default LeaderboardPage
