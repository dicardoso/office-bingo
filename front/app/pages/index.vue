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
              <div class="text-sm font-bold text-ide-text">{{ currentUser.username }}</div>
              
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
              <button 
                v-if="currentUser?.role === 'ADMIN'" 
                @click="$router.push('/admin')" 
                class="p-2 hover:bg-ide-panel rounded-full text-ide-accent hover:text-white transition-colors" 
                title="Abrir Terminal Admin"
              >
                <CommandLineIcon class="w-5 h-5" />
              </button>
              <button @click="openProfile" class="p-2 hover:bg-ide-panel rounded-full text-ide-dim hover:text-ide-text transition-colors" title="View Profile Stats">
                <UserCircleIcon class="w-5 h-5" />
              </button>
              <div class="relative">
                <button 
                  @click="showThemeMenu = !showThemeMenu" 
                  :disabled="currentTheme === 'troll'"
                  class="p-2 rounded-full transition-colors"
                  :class="currentTheme === 'troll' ? 'opacity-30 cursor-not-allowed text-ide-error' : 'hover:bg-ide-panel text-ide-dim hover:text-ide-text'"
                  :title="currentTheme === 'troll' ? 'HACKED: Você perdeu o direito de escolher! 😈' : 'Mudar Tema'"
                >
                  <SwatchIcon class="w-5 h-5" />
                </button>

                <transition
                    enter-active-class="transition duration-100 ease-out"
                    enter-from-class="transform scale-95 opacity-0"
                    leave-active-class="transition duration-75 ease-in"
                    leave-to-class="transform scale-95 opacity-0"
                >
                    <div v-if="showThemeMenu" class="absolute right-0 mt-2 w-48 bg-ide-panel border border-ide-border rounded-lg shadow-2xl z-50 overflow-hidden">
                        <div class="px-3 py-2 border-b border-ide-border text-[10px] font-mono text-ide-dim uppercase tracking-wider">
                            Select Theme
                        </div>
                        <div class="p-1">
                            <button 
                                v-for="theme in availableThemes" 
                                :key="theme.id"
                                @click="changeTheme(theme.id)"
                                class="w-full text-left flex items-center gap-3 px-3 py-2 text-sm rounded hover:bg-ide-bg transition-colors"
                                :class="currentTheme === theme.id ? 'text-ide-accent font-bold bg-ide-bg/50' : 'text-ide-text'"
                            >
                                <span>{{ theme.icon }}</span>
                                {{ theme.name }}
                            </button>
                        </div>
                    </div>
                </transition>
              </div>
              <div class="w-px h-4 bg-ide-border bg-ide-dim"></div>
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

      <transition 
        enter-active-class="transform transition duration-500 ease-out" 
        enter-from-class="-translate-y-full opacity-0"
        leave-active-class="transform transition duration-500 ease-in"
        leave-to-class="-translate-y-full opacity-0"
      >
        <div v-if="adminMessage" class="fixed top-24 left-0 right-0 z-[100] flex justify-center pointer-events-none">
          <div class="bg-ide-accent text-ide-bg px-6 py-3 rounded-lg shadow-[0_0_30px_rgba(0,0,0,0.5)] border-2 border-white flex items-center gap-3 max-w-2xl transform scale-105">
            <span class="text-2xl animate-bounce">📢</span>
            <div>
              <h3 class="font-black text-[10px] font-mono uppercase tracking-widest opacity-80">Mensagem do Game Master</h3>
              <p class="font-bold text-sm">{{ adminMessage }}</p>
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
                    <span class="font-bold group-hover:underline decoration-ide-dim underline-offset-2" :class="player.markedCount === 9 ? 'text-ide-accent' : 'text-ide-text'">
                      {{ player.username }}
                    </span>
                    <div v-if="player.completed" class="flex items-center gap-1 px-1.5 py-0.5 rounded bg-ide-accent/10 border border-ide-accent/50 text-ide-accent text-[10px] font-mono font-bold uppercase transition-colors">
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
                      <h3 class="font-mono font-bold text-ide-text text-lg">Player Stats</h3>
                      <p class="text-xs text-ide-dim font-mono">~/profile/{{ currentUser.username }}.json</p>
                    </div>
                </div>
                <button @click="showProfile = false" class="text-ide-dim hover:text-ide-text">
                    <span class="font-mono text-xs">[ ESC ]</span>
                </button>
              </div>

              <div class="p-6 space-y-6">
                
                <div class="text-center">
                  <div class="inline-block px-3 py-1 rounded-full bg-ide-accent/10 border border-ide-accent/20 text-ide-accent text-xs font-bold font-mono mb-2 uppercase tracking-widest">
                    {{ currentUser.position || 'Estagiário' }}
                  </div>
                  <h2 class="text-3xl font-bold text-ide-text mb-1">{{ currentUser.careerXp || 0 }} XP</h2>
                  <p class="text-xs text-ide-dim mb-4">Lifetime Experience</p>
                  
                  <div class="relative h-4 bg-ide-bg rounded-full overflow-hidden border border-ide-border">
                    <div 
                      class="h-full bg-gradient-to-r from-blue-500 to-ide-accent transition-all duration-1000 ease-out"
                      :style="{ width: levelInfo.percent + '%' }"
                    ></div>
                    <div class="absolute inset-0 flex items-center justify-between px-3 text-[9px] font-mono font-bold text-ide-text mix-blend-difference">
                      <span>CURRENT</span>
                      <span>NEXT: {{ levelInfo.nextLevel }}</span>
                    </div>
                  </div>
                </div>

                <div class="h-px bg-ide-border"></div>

                <div class="grid grid-cols-3 gap-4">
                  <div class="bg-ide-bg p-3 rounded border border-ide-border text-center">
                    <div class="text-2xl font-bold text-ide-text">{{ currentUser.stats?.totalGamesPlayed || 0 }}</div>
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
                    <div class="text-sm font-bold text-ide-text">Season Progress</div>
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
                <span class="font-mono font-bold text-ide-text">Reviewing: {{ inspectedUser }}</span>
            </div>
            <button @click="closeInspection" class="text-ide-dim hover:text-ide-text">
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
        class="aspect-square rounded border flex flex-col items-center justify-center p-2 text-center text-xs select-none relative group transition-all"
        :class="[
            slot.verified 
                ? 'bg-yellow-500/10 border-yellow-500 text-yellow-500 shadow-[0_0_10px_rgba(234,179,8,0.2)]' 
                : slot.marked 
                    ? 'bg-ide-success/5 border-ide-success/50 text-ide-success line-through opacity-80 cursor-pointer hover:bg-red-900/20 hover:border-red-500 hover:text-red-400' 
                    : 'bg-ide-bg border-ide-border text-ide-dim opacity-50'
        ]"
        @click="slot.marked && !slot.verified ? initiateAudit(slot) : null"
    >
        {{ slot.phrase }}

        <div v-if="slot.verified" class="absolute -top-2 -right-2 bg-ide-bg rounded-full p-0.5 border border-yellow-500 shadow-sm z-10">
            <ShieldCheckIcon class="w-4 h-4 text-yellow-500" />
        </div>
        
        <div v-if="slot.marked && !slot.verified" class="absolute inset-0 flex flex-col items-center justify-center bg-black/80 opacity-0 group-hover:opacity-100 transition-opacity rounded backdrop-blur-[2px]">
            <ScaleIcon class="w-6 h-6 text-red-500 mb-1" />
            <span class="text-[10px] font-bold text-red-400 uppercase tracking-widest">Contestar</span>
        </div>
    </div>
</div>
          </div>
          
          <div class="bg-ide-bg px-4 py-2 border-t border-ide-border text-center">
             <span class="text-[10px] font-mono text-ide-dim">READ-ONLY MODE enabled</span>
          </div>
        </div>
      </div>
      
    </transition>

    <transition
      enter-active-class="transition duration-500 cubic-bezier(0.34, 1.56, 0.64, 1)"
      enter-from-class="translate-y-full opacity-0 scale-90"
      leave-active-class="transition duration-300 ease-in"
      leave-to-class="translate-y-full opacity-0 scale-90"
    >
      <div v-if="activeAudit" class="fixed bottom-4 right-4 left-4 md:left-auto md:w-96 z-[100]">
        <div class="bg-ide-panel border-2 rounded-lg shadow-2xl overflow-hidden flex flex-col"
             :class="activeAudit.status === 'OPEN' ? 'border-red-500 shadow-red-900/50' : (activeAudit.status === 'GUILTY' ? 'border-ide-error' : 'border-ide-success')">
          
          <div class="px-4 py-3 flex justify-between items-center text-ide-text font-bold font-mono text-sm uppercase tracking-wider relative overflow-hidden"
               :class="activeAudit.status === 'OPEN' ? 'bg-red-600' : 'bg-ide-panel border-b border-ide-border'">
            
            <div v-if="activeAudit.status === 'OPEN'" class="absolute inset-0 bg-white/10 animate-pulse"></div>

            <div class="flex items-center gap-2 relative z-10">
                <ScaleIcon class="w-5 h-5" />
                <span v-if="activeAudit.status === 'OPEN'">EM JULGAMENTO</span>
                <span v-else>VEREDITO: {{ activeAudit.status === 'GUILTY' ? 'CULPADO' : 'INOCENTE' }}</span>
            </div>
            <div class="text-xs font-mono relative z-10 bg-black/20 px-2 py-0.5 rounded">{{ auditTimeLeft }}s</div>
          </div>

          <div class="p-5 bg-ide-bg/95 backdrop-blur-md">
            <div class="text-center mb-6">
                <p class="text-xs text-ide-dim font-mono mb-2">
                    <span class="text-red-400 font-bold">@{{ activeAudit.auditorName }}</span> questiona a verdade:
                </p>
                <div class="text-lg font-bold text-ide-text border border-ide-dim/30 rounded-lg p-3 bg-ide-panel shadow-inner">
                    "{{ activeAudit.slotPhrase || 'Carregando prova...' }}" 
                </div>
                <p class="text-xs text-ide-dim font-mono mt-2">
                    Réu: <span class="text-yellow-400 font-bold">@{{ activeAudit.accusedName }}</span>
                </p>
            </div>

            <div v-if="activeAudit.status === 'OPEN'" class="grid grid-cols-2 gap-4 mb-2">
                <button 
                    @click="castVote(true)"
                    :disabled="currentUser.id === activeAudit.auditorId || currentUser.id === activeAudit.accusedId"
                    class="group relative flex flex-col items-center justify-center p-3 rounded border border-green-500/30 bg-green-500/5 hover:bg-green-500/20 hover:border-green-500 transition-all disabled:opacity-30 disabled:cursor-not-allowed"
                >
                    <span class="text-2xl mb-1 group-hover:scale-110 transition-transform">👍</span>
                    <span class="text-[10px] font-bold text-green-400 uppercase tracking-widest">É Verdade</span>
                </button>

                <button 
                    @click="castVote(false)"
                    :disabled="currentUser.id === activeAudit.auditorId || currentUser.id === activeAudit.accusedId"
                    class="group relative flex flex-col items-center justify-center p-3 rounded border border-red-500/30 bg-red-500/5 hover:bg-red-500/20 hover:border-red-500 transition-all disabled:opacity-30 disabled:cursor-not-allowed"
                >
                    <span class="text-2xl mb-1 group-hover:scale-110 transition-transform">🤥</span>
                    <span class="text-[10px] font-bold text-red-400 uppercase tracking-widest">É Mentira</span>
                </button>
            </div>

            <div class="mt-4 space-y-2">
                <div class="flex justify-between text-[10px] font-mono text-ide-dim uppercase font-bold">
                    <span class="text-green-500">Verdade ({{ voteCounts.yes }})</span>
                    <span class="text-red-500">Mentira ({{ voteCounts.no }})</span>
                </div>
                <div class="h-3 bg-ide-panel rounded-full overflow-hidden flex border border-ide-border relative">
                    <div class="absolute left-1/2 top-0 bottom-0 w-px bg-white/20 z-10"></div>
                    
                    <div class="bg-green-500 transition-all duration-500 ease-out" :style="{ width: (voteCounts.total ? (voteCounts.yes / voteCounts.total * 100) : 50) + '%' }"></div>
                    <div class="bg-red-500 transition-all duration-500 ease-out flex-1"></div>
                </div>
                <div class="text-[10px] text-center text-ide-dim font-mono">
                    {{ voteCounts.total }} votos computados
                </div>
            </div>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>
<script setup>
import confetti from 'canvas-confetti'
import {
  ArrowRightOnRectangleIcon,
  ChartBarIcon,
  CodeBracketIcon,
  TagIcon,
  TrophyIcon,
  UserCircleIcon,
  ScaleIcon,
  ShieldCheckIcon,
  CommandLineIcon,
  SwatchIcon
} from '@heroicons/vue/24/outline'
import {useSound} from '@/composables/useSound';
import {version} from '../../package.json'
import { useSocket } from '@/composables/useSocket'

const { play } = useSound()
const api = useAPI()
const router = useRouter()
const config = useRuntimeConfig()
const { isConnected: socketConnected, connect, subscribe } = useSocket()

const currentUser = ref('')
const card = ref(null)
const leaderboard = ref([])
const lastWinner = ref(null)
const isLoading = ref(true)

const inspectedCard = ref(null)
const inspectedUser = ref('')
const loadingInspection = ref(false)

const activeAudit = ref(null)
const auditTimeLeft = ref(60)
const inspectedUserId = ref(null)
let auditTimerInterval = null

const showProfile = ref(false)
const isLoadingProfile = ref(false)

const appVersion = version
const availableThemes = [
  { id: 'dracula', name: 'Dark IDE', icon: '🌙' },
  { id: 'matrix', name: 'Matrix', icon: '💻' },
  { id: 'cyberpunk', name: 'Cyberpunk', icon: '🌆' },
  { id: 'corporate', name: 'Corporate (Light)', icon: '👔' },
  { id: 'barbie', name: 'Barbie Dev', icon: '💅' },
  { id: 'aura', name: 'Aura Dark', icon: '✨' },
  { id: 'nord', name: 'Nord Arctic', icon: '❄️' }
]

const currentTheme = ref('matrix')
const showThemeMenu = ref(false)

const setupSocketListeners = () => {
  subscribe('/topic/progress', (tick) => {
    const update = JSON.parse(tick.body)
    updateLeaderboardLocal(update)
  })

  subscribe('/topic/winners', (tick) => {
    handleWinner(JSON.parse(tick.body))
  })
  
  subscribe('/topic/audit/start', (msg) => handleAuditStart(JSON.parse(msg.body)))
  
  subscribe('/topic/audit/update', (msg) => {
      if (activeAudit.value && activeAudit.value.id === JSON.parse(msg.body).id) {
          activeAudit.value = JSON.parse(msg.body)
      }
  })
  
  subscribe('/topic/audit/end', (msg) => handleAuditEnd(JSON.parse(msg.body)))
}

const updateLeaderboardLocal = (update) => {
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
  showProfile.value = true
  isLoadingProfile.value = true

  try {
    await syncCurrentUser()
  } catch (error) {
    // O erro já foi logado na função principal, adicionar um alert() caso quiera
  } finally {
    isLoadingProfile.value = false
  }
}

const inspectPlayer = async (player) => {
    if (player.username === currentUser.value.username) return;

    inspectedUser.value = player.username
    inspectedUserId.value = player.id
    inspectedCard.value = { slots: [] }
    loadingInspection.value = true
    
    try {
      const cardData = await api(`/game/card/${player.username}`)
      inspectedCard.value = cardData
      if (cardData.userId) inspectedUserId.value = cardData.userId
    } catch (e) {
        console.error("Erro ao inspecionar", e)
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

const handleAuditStart = (session) => {
    activeAudit.value = session
    // play('notification')

    const startTime = new Date(session.startTime).getTime()
    const now = new Date().getTime()
    const elapsedSeconds = Math.floor((now - startTime) / 1000)
    const remainingTime = 60 - elapsedSeconds

    if (remainingTime <= 0) {
        activeAudit.value = null
        return
    }

    auditTimeLeft.value = remainingTime

    if (auditTimerInterval) clearInterval(auditTimerInterval)
    auditTimerInterval = setInterval(() => {
        auditTimeLeft.value--
        if (auditTimeLeft.value <= 0) {
            clearInterval(auditTimerInterval)
        }
    }, 1000)
}

const checkActiveAudit = async () => {
    try {
        const session = await api('/game/audit/current')
        
        if (session && session.status === 'OPEN') {
            handleAuditStart(session)
        }
    } catch (e) {
        console.log("Nenhuma auditoria ativa no momento.")
    }
}

const handleAuditEnd = (session) => {
  activeAudit.value = session
  clearInterval(auditTimerInterval)
  setTimeout(() => { activeAudit.value = null }, 5000)

  if (session.accusedId === currentUser.value.id) loadData()
}

const initiateAudit = async (slot) => {
  if (!confirm(`TRIBUNAL DO BINGO:\n\nVocê afirma que "${slot.phrase}" é MENTIRA?\n\nSe a maioria votar que aconteceu, você perde 100 XP!`)) return;

  try {
    await api('/game/audit/initiate', {
      method: 'POST',
      body: {
        accusedId: inspectedUserId.value,
        slotPosition: slot.position
      }
    })
    closeInspection()
  } catch (err) {
    alert(err.message || "Erro ao iniciar auditoria")
  }
}

const castVote = async (vote) => {
  try {
    await api('/game/audit/vote', {
      method: 'POST',
      body: { auditId: activeAudit.value.id, vote: vote }
    })
  } catch (err) {
    console.error(err)
  }
}

const voteCounts = computed(() => {
  if (!activeAudit.value) return { yes: 0, no: 0, total: 0 }
  const votes = Object.values(activeAudit.value.votes || {})
  const yes = votes.filter(v => v === true).length
  const no = votes.filter(v => v === false).length
  return { yes, no, total: votes.length }
})

const changeTheme = async (themeId) => {
  currentTheme.value = themeId
  showThemeMenu.value = false

  document.documentElement.setAttribute('data-theme', themeId)
  localStorage.setItem('bingo_theme', themeId)

  try {
    await api('/auth/theme', {
      method: 'PATCH',
      body: { theme: themeId }
    })

    if (currentUser.value) {
        currentUser.value.preferredTheme = themeId
        localStorage.setItem('bingo_user', JSON.stringify(currentUser.value))
    }
  } catch (err) {
    console.error("Erro ao salvar tema no servidor", err)
  }
}

const syncCurrentUser = async () => {
  try {
    const freshUser = await api('/auth/me')
    
    currentUser.value = freshUser
    localStorage.setItem('bingo_user', JSON.stringify(freshUser))
    
    return freshUser
  } catch (error) {
    console.error("Falha ao sincronizar dados do usuário:", error)
    throw error
  }
}

watch(currentTheme, (newTheme) => {
  document.documentElement.setAttribute('data-theme', newTheme)
  localStorage.setItem('bingo_theme', newTheme)
})

onMounted(async () => {
  const savedTheme = localStorage.getItem('bingo_theme')
  if (savedTheme) {
    currentTheme.value = savedTheme
  }
  currentUser.value = JSON.parse(localStorage.getItem('bingo_user')) || 'Dev'
  
  loadData()
  checkActiveAudit()

  connect(currentUser.value.id)

  watch(socketConnected, (connected) => {
    if (connected) {
      setupSocketListeners()
    }
  }, { immediate: true })

  try {
    const freshUser = await syncCurrentUser()
    const realTheme = freshUser.preferredTheme || 'dracula'
    if (realTheme !== currentTheme.value) {
        currentTheme.value = realTheme
    }
  } catch (error) {
    console.log("Não foi possível validar o tema em background.")
  }
})

const adminMessage = ref(null)

  subscribe('/topic/broadcast', (msg) => {
      const data = JSON.parse(msg.body)
      
      if (data.type === 'RELOAD_CARDS') {
          loadData() 
      }
      
      adminMessage.value = data.message
      play('notification')
      
      setTimeout(() => { adminMessage.value = null }, 8000)
  })
</script>