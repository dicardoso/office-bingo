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

  future: {
    compatibilityVersion: 4,
  }
})