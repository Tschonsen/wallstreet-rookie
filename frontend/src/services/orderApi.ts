import type { OrderEntry } from '../types'
import api from './api'

export const orderApi = {
  createOrder(data: { symbol: string; orderType: string; quantity: number; targetPrice: number }) {
    return api.post<OrderEntry>('/orders', data)
  },

  getOpenOrders() {
    return api.get<OrderEntry[]>('/orders')
  },

  cancelOrder(orderId: string) {
    return api.delete(`/orders/${orderId}`)
  },
}
