import type { News, Stock, PricePoint } from '../types'
import api from './api'

export const marketApi = {
  getStocks() {
    return api.get<Stock[]>('/market/stocks')
  },

  getStock(symbol: string) {
    return api.get<Stock>(`/market/stocks/${symbol}`)
  },

  getStockHistory(symbol: string) {
    return api.get<PricePoint[]>(`/market/stocks/${symbol}/history`)
  },

  getNews() {
    return api.get<News[]>('/market/news')
  },
}
