import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { ConfigProvider, theme, App as AntApp } from 'antd'
import { StoreContext, RootStore } from './stores/RootStore'
import LoginPage from './pages/LoginPage'
import TradingPage from './pages/TradingPage'
import StockDetailPage from './pages/StockDetailPage'
import ProtectedRoute from './components/ProtectedRoute'

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
                    <div>ModeSelect Page (TODO)</div>
                  </ProtectedRoute>
                }
              />
              <Route
                path="/trading"
                element={
                  <ProtectedRoute>
                    <TradingPage />
                  </ProtectedRoute>
                }
              />
              <Route
                path="/trading/:symbol"
                element={
                  <ProtectedRoute>
                    <StockDetailPage />
                  </ProtectedRoute>
                }
              />
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </BrowserRouter>
        </AntApp>
      </ConfigProvider>
    </StoreContext.Provider>
  )
}

export default App
