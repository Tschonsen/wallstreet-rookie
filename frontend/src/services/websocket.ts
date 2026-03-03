import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client'

let stompClient: Client | null = null

export function connectWebSocket(onConnect?: () => void) {
  stompClient = new Client({
    webSocketFactory: () => new SockJS('/ws'),
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
