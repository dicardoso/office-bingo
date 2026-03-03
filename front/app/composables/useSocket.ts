import { ref } from 'vue'
import SockJS from 'sockjs-client'
import Stomp from 'webstomp-client'

const stompClient = ref(null)
const isConnected = ref(false)

export function useSocket() {
  
  const connect = (userId) => {
    if (isConnected.value && stompClient.value) return stompClient.value

    const socket = new SockJS(`http://172.16.155.182:8080/ws`) 
    const client = Stomp.over(socket)
    
    client.debug = () => {}

    client.connect({}, () => {
      isConnected.value = true
      stompClient.value = client
      
      if (userId) {
        client.send("/app/presence/connect", String(userId), {})
      }
      
    }, (err) => {
      console.error('Socket desconectado ou falha na ligação:', err)
      isConnected.value = false
      stompClient.value = null
      
      setTimeout(() => connect(userId), 5000)
    })

    return client
  }

  const disconnect = () => {
    if (stompClient.value && isConnected.value) {
      stompClient.value.disconnect()
      isConnected.value = false
      stompClient.value = null
    }
  }

  const subscribe = (topic, callback) => {
    if (!isConnected.value || !stompClient.value) {
      console.warn(`Tentou subscrever em ${topic} mas o socket não está ligado.`)
      return null
    }
    return stompClient.value.subscribe(topic, callback)
  }

  const send = (destination, body = {}, headers = {}) => {
    if (isConnected.value && stompClient.value) {
        stompClient.value.send(destination, JSON.stringify(body), headers)
    }
  }

  return {
    stompClient,
    isConnected,
    connect,
    disconnect,
    subscribe,
    send
  }
}