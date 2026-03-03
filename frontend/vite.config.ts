import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/auth': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/market': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/trade': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/player': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/game': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/leaderboard': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
      '/ws': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        ws: true,
      },
    },
  },
})
