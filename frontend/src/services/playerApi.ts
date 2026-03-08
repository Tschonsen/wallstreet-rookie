import type { PortfolioResponse } from '../types'
import api from './api'

export const playerApi = {
  getPortfolio() {
    return api.get<PortfolioResponse>('/player/portfolio')
  },

  getStats() {
    return api.get('/player/stats')
  },
}
