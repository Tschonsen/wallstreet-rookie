import type { GameSession } from '../types'
import api from './api'

export const gameApi = {
  startSingleplayer() {
    return api.post<GameSession>('/game/singleplayer')
  },

  skipTime(sessionId: string, weeks: number) {
    return api.post<GameSession>(`/game/session/${sessionId}/skip`, { weeks })
  },

  joinMultiplayer() {
    return api.post<GameSession>('/game/multiplayer/join')
  },

  getSession(sessionId: string) {
    return api.get<GameSession>(`/game/session/${sessionId}`)
  },

  pauseSession(sessionId: string) {
    return api.post<GameSession>(`/game/session/${sessionId}/pause`)
  },

  resumeSession(sessionId: string) {
    return api.post<GameSession>(`/game/session/${sessionId}/resume`)
  },
}
