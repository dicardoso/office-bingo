export default defineNuxtRouteMiddleware((to, from) => {
  const token = localStorage.getItem('bingo_token')

  if (to.path !== '/login' && !token) {
    return navigateTo('/login')
  }

  if (to.path === '/login' && token) {
    return navigateTo('/')
  }
})