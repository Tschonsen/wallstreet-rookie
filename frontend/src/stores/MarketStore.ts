import { makeAutoObservable, runInAction } from 'mobx'
import { notification } from 'antd'
import type { News, Stock, LeaderboardEntry } from '../types'
import { marketApi } from '../services/marketApi'
import { connectWebSocket, disconnectWebSocket, subscribe } from '../services/websocket'

export class MarketStore {
  stocks: Stock[] = []
  news: News[] = []
  leaderboard: LeaderboardEntry[] = []
  loading = false

  constructor() {
    makeAutoObservable(this)
  }

  async fetchStocks() {
    this.loading = true
    try {
      const response = await marketApi.getStocks()
      runInAction(() => {
        this.stocks = response.data
      })
    } finally {
      runInAction(() => {
        this.loading = false
      })
    }
  }

  async fetchNews() {
    const response = await marketApi.getNews()
    runInAction(() => {
      this.news = response.data
    })
  }

  connectWebSocket(onConnected?: () => void) {
    connectWebSocket(() => {
      subscribe<Stock[]>('/topic/market/prices', (stocks) => {
        runInAction(() => {
          this.stocks = stocks
        })
      })

      subscribe<News>('/topic/market/news', (news) => {
        runInAction(() => {
          this.news = [news, ...this.news]
        })
        notification.info({
          message: 'Neue Nachricht',
          description: news.title,
          placement: 'topRight',
          duration: 5,
        })
      })

      subscribe<LeaderboardEntry[]>('/topic/leaderboard', (leaderboard) => {
        runInAction(() => {
          this.leaderboard = leaderboard
        })
      })

      onConnected?.()
    })
  }

  disconnectWebSocket() {
    disconnectWebSocket()
  }
}
