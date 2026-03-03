import type { AuthResponse } from '../types'
import api from './api'

export const authApi = {
  register(username: string, password: string, email: string) {
    return api.post<AuthResponse>('/auth/register', { username, password, email })
  },

  login(username: string, password: string) {
    return api.post<AuthResponse>('/auth/login', { username, password })
  },
}
