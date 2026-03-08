import { makeAutoObservable, runInAction } from 'mobx'
import type { Player, PortfolioResponse } from '../types'
import { authApi } from '../services/authApi'
import { playerApi } from '../services/playerApi'

export class PlayerStore {
  player: Player | null = null
  token: string | null = localStorage.getItem('token')
  username: string | null = localStorage.getItem('username')
  loading = false
  error: string | null = null

  constructor() {
    makeAutoObservable(this)
  }

  get isAuthenticated() {
    return this.token !== null
  }

  async login(username: string, password: string) {
    this.loading = true
    this.error = null
    try {
      const response = await authApi.login(username, password)
      runInAction(() => {
        this.token = response.data.token
        this.username = response.data.username
        localStorage.setItem('token', response.data.token)
        localStorage.setItem('username', response.data.username)
      })
    } catch (e: unknown) {
      runInAction(() => {
        const err = e as { response?: { data?: { message?: string } } }
        this.error = err.response?.data?.message ?? 'Login fehlgeschlagen'
      })
      throw e
    } finally {
      runInAction(() => {
        this.loading = false
      })
    }
  }

  async register(username: string, password: string, email: string) {
    this.loading = true
    this.error = null
    try {
      const response = await authApi.register(username, password, email)
      runInAction(() => {
        this.token = response.data.token
        this.username = response.data.username
        localStorage.setItem('token', response.data.token)
        localStorage.setItem('username', response.data.username)
      })
    } catch (e: unknown) {
      runInAction(() => {
        const err = e as { response?: { data?: { message?: string } } }
        this.error = err.response?.data?.message ?? 'Registrierung fehlgeschlagen'
      })
      throw e
    } finally {
      runInAction(() => {
        this.loading = false
      })
    }
  }

  logout() {
    this.token = null
    this.username = null
    this.player = null
    localStorage.removeItem('token')
    localStorage.removeItem('username')
  }

  async fetchPortfolio() {
    const response = await playerApi.getPortfolio()
    runInAction(() => {
      this.player = {
        username: this.username ?? '',
        cash: response.data.cash,
        totalValue: response.data.totalValue,
        portfolio: response.data.positions.map((p) => ({
          symbol: p.symbol,
          quantity: p.quantity,
          averageBuyPrice: p.averageBuyPrice,
        })),
      }
    })
  }

  updateFromWebSocket(data: PortfolioResponse) {
    this.player = {
      username: this.username ?? '',
      cash: data.cash,
      totalValue: data.totalValue,
      portfolio: data.positions.map((p) => ({
        symbol: p.symbol,
        quantity: p.quantity,
        averageBuyPrice: p.averageBuyPrice,
      })),
    }
  }
}
