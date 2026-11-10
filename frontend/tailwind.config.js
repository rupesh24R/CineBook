/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        cinema: {
          darkest: '#070a12',
          navy: '#0b1120',
          surface: '#121829',
          card: '#182035',
          border: '#222f4c',
          text: '#e2e8f0',
          muted: '#94a3b8',
        },
        accent: {
          purple: '#8b5cf6',
          purpleDark: '#6d28d9',
          purpleLight: '#a78bfa',
          gold: '#f59e0b',
          goldDark: '#b45309',
          goldLight: '#fbbf24',
        }
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'sans-serif'],
        display: ['Cabinet Grotesk', 'Plus Jakarta Sans', 'Inter', 'sans-serif'],
      },
      boxShadow: {
        'cinema-glow': '0 0 25px -5px rgba(139, 92, 246, 0.25)',
        'gold-glow': '0 0 25px -5px rgba(245, 158, 11, 0.3)',
      }
    },
  },
  plugins: [],
}

