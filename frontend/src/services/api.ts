import axios from 'axios'
import { notification } from 'antd'

const api = axios.create({
  baseURL: '/',
})

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      window.location.href = '/login'
    } else if (error.response?.data?.message) {
      notification.error({
        message: 'Fehler',
        description: error.response.data.message,
        duration: 5,
      })
    }
    return Promise.reject(error)
  },
)

export default api
