<template>
  <div class="min-h-screen bg-ide-bg p-4 md:p-8">
    <div class="max-w-6xl mx-auto">
      
      <header class="flex justify-between items-end mb-8 border-b border-ide-border pb-4">
        <div>
          <div class="flex items-center gap-2 mb-1">
            <CodeBracketIcon class="w-6 h-6 text-ide-dim" />
            <span class="text-xs font-mono text-ide-dim">~/workspace/games/bingo</span>
          </div>
          <h1 class="text-2xl md:text-3xl font-bold font-mono text-ide-accent">
            ./run_bingo.sh
          </h1>
        </div>
        
        <div class="flex items-center gap-6">
          <div class="hidden md:flex items-center gap-2 px-3 py-1 rounded bg-ide-bg border border-ide-border">
            <div class="w-2 h-2 rounded-full" :class="socketConnected ? 'bg-ide-success animate-pulse' : 'bg-red-500'"></div>
            <span class="text-xs font-mono text-ide-dim uppercase">
              {{ socketConnected ? 'Connected: 8080' : 'Offline' }}
            </span>
          </div>

          
          <div class="flex items-center gap-3">
            <div class="text-right">
              <div class="text-sm font-bold text-white">{{ currentUser.username }}</div>
              <div class="text-[10px] font-mono text-ide-dim uppercase">{{ currentUser.position || 'Dev' }}</div>
            </div>
            <button @click="logout" class="p-2 hover:bg-ide-panel rounded-full text-red-400 transition-colors" title="Exit Process">
              <ArrowRightOnRectangleIcon class="w-5 h-5" />
            </button>
          </div>
        </div>
      </header>
      <transition 
        enter-active-class="transform transition duration-500 ease-out" 
        enter-from-class="-translate-y-full opacity-0"
        leave-active-class="transform transition duration-500 ease-in"
        leave-to-class="-translate-y-full opacity-0"
      >
        <div v-if="lastWinner" class="fixed top-6 left-0 right-0 z-50 flex justify-center pointer-events-none">
          <div :class="lastWinner.username === currentUser.username ?'bg-yellow-500/90 border-yellow-300': 'bg-blue-500/90 border-blue-300'" class="text-ide-bg px-8 py-4 rounded shadow-2xl backdrop-blur-sm border-2 transform scale-110">
            <div class="flex items-center gap-3">
              <TrophyIcon class="w-8 h-8" />
              <div>
                <h3 class="font-black text-xl font-mono uppercase">Build Successful!</h3>
                <p class="font-bold">{{ lastWinner.message }}</p>
              </div>
            </div>
          </div>
        </div>
      </transition>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        <main class="lg:col-span-2 space-y-4">
          <div class="bg-ide-panel rounded-lg border border-ide-border shadow-xl overflow-hidden">
            <div class="bg-ide-bg px-4 py-2 border-b border-ide-border flex justify-between items-center">
              <span class="text-xs font-mono text-ide-dim">daily_ticket.json</span>
              <span v-if="card?.completed" class="text-xs font-bold text-yellow-400 font-mono animate-pulse">[ STATUS: COMPLETED ]</span>
              <span v-else class="text-xs font-mono text-ide-dim">[ READ-WRITE ]</span>
            </div>

            <div v-if="isLoading" class="p-12 flex justify-center text-ide-accent">
              <svg class="animate-spin h-8 w-8" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
            </div>

            <div v-else class="p-6 grid grid-cols-3 gap-4">
              <button 
                v-for="slot in card.slots" 
                :key="slot.position"
                @click="toggleSlot(slot)"
                class="relative group aspect-square rounded border-2 flex flex-col items-center justify-center p-2 transition-all duration-200 select-none active:scale-95"
                :class="slot.marked 
                  ? 'bg-ide-success/10 border-ide-success text-ide-success shadow-[0_0_15px_rgba(74,222,128,0.2)]' 
                  : 'bg-ide-bg border-ide-border hover:border-ide-accent/50 hover:bg-ide-bg/80 text-ide-text'"
              >
                <span class="text-sm md:text-base font-medium text-center z-10 leading-tight" :class="{'line-through opacity-70': slot.marked}">
                  {{ slot.phrase }}
                </span>
                
                <div v-if="slot.marked" class="absolute top-2 right-2 opacity-50">
                  <svg xmlns="http://www.w3.org/2000/svg" class="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
                    <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd" />
                  </svg>
                </div>
              </button>
            </div>
          </div>
        </main>

        <aside class="bg-ide-panel rounded-lg border border-ide-border h-fit shadow-xl">
          <div class="bg-ide-bg px-4 py-2 border-b border-ide-border">
            <span class="text-xs font-mono text-ide-dim">CONTRIBUTORS.md</span>
          </div>
          
          <div class="p-4 space-y-3">
            <div 
              v-for="(player, index) in leaderboard" 
              :key="index"
              class="flex items-center gap-3 p-2 rounded hover:bg-ide-bg/50 transition-colors"
              :class="{'bg-yellow-500/10 border border-yellow-500/20': index === 0}"
            >
              <div class="font-mono text-ide-dim w-6 text-right">#{{ index + 1 }}</div>
              
              <div class="flex-1">
                <div class="flex justify-between text-sm mb-1">
                  <span class="font-bold text-white">{{ player.username }}</span>
                  <span class="font-mono" :class="player.markedCount === 9 ? 'text-yellow-400' : 'text-ide-dim'">
                    {{ player.markedCount }}/9
                  </span>
                </div>
                <div class="h-1.5 w-full bg-ide-bg rounded-full overflow-hidden">
                  <div 
                    class="h-full bg-ide-accent transition-all duration-500 ease-out relative"
                    :style="{ width: (player.markedCount / 9 * 100) + '%' }"
                    :class="{'bg-ide-success': player.markedCount === 9}"
                  ></div>
                </div>
              </div>
            </div>

            <div v-if="leaderboard.length === 0" class="text-center py-4 text-ide-dim text-sm italic font-mono">
              // No logs found yet...
            </div>
          </div>
        </aside>

      </div>
    </div>
  </div>
</template>
<script setup>
import SockJS from 'sockjs-client'
import Stomp from 'webstomp-client'
import confetti from 'canvas-confetti'
import { TrophyIcon, ArrowRightOnRectangleIcon, CodeBracketIcon } from '@heroicons/vue/24/outline'
import { useSound } from '@/composables/useSound';

const { play } = useSound()
const api = useAPI()
const router = useRouter()

const currentUser = ref('')
const card = ref(null)
const leaderboard = ref([])
const socketConnected = ref(false)
const lastWinner = ref(null)
const isLoading = ref(true)

const connectSocket = () => {
  const socket = new SockJS('http://localhost:8080/ws')
  const stompClient = Stomp.over(socket)
  
  stompClient.debug = () => {}

  stompClient.connect({}, () => {
    socketConnected.value = true
    
    stompClient.subscribe('/topic/progress', (tick) => {
      const update = JSON.parse(tick.body)
      if (update.username !== currentUser.value.username) {
        play('notification');
      }
      updateLeaderboardLocal(update)
    })

    stompClient.subscribe('/topic/winners', (tick) => {
      const winner = JSON.parse(tick.body)
      handleWinner(winner)
    })
  }, (err) => {
    console.error('Socket connection error:', err)
    socketConnected.value = false
    setTimeout(connectSocket, 5000)
  })
}

const updateLeaderboardLocal = (update) => {
  const player = leaderboard.value.find(u => u.username === update.username)
  if (player) {
    player.markedCount = update.markedCount
  } else {
    leaderboard.value.push({ username: update.username, markedCount: update.markedCount })
  }
  leaderboard.value.sort((a, b) => b.markedCount - a.markedCount)
}

const handleWinner = (winner) => {
  lastWinner.value = winner
  const end = Date.now() + 3000
  ;(function frame() {
    confetti({ particleCount: 5, spread: 60, origin: { y: 0.6 } })
    if (Date.now() < end) requestAnimationFrame(frame)
  }())
  
  setTimeout(() => lastWinner.value = null, 6000)
}

const loadData = async () => {
  try {
    isLoading.value = true
    const [cardData, leaderData] = await Promise.all([
      api('/game/my-card'),
      api('/game/leaderboard')
    ])
    card.value = cardData
    leaderboard.value = leaderData
  } catch (error) {
    console.error('Erro ao carregar dados:', error)
  } finally {
    isLoading.value = false
  }
}

const toggleSlot = async (slot) => {
  const originalState = slot.marked

  play('click');
  slot.marked = !originalState
  
  try {
    const response = await api(`/game/mark/${slot.position}`, { method: 'POST' })
    card.value = response
    if (response.completed && !card.value.completed) {
      play('win')
    }
  } catch (error) {
    slot.marked = originalState
    alert('Erro de sincronização. Tente novamente.')
  }
}

const logout = () => {
  localStorage.removeItem('bingo_token')
  router.push('/login')
}

onMounted(() => {
  currentUser.value = JSON.parse(localStorage.getItem('bingo_user')) || 'Dev'
  loadData()
  connectSocket()
})
</script>