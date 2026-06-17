import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
const backendTarget = process.env.VITE_PROXY_TARGET ?? "http://localhost:8080"

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: {
    host: true,
    proxy: {
      "/api": backendTarget,
      "/uploads": backendTarget,
    },
  },
})
