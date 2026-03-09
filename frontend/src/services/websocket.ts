import { Client } from '@stomp/stompjs'

let stompClient: Client | null = null

export function connectWebSocket(onConnect?: () => void) {
  const token = localStorage.getItem('token')

  stompClient = new Client({
    brokerURL: `ws://${window.location.host}/ws`,
    connectHeaders: token ? { Authorization: `Bearer ${token}` } : {},
    reconnectDelay: 5000,
    onConnect: () => {
      onConnect?.()
    },
  })

  stompClient.activate()
}

export function subscribe<T>(destination: string, callback: (message: T) => void) {
  if (!stompClient?.connected) return null

  return stompClient.subscribe(destination, (message) => {
    callback(JSON.parse(message.body) as T)
  })
}

export function disconnectWebSocket() {
  stompClient?.deactivate()
  stompClient = null
}
