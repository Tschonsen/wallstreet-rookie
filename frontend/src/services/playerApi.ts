import type { Player } from '../types'
import api from './api'

export const playerApi = {
  getPortfolio() {
    return api.get<Player>('/player/portfolio')
  },

  getStats() {
    return api.get<Player>('/player/stats')
  },
}
