import type { Trade } from '../types'
import api from './api'

export const tradeApi = {
  buy(symbol: string, quantity: number) {
    return api.post<Trade>('/trade/buy', { symbol, quantity })
  },

  sell(symbol: string, quantity: number) {
    return api.post<Trade>('/trade/sell', { symbol, quantity })
  },

  getHistory() {
    return api.get<Trade[]>('/trade/history')
  },
}
