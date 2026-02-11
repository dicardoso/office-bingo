export default defineNuxtConfig({
  ssr: false,

  devtools: { enabled: true },
  modules: ['@nuxtjs/tailwindcss'],

  app: {
    head: {
      title: 'Office Bingo',
      bodyAttrs: {
        class: 'bg-ide-bg text-ide-text antialiased font-sans selection:bg-ide-accent selection:text-ide-bg'
      }
    }
  },
  runtimeConfig: {
    public: {
      apiBase: process.env.URL_API || 'http://172.16.155.182:8080/api'
    }
  },

  future: {
    compatibilityVersion: 4,
  }
})