import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { ConfigProvider, theme } from 'antd'
import { StoreContext, RootStore } from './stores/RootStore'

const rootStore = new RootStore()

function App() {
  return (
    <StoreContext.Provider value={rootStore}>
      <ConfigProvider
        theme={{
          algorithm: theme.darkAlgorithm,
        }}
      >
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={<div>Login Page (TODO)</div>} />
            <Route path="/" element={<Navigate to="/login" replace />} />
          </Routes>
        </BrowserRouter>
      </ConfigProvider>
    </StoreContext.Provider>
  )
}

export default App
