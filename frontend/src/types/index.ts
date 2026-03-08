export interface Stock {
  symbol: string
  name: string
  sector: string
  price: number
  change: number
  changePercent: number
}

export interface Player {
  username: string
  cash: number
  totalValue: number
  portfolio: PortfolioEntry[]
}

export interface PortfolioEntry {
  symbol: string
  quantity: number
  averageBuyPrice: number
}

export interface Trade {
  symbol: string
  type: 'BUY' | 'SELL'
  quantity: number
  price: number
  total: number
  timestamp: string
}

export interface News {
  title: string
  content: string
  affectedSymbols: string[]
  impact: number
  timestamp: string
}

export interface GameSession {
  id: string
  mode: 'SP' | 'MP'
  currentWeek: number
  status: 'RUNNING' | 'PAUSED' | 'ENDED'
}

export interface LeaderboardEntry {
  rank: number
  username: string
  totalValue: number
}

export interface PortfolioPosition {
  symbol: string
  quantity: number
  averageBuyPrice: number
  currentPrice: number
  positionValue: number
  profitLoss: number
  profitLossPercent: number
}

export interface PortfolioResponse {
  cash: number
  portfolioValue: number
  totalValue: number
  positions: PortfolioPosition[]
}

export interface PricePoint {
  symbol: string
  price: number
  change: number
  changePercent: number
  timestamp: string
  gameSessionId?: string
}

export interface OrderEntry {
  id: string
  symbol: string
  orderType: 'LIMIT_BUY' | 'LIMIT_SELL' | 'STOP_LOSS'
  quantity: number
  targetPrice: number
  status: 'OPEN' | 'FILLED' | 'CANCELLED'
  createdAt: string
  filledAt: string | null
}

export interface ChatMessage {
  username: string
  message: string
  timestamp: string
}

export interface TradeFeedEntry {
  username: string
  symbol: string
  action: string
  quantity: number
  total: number
  timestamp: string
}

export interface AuthResponse {
  token: string
  username: string
  expiresAt: string
}
