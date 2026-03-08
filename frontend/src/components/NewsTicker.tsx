import { Typography } from 'antd'
import type { News } from '../types'

const { Text } = Typography

interface NewsTickerProps {
  news: News[]
}

export default function NewsTicker({ news }: NewsTickerProps) {
  const recentNews = news.slice(0, 5)

  if (recentNews.length === 0) return null

  const tickerText = recentNews.map((n) => n.title).join('  ·  ')

  return (
    <div style={{ overflow: 'hidden', maxWidth: 400 }}>
      <div
        style={{
          display: 'inline-block',
          whiteSpace: 'nowrap',
          animation: 'ticker 20s linear infinite',
        }}
      >
        <Text type="secondary" style={{ fontSize: 12 }}>
          {tickerText}
        </Text>
      </div>

      <style>{`
        @keyframes ticker {
          0% { transform: translateX(100%); }
          100% { transform: translateX(-100%); }
        }
      `}</style>
    </div>
  )
}
