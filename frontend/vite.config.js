import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    proxy: {
      '/admin': { target: 'http://127.0.0.1:8088', changeOrigin: true },
      '/api': { target: 'http://127.0.0.1:8088', changeOrigin: true },
      '/auth': { target: 'http://127.0.0.1:8088', changeOrigin: true },
      '/home': { target: 'http://127.0.0.1:8088', changeOrigin: true },
      '/uploads': { target: 'http://127.0.0.1:8088', changeOrigin: true }
    }
  }
})
