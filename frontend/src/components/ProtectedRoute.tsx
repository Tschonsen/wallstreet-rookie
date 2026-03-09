import { useEffect } from 'react'
import { observer } from 'mobx-react-lite'
import { Navigate } from 'react-router-dom'
import { runInAction } from 'mobx'
import { useStore } from '../stores/RootStore'
import { subscribe } from '../services/websocket'
import type { PortfolioResponse } from '../types'

interface ProtectedRouteProps {
  children: React.ReactNode
}

const ProtectedRoute = observer(({ children }: ProtectedRouteProps) => {
  const { playerStore, marketStore } = useStore()

  useEffect(() => {
    if (!playerStore.isAuthenticated) return

    marketStore.connectWebSocket(() => {
      subscribe<PortfolioResponse>('/user/queue/portfolio', (data) => {
        runInAction(() => {
          playerStore.updateFromWebSocket(data)
        })
      })
    })

    return () => {
      marketStore.disconnectWebSocket()
    }
  }, [playerStore.isAuthenticated, marketStore, playerStore])

  if (!playerStore.isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  return <>{children}</>
})

export default ProtectedRoute
