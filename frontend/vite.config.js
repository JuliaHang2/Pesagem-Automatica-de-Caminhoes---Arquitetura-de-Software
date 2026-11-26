import { Navigate, NavLink, Route, Routes, useLocation, useNavigate } from 'react-router-dom';
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: { port: 5173, proxy: { '/api': 'http://localhost:8080' } },
});
