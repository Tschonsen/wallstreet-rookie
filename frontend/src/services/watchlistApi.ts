import api from './api'

export const watchlistApi = {
  getWatchlist() {
    return api.get<string[]>('/player/watchlist')
  },

  addToWatchlist(symbol: string) {
    return api.post<string[]>(`/player/watchlist/${symbol}`)
  },

  removeFromWatchlist(symbol: string) {
    return api.delete<string[]>(`/player/watchlist/${symbol}`)
  },
}
