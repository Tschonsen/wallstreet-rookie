import { makeAutoObservable, runInAction } from 'mobx'
import type { Trade } from '../types'
import { tradeApi } from '../services/tradeApi'

export class TradeStore {
  tradeHistory: Trade[] = []
  loading = false

  constructor() {
    makeAutoObservable(this)
  }

  async buy(symbol: string, quantity: number) {
    this.loading = true
    try {
      await tradeApi.buy(symbol, quantity)
    } finally {
      runInAction(() => {
        this.loading = false
      })
    }
  }

  async sell(symbol: string, quantity: number) {
    this.loading = true
    try {
      await tradeApi.sell(symbol, quantity)
    } finally {
      runInAction(() => {
        this.loading = false
      })
    }
  }

  async fetchHistory() {
    const response = await tradeApi.getHistory()
    runInAction(() => {
      this.tradeHistory = response.data
    })
  }
}
