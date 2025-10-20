import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react-swc';
import path from "path";

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src'),
      components: path.resolve(__dirname, 'src/components'),
      contexts: path.resolve(__dirname, 'src/contexts'),
      lib: path.resolve(__dirname, 'src/lib'),
      pages: path.resolve(__dirname, 'src/pages'),
      strings: path.resolve(__dirname, 'src/strings'),
    },
  },
  build: {
    outDir: 'build',
  },
  server: {
    port: 3000,
  },
  preview: {
    port: 3000,
  }
});
