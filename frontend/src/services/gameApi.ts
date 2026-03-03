import type { GameSession } from '../types'
import api from './api'

export const gameApi = {
  startSingleplayer() {
    return api.post<GameSession>('/game/start', { mode: 'SP' })
  },

  skipTime(weeks: number) {
    return api.post<GameSession>('/game/skip', { weeks })
  },

  joinMultiplayer(sessionId: string) {
    return api.post<GameSession>(`/game/join/${sessionId}`)
  },
}
