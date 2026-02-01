/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./components/**/*.{js,vue,ts}",
    "./layouts/**/*.vue",
    "./pages/**/*.vue",
    "./plugins/**/*.{js,ts}",
    "./app.vue",
  ],
  theme: {
    extend: {
      colors: {
        'ide-bg': '#0f172a',       // Fundo principal
        'ide-panel': '#1e293b',    // Fundo dos cartões
        'ide-accent': '#38bdf8',   // Azul claro
        'ide-success': '#22c55e',  // Verde
        'ide-text': '#e2e8f0',     // Texto claro
        'ide-dim': '#64748b',      // Texto secundário
      },
      fontFamily: {
        mono: ['ui-monospace', 'SFMono-Regular', 'Menlo', 'Monaco', 'Consolas', 'monospace'],
        sans: ['Inter', 'system-ui', 'sans-serif'],
      }
    },
  },
  keyframes: {
    shake: {
      '0%, 100%': { transform: 'translateX(0)' },
      '10%, 30%, 50%, 70%, 90%': { transform: 'translateX(-4px)' },
      '20%, 40%, 60%, 80%': { transform: 'translateX(4px)' },
    },
    pop: {
      '0%': { transform: 'scale(1)' },
      '50%': { transform: 'scale(1.1)' },
      '100%': { transform: 'scale(1)' },
    }
  },
  animation: {
    shake: 'shake 0.5s cubic-bezier(.36,.07,.19,.97) both',
    pop: 'pop 0.2s ease-in-out',
  },
  plugins: [],
}