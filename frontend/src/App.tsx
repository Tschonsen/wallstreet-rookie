import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { ConfigProvider, theme } from 'antd'
import { StoreContext, RootStore } from './stores/RootStore'
import LoginPage from './pages/LoginPage'
import ProtectedRoute from './components/ProtectedRoute'

const rootStore = new RootStore()

function App() {
  return (
    <StoreContext.Provider value={rootStore}>
      <ConfigProvider
        theme={{
          algorithm: theme.darkAlgorithm,
          token: {
            colorPrimary: '#1677ff',
            colorSuccess: '#52c41a',
            colorError: '#ff4d4f',
            colorBgContainer: '#1f1f1f',
            colorBgElevated: '#262626',
            colorBgLayout: '#141414',
            borderRadius: 6,
            fontFamily: "-apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif",
          },
        }}
      >
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
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </BrowserRouter>
      </ConfigProvider>
    </StoreContext.Provider>
  )
}

export default App
