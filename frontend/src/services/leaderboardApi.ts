import type { LeaderboardEntry } from '../types'
import api from './api'

export const leaderboardApi = {
  getLeaderboard() {
    return api.get<LeaderboardEntry[]>('/leaderboard')
  },
}
