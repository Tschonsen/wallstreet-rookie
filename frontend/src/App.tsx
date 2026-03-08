import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { ConfigProvider, theme, App as AntApp } from 'antd'
import { StoreContext, RootStore } from './stores/RootStore'
import LoginPage from './pages/LoginPage'
import ModeSelectPage from './pages/ModeSelectPage'
import DashboardPage from './pages/DashboardPage'
import TradingPage from './pages/TradingPage'
import StockDetailPage from './pages/StockDetailPage'
import PortfolioPage from './pages/PortfolioPage'
import LeaderboardPage from './pages/LeaderboardPage'
import ProtectedRoute from './components/ProtectedRoute'
import AppLayout from './components/AppLayout'

const rootStore = new RootStore()

function App() {
  return (
    <StoreContext.Provider value={rootStore}>
      <ConfigProvider
        theme={{
          algorithm: theme.darkAlgorithm,
        }}
      >
        <AntApp>
          <BrowserRouter>
            <Routes>
              <Route path="/login" element={<LoginPage />} />
              <Route
                path="/"
                element={
                  <ProtectedRoute>
                    <ModeSelectPage />
                  </ProtectedRoute>
                }
              />
              <Route
                element={
                  <ProtectedRoute>
                    <AppLayout />
                  </ProtectedRoute>
                }
              >
                <Route path="/dashboard" element={<DashboardPage />} />
                <Route path="/trading" element={<TradingPage />} />
                <Route path="/trading/:symbol" element={<StockDetailPage />} />
                <Route path="/portfolio" element={<PortfolioPage />} />
                <Route path="/leaderboard" element={<LeaderboardPage />} />
              </Route>
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </BrowserRouter>
        </AntApp>
      </ConfigProvider>
    </StoreContext.Provider>
  )
}

export default App
