import type { News, Stock } from '../types'
import api from './api'

export const marketApi = {
  getStocks() {
    return api.get<Stock[]>('/market/stocks')
  },

  getStock(symbol: string) {
    return api.get<Stock>(`/market/stocks/${symbol}`)
  },

  getNews() {
    return api.get<News[]>('/market/news')
  },
}
