import { observer } from 'mobx-react-lite'
import { Navigate } from 'react-router-dom'
import { useStore } from '../stores/RootStore'

interface ProtectedRouteProps {
  children: React.ReactNode
}

const ProtectedRoute = observer(({ children }: ProtectedRouteProps) => {
  const { playerStore } = useStore()

  if (!playerStore.isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  return <>{children}</>
})

export default ProtectedRoute
