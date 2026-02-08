<template>
  <div class="min-h-screen bg-ide-bg p-4 md:p-8">
    <div class="max-w-6xl mx-auto">
      
      <header class="flex justify-between items-end mb-8 border-b border-ide-border pb-4">
        <div>
          <div class="flex items-center gap-2 mb-1">
            <CodeBracketIcon class="w-6 h-6 text-ide-dim" />
            <span class="text-xs font-mono text-ide-dim">~/workspace/games/bingo</span>

            <div class="flex items-center gap-1 px-1.5 py-0.5 rounded bg-ide-dim/10 border border-ide-dim/20 ml-2">
              <TagIcon class="w-3 h-3 text-ide-accent" />
              <span class="text-[10px] font-mono font-bold text-ide-accent">{{ appVersion }}</span>
            </div>
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

          <div class="flex items-center gap-4">
          <div class="text-right hidden sm:block">
            <div class="text-sm font-bold text-white">{{ currentUser.username }}</div>
            
            <div class="flex items-center gap-2 justify-end mt-0.5">
              <span class="text-[10px] font-mono text-ide-accent uppercase tracking-wider">
                {{ currentUser.position || 'Estagiário' }}
              </span>
              <div class="w-16 h-1.5 bg-ide-dim/20 rounded-full overflow-hidden border border-ide-dim/30" :title="`XP: ${currentUser.careerXp || 0} / ${levelInfo.limit}`">
                <div 
                  class="h-full bg-ide-accent transition-all duration-500 ease-out" 
                  :style="{ width: levelInfo.percent + '%' }"
                ></div>
              </div>
            </div>
          </div>

          <div class="flex items-center gap-1 bg-ide-bg rounded-full border border-ide-border p-1">
            <button @click="openProfile" class="p-2 hover:bg-ide-panel rounded-full text-ide-dim hover:text-white transition-colors" title="View Profile Stats">
              <UserCircleIcon class="w-5 h-5" />
            </button>
            <div class="w-px h-4 bg-ide-border mx-1"></div>
            <button @click="logout" class="p-2 hover:bg-ide-panel rounded-full text-red-400 hover:bg-red-400/10 transition-colors" title="Exit Process">
              <ArrowRightOnRectangleIcon class="w-5 h-5" />
            </button>
          </div>
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
              @click="inspectPlayer(player)" 
              class="flex items-center gap-3 p-2 rounded transition-colors cursor-pointer group"
              :class="[
                index === 0 ? 'bg-yellow-500/5 border border-yellow-500/20' : 'hover:bg-ide-bg/50 border border-transparent hover:border-ide-dim/30',
                player.markedCount === 9 ? 'bg-green-500/5 border-l-2 border-l-ide-success' : ''
              ]"
            >
              <div class="font-mono text-ide-dim w-6 text-right group-hover:text-ide-accent">#{{ index + 1 }}</div>
              
              <div class="flex-1">
                <div class="flex justify-between items-center text-sm mb-1">
                  <div class="flex items-center gap-2">
                    <span class="font-bold group-hover:underline decoration-ide-dim underline-offset-2" :class="player.markedCount === 9 ? 'text-yellow-400' : 'text-white'">
                      {{ player.username }}
                    </span>
                    <div v-if="player.completed" class="flex items-center gap-1 px-1.5 py-0.5 rounded bg-yellow-400/10 border border-yellow-400/50 text-yellow-400 text-[10px] font-mono font-bold uppercase">
                      <TrophyIcon class="w-3 h-3" />
                    </div>
                  </div>

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
            
             <div v-if="leaderboard.length > 0" class="mt-4 text-[10px] text-center text-ide-dim font-mono">
                [ CLICK ON USER TO CODE REVIEW ]
            </div>
          </div>
        </aside>
        
        <transition
          enter-active-class="transition duration-200 ease-out"
          enter-from-class="opacity-0 scale-95"
          leave-active-class="transition duration-150 ease-in"
          leave-to-class="opacity-0 scale-95"
        >
          <div v-if="showProfile" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm" @click.self="showProfile = false">
            <div class="bg-ide-panel w-full max-w-lg rounded-lg border border-ide-border shadow-2xl overflow-hidden">
              
              <div class="bg-ide-bg px-6 py-4 border-b border-ide-border flex justify-between items-center">
                <div class="flex items-center gap-3">
                    <div class="p-2 bg-ide-accent/10 rounded-lg">
                      <ChartBarIcon class="w-6 h-6 text-ide-accent" />
                    </div>
                    <div>
                      <h3 class="font-mono font-bold text-white text-lg">Player Stats</h3>
                      <p class="text-xs text-ide-dim font-mono">~/profile/{{ currentUser.username }}.json</p>
                    </div>
                </div>
                <button @click="showProfile = false" class="text-ide-dim hover:text-white">
                    <span class="font-mono text-xs">[ ESC ]</span>
                </button>
              </div>

              <div class="p-6 space-y-6">
                
                <div class="text-center">
                  <div class="inline-block px-3 py-1 rounded-full bg-ide-accent/10 border border-ide-accent/20 text-ide-accent text-xs font-bold font-mono mb-2 uppercase tracking-widest">
                    {{ currentUser.position || 'Estagiário' }}
                  </div>
                  <h2 class="text-3xl font-bold text-white mb-1">{{ currentUser.careerXp || 0 }} XP</h2>
                  <p class="text-xs text-ide-dim mb-4">Lifetime Experience</p>
                  
                  <div class="relative h-4 bg-ide-bg rounded-full overflow-hidden border border-ide-border">
                    <div 
                      class="h-full bg-gradient-to-r from-blue-500 to-ide-accent transition-all duration-1000 ease-out"
                      :style="{ width: levelInfo.percent + '%' }"
                    ></div>
                    <div class="absolute inset-0 flex items-center justify-between px-3 text-[9px] font-mono font-bold text-white/50 mix-blend-difference">
                      <span>CURRENT</span>
                      <span>NEXT: {{ levelInfo.nextLevel }}</span>
                    </div>
                  </div>
                </div>

                <div class="h-px bg-ide-border"></div>

                <div class="grid grid-cols-3 gap-4">
                  <div class="bg-ide-bg p-3 rounded border border-ide-border text-center">
                    <div class="text-2xl font-bold text-white">{{ currentUser.stats?.totalGamesPlayed || 0 }}</div>
                    <div class="text-[10px] text-ide-dim font-mono uppercase mt-1">Games Played</div>
                  </div>
                  <div class="bg-ide-bg p-3 rounded border border-ide-border text-center">
                    <div class="text-2xl font-bold text-yellow-400">{{ currentUser.stats?.totalBingos || 0 }}</div>
                    <div class="text-[10px] text-ide-dim font-mono uppercase mt-1">Total Bingos</div>
                  </div>
                  <div class="bg-ide-bg p-3 rounded border border-ide-border text-center">
                    <div class="text-2xl font-bold text-green-400">{{ currentUser.stats?.totalSlotsMarked || 0 }}</div>
                    <div class="text-[10px] text-ide-dim font-mono uppercase mt-1">Slots Marked</div>
                  </div>
                </div>

                <div class="bg-ide-bg/50 p-4 rounded border border-ide-border flex justify-between items-center">
                  <div>
                    <div class="text-sm font-bold text-white">Season Progress</div>
                    <div class="text-xs text-ide-dim">Resets monthly</div>
                  </div>
                  <div class="text-right">
                    <div class="text-xl font-mono font-bold text-ide-accent">{{ currentUser.seasonXp || 0 }} XP</div>
                  </div>
                </div>

              </div>
            </div>
          </div>
        </transition>
      </div>
    </div>

    <transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="opacity-0 scale-95"
      leave-active-class="transition duration-150 ease-in"
      leave-to-class="opacity-0 scale-95"
    >
      <div v-if="inspectedCard" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm" @click.self="closeInspection">
        <div class="bg-ide-panel w-full max-w-lg rounded-lg border border-ide-border shadow-2xl overflow-hidden">
          
          <div class="bg-ide-bg px-4 py-3 border-b border-ide-border flex justify-between items-center">
            <div class="flex items-center gap-2">
                <CodeBracketIcon class="w-5 h-5 text-ide-accent" />
                <span class="font-mono font-bold text-white">Reviewing: {{ inspectedUser }}</span>
            </div>
            <button @click="closeInspection" class="text-ide-dim hover:text-white">
                <span class="font-mono text-xs">[ ESC ]</span>
            </button>
          </div>

          <div class="p-6">
            <div v-if="loadingInspection" class="flex justify-center py-8">
                <svg class="animate-spin h-8 w-8 text-ide-dim" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
            </div>
            <div v-else class="grid grid-cols-3 gap-3">
                <div 
                    v-for="slot in inspectedCard.slots" 
                    :key="slot.position"
                    class="aspect-square rounded border flex items-center justify-center p-2 text-center text-xs select-none"
                    :class="slot.marked 
                        ? 'bg-ide-success/5 border-ide-success/50 text-ide-success line-through opacity-80' 
                        : 'bg-ide-bg border-ide-border text-ide-dim opacity-50'"
                >
                    {{ slot.phrase }}
                </div>
            </div>
          </div>
          
          <div class="bg-ide-bg px-4 py-2 border-t border-ide-border text-center">
             <span class="text-[10px] font-mono text-ide-dim">READ-ONLY MODE enabled</span>
          </div>
        </div>
      </div>
    </transition>

  </div>
</template>
<script setup>
import SockJS from 'sockjs-client'
import Stomp from 'webstomp-client'
import confetti from 'canvas-confetti'
import { 
  TrophyIcon,
  TagIcon,
  ArrowRightOnRectangleIcon,
  CodeBracketIcon,
  UserCircleIcon, 
  ChartBarIcon
} from '@heroicons/vue/24/outline'
import { useSound } from '@/composables/useSound';
import {version} from '../../package.json'

const { play } = useSound()
const api = useAPI()
const router = useRouter()
const config = useRuntimeConfig()

const currentUser = ref('')
const card = ref(null)
const leaderboard = ref([])
const socketConnected = ref(false)
const lastWinner = ref(null)
const isLoading = ref(true)

const inspectedCard = ref(null)
const inspectedUser = ref('')
const loadingInspection = ref(false)

const showProfile = ref(false)
const isLoadingProfile = ref(false)

const appVersion = version

const connectSocket = () => {
  const socket = new SockJS(`${config.public.apiBase}/ws`)
  console.log(socket)
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
  console.log(update)
  const player = leaderboard.value.find(u => u.username === update.username)
  if (player) {
    player.markedCount = update.markedCount
    player.completed = update.completed
  } else {
    leaderboard.value.push({ username: update.username, markedCount: update.markedCount, completed: update.completed })
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
  
  play('win')
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

    if (slot.marked) { // Se marcou
        currentUser.value.careerXp = (currentUser.value.careerXp || 0) + 10
        currentUser.value.seasonXp = (currentUser.value.seasonXp || 0) + 10
        if (currentUser.value.stats) currentUser.value.stats.totalSlotsMarked++
    } else { // Se desmarcou
        currentUser.value.careerXp = Math.max(0, (currentUser.value.careerXp || 0) - 10)
        currentUser.value.seasonXp = Math.max(0, (currentUser.value.seasonXp || 0) - 10)
        if (currentUser.value.stats) currentUser.value.stats.totalSlotsMarked--
    }

    if (response.completed && !card.value.completed) {
      play('win')
      currentUser.value.careerXp += 150
      currentUser.value.seasonXp += 150
    }
    localStorage.setItem('bingo_user', JSON.stringify(currentUser.value))
  } catch (error) {
    slot.marked = originalState
    alert('Erro de sincronização. Tente novamente.')
  }
}

const closeInspection = () => {
    inspectedCard.value = null
    inspectedUser.value = ''
}

const openProfile = async () => {
  showProfile.value = true // Abre o modal imediatamente
  isLoadingProfile.value = true // Ativa o spinner do modal

  try {
    const updatedUser = await api('/auth/me')
    
    // Atualiza o estado local com os dados frescos do banco
    currentUser.value = updatedUser
    
    // Atualiza o localStorage para manter sincronizado caso dê F5 depois
    localStorage.setItem('bingo_user', JSON.stringify(updatedUser))
  } catch (error) {
    console.error("Erro ao carregar perfil", error)
  } finally {
    isLoadingProfile.value = false // Desativa o spinner
  }
}

const inspectPlayer = async (player) => {
    if (player.username === currentUser.value.username) return;

    inspectedUser.value = player.username
    inspectedCard.value = { slots: [] }
    loadingInspection.value = true
    
    try {
        const data = await api(`/game/card/${player.username}`)
        inspectedCard.value = data
    } catch (e) {
        console.error("Erro ao inspecionar", e)
        console.log(e)
        alert("Não foi possível carregar o código fonte deste usuário.")
        closeInspection()
    } finally {
        loadingInspection.value = false
    }
}

const getLevelProgress = (xp = 0) => {
  const milestones = [
    { name: 'Estagiário', limit: 500 },
    { name: 'Júnior', limit: 2000 },
    { name: 'Pleno', limit: 5000 },
    { name: 'Sênior', limit: 10000 },
    { name: 'Tech Lead', limit: Infinity }
  ]

  let previousLimit = 0
  for (const level of milestones) {
    if (xp < level.limit) {
      const range = level.limit - previousLimit
      const current = xp - previousLimit
      const percent = Math.min(100, Math.max(0, (current / range) * 100))
      
      return {
        nextLevel: level.name === 'Estagiário' ? 'Júnior' : level.name,
        limit: level.limit,
        percent: percent
      }
    }
    previousLimit = level.limit
  }
  return { nextLevel: 'Max Level', limit: 0, percent: 100 }
}
const levelInfo = computed(() => getLevelProgress(currentUser.value.careerXp))

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