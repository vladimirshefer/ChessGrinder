import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react-swc';
import { fileURLToPath, URL } from 'node:url';

export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      components: fileURLToPath(new URL('./src/components', import.meta.url)),
      contexts: fileURLToPath(new URL('./src/contexts', import.meta.url)),
      lib: fileURLToPath(new URL('./src/lib', import.meta.url)),
      pages: fileURLToPath(new URL('./src/pages', import.meta.url)),
      strings: fileURLToPath(new URL('./src/strings', import.meta.url)),
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
  },
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './src/setupTests.ts',
    include: ['src/**/*.{test,spec}.{ts,tsx}'],
  },
});
