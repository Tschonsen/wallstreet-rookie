import { makeAutoObservable, runInAction } from 'mobx'
import type { GameSession } from '../types'
import { gameApi } from '../services/gameApi'

export class GameStore {
  session: GameSession | null = null
  loading = false

  constructor() {
    makeAutoObservable(this)
  }

  get currentWeek() {
    return this.session?.currentWeek ?? 0
  }

  get mode() {
    return this.session?.mode ?? null
  }

  async startSingleplayer() {
    this.loading = true
    try {
      const response = await gameApi.startSingleplayer()
      runInAction(() => {
        this.session = response.data
      })
    } finally {
      runInAction(() => {
        this.loading = false
      })
    }
  }

  async skipTime(weeks: number) {
    this.loading = true
    try {
      const response = await gameApi.skipTime(weeks)
      runInAction(() => {
        this.session = response.data
      })
    } finally {
      runInAction(() => {
        this.loading = false
      })
    }
  }

  async joinMultiplayer(sessionId: string) {
    this.loading = true
    try {
      const response = await gameApi.joinMultiplayer(sessionId)
      runInAction(() => {
        this.session = response.data
      })
    } finally {
      runInAction(() => {
        this.loading = false
      })
    }
  }
}
