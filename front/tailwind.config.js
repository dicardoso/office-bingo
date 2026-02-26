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
        'ide-bg': 'rgb(var(--color-ide-bg) / <alpha-value>)',
        'ide-panel': 'rgb(var(--color-ide-panel) / <alpha-value>)',
        'ide-border': 'rgb(var(--color-ide-border) / <alpha-value>)',
        'ide-accent': 'rgb(var(--color-ide-accent) / <alpha-value>)',
        'ide-success': 'rgb(var(--color-ide-success) / <alpha-value>)',
        'ide-error': 'rgb(var(--color-ide-error) / <alpha-value>)',
        'ide-text': 'rgb(var(--color-ide-text) / <alpha-value>)',
        'ide-dim': 'rgb(var(--color-ide-dim) / <alpha-value>)',
      },
      fontFamily: {
        mono: ['ui-monospace', 'SFMono-Regular', 'Menlo', 'Monaco', 'Consolas', 'monospace'],
        sans: ['Inter', 'system-ui', 'sans-serif'],
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
      }
    },
  },
  plugins: [],
}