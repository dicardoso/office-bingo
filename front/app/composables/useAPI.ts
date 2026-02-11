  export const useAPI = () => {
    const config = useRuntimeConfig()
    const baseURL = `http://172.16.155.182:8080/api`

    return $fetch.create({
      baseURL,
      onRequest({ options }) {
        const token = localStorage.getItem('bingo_token')
        
        if (token) {
          const headers = new Headers(options.headers)
          
          headers.set('Authorization', `Bearer ${token}`)
          
          options.headers = headers
        }
      },
      onResponseError({ response }) {
        if (response.status === 401 || response.status === 403) {
          if (process.client) {
              localStorage.removeItem('bingo_token')
              window.location.href = '/login'
          }
        }
      }
    })
  }