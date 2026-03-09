import type { Trade } from '../types'
import api from './api'

export const tradeApi = {
  buy(symbol: string, quantity: number) {
    return api.post<Trade>('/trades/buy', { symbol, quantity })
  },

  sell(symbol: string, quantity: number) {
    return api.post<Trade>('/trades/sell', { symbol, quantity })
  },

  getHistory() {
    return api.get<Trade[]>('/trades/history')
  },
}
