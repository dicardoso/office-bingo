<template>
  <div class="min-h-screen bg-ide-bg p-4 md:p-8">
    <div class="max-w-5xl mx-auto">
      
      <header class="flex justify-between items-center mb-6 border-b border-ide-border pb-4">
        <div>
          <h1 class="text-2xl font-bold font-mono text-ide-accent">~/admin_panel.sh</h1>
          <p class="text-xs text-ide-dim font-mono mt-1">SuperUser Control Interface</p>
        </div>
        <button @click="$router.push('/')" class="text-sm font-mono text-ide-dim hover:text-white transition-colors border border-ide-border px-3 py-1.5 rounded hover:bg-ide-panel">
          [ VOLTAR AO JOGO ]
        </button>
      </header>

      <div class="flex gap-1 mb-6 border-b border-ide-border">
        <button 
          @click="activeTab = TabEnum.PHRASES" 
          class="px-4 py-2 text-sm font-mono transition-colors border-b-2"
          :class="activeTab === TabEnum.PHRASES ? 'border-ide-accent text-ide-accent bg-ide-panel' : 'border-transparent text-ide-dim hover:text-white hover:bg-ide-panel/50'"
        >
          📄 phrases.json
        </button>
        <button 
          @click="activeTab = 'users'" 
          class="px-4 py-2 text-sm font-mono transition-colors border-b-2"
          :class="activeTab === 'users' ? 'border-ide-accent text-ide-accent bg-ide-panel' : 'border-transparent text-ide-dim hover:text-white hover:bg-ide-panel/50'"
        >
          👥 users.config
        </button>
        <button 
          @click="activeTab = TabEnum.GAME" 
          class="px-4 py-2 text-sm font-mono transition-colors border-b-2"
          :class="activeTab === TabEnum.GAME ? 'border-ide-accent text-ide-accent bg-ide-panel' : 'border-transparent text-ide-dim hover:text-white hover:bg-ide-panel/50'"
        >
          🎮 game_master.sh
        </button>
      </div>

      <div v-if="activeTab === TabEnum.PHRASES" class="bg-ide-panel rounded-lg border border-ide-border shadow-2xl overflow-hidden animate-fade-in">
        <div class="bg-ide-bg px-4 py-3 border-b border-ide-border flex justify-between items-center">
          <div class="flex items-center gap-4">
            <span class="text-xs font-mono text-ide-dim uppercase tracking-widest">Total: {{ phrases.length }}</span>
            <span class="text-xs font-mono text-ide-success uppercase tracking-widest">Ativas: {{ activeCount }}</span>
          </div>
          <button @click="openPhraseModal()" class="bg-ide-success/10 text-ide-success border border-ide-success/50 hover:bg-ide-success hover:text-white px-3 py-1.5 rounded text-xs font-mono font-bold transition-all">
            + NOVA FRASE
          </button>
        </div>

        <div class="overflow-x-auto">
          <table class="w-full text-left text-sm text-ide-text font-mono">
            <thead class="text-xs text-ide-dim bg-ide-bg border-b border-ide-border uppercase">
              <tr>
                <th scope="col" class="px-6 py-3">Status</th>
                <th scope="col" class="px-6 py-3">Frase (Texto)</th>
                <th scope="col" class="px-6 py-3 text-right">Ações</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="isLoadingPhrases" class="border-b border-ide-border/50">
                <td colspan="3" class="px-6 py-8 text-center text-ide-dim animate-pulse">Carregando dicionário...</td>
              </tr>
              <tr v-else v-for="phrase in phrases" :key="phrase.id" class="border-b border-ide-border/50 hover:bg-ide-bg/30 transition-colors" :class="!phrase.active ? 'opacity-50' : ''">
                <td class="px-6 py-4">
                  <button @click="togglePhraseStatus(phrase)" class="px-2 py-1 rounded text-[10px] font-bold tracking-wider transition-colors" :class="phrase.active ? 'bg-green-500/20 text-green-400 border border-green-500/30' : 'bg-red-500/20 text-red-400 border border-red-500/30'">
                    {{ phrase.active ? 'ATIVA' : 'INATIVA' }}
                  </button>
                </td>
                <td class="px-6 py-4 font-bold" :class="!phrase.active ? 'line-through decoration-ide-error' : ''">
                  {{ phrase.text }}
                </td>
                <td class="px-6 py-4 text-right space-x-3">
                  <button @click="openPhraseModal(phrase)" class="text-ide-accent hover:text-white transition-colors">Editar</button>
                  <button @click="deletePhrase(phrase.id)" class="text-ide-error hover:text-red-400 transition-colors">Excluir</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div v-if="activeTab === TabEnum.USERS" class="bg-ide-panel rounded-lg border border-ide-border shadow-2xl overflow-hidden animate-fade-in">
        <div class="bg-ide-bg px-4 py-3 border-b border-ide-border flex justify-between items-center">
          <div class="flex items-center gap-4">
            <span class="text-xs font-mono text-ide-dim uppercase tracking-widest">Desenvolvedores: {{ users.length }}</span>
          </div>
          <button @click="fetchUsers" class="text-ide-dim hover:text-white transition-colors" title="Atualizar Lista">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path></svg>
          </button>
        </div>

        <div class="overflow-x-auto">
          <table class="w-full text-left text-sm text-ide-text font-mono">
            <thead class="text-xs text-ide-dim bg-ide-bg border-b border-ide-border uppercase">
              <tr>
                <th scope="col" class="px-6 py-3">Developer</th>
                <th scope="col" class="px-6 py-3">Progressão</th>
                <th scope="col" class="px-6 py-3">Status</th>
                <th scope="col" class="px-6 py-3 text-right">Ações</th>
              </tr>
            </thead>
            <tbody>
              <tr v-if="isLoadingUsers" class="border-b border-ide-border/50">
                <td colspan="4" class="px-6 py-8 text-center text-ide-dim animate-pulse">Consultando banco de dados...</td>
              </tr>
              <tr v-else v-for="user in users" :key="user.id" class="border-b border-ide-border/50 hover:bg-ide-bg/30 transition-colors">
                <td class="px-6 py-4 flex items-center gap-2">
                  <div class="relative flex items-center justify-center w-3 h-3" :title="onlineUsers.includes(user.id) ? 'Online' : 'Offline'">
                    <span v-if="onlineUsers.includes(user.id)" class="absolute inline-flex w-full h-full bg-green-400 rounded-full opacity-75 animate-ping"></span>
                    <span class="relative inline-flex w-2 h-2 rounded-full transition-colors duration-300" :class="onlineUsers.includes(user.id) ? 'bg-green-500 shadow-[0_0_8px_rgba(34,197,94,0.8)]' : 'bg-ide-dim/30'"></span>
                  </div>

                  <span class="font-bold text-white">{{ user.username }}</span>
                  <span v-if="user.role === UserRole.ADMIN" class="px-1.5 py-0.5 rounded bg-ide-accent/20 text-ide-accent text-[9px] font-bold border border-ide-accent/30">ADMIN</span>
                </td>
                <td class="px-6 py-4">
                  <div class="text-xs text-ide-dim">{{ user.position }}</div>
                  <div class="font-bold text-ide-accent">{{ user.careerXp || 0 }} XP</div>
                </td>
                <td class="px-6 py-4">
                  <span v-if="user.suspended" class="text-xs font-bold text-red-400 bg-red-500/10 px-2 py-1 rounded border border-red-500/20">SUSPENSO</span>
                  <span v-else-if="user.preferredTheme === ThemeEnum.TROLL" class="text-xs font-bold text-[#ff00ff] bg-[#ff00ff]/10 px-2 py-1 rounded border border-[#ff00ff]/20">TROLLADO</span>
                  <span v-else class="text-xs font-bold text-green-400 bg-green-500/10 px-2 py-1 rounded border border-green-500/20">ATIVO</span>
                </td>
                <td class="px-6 py-4 text-right">
                  <button @click="openUserModal(user)" class="bg-ide-bg border border-ide-border hover:border-ide-accent text-ide-dim hover:text-white px-3 py-1.5 rounded text-xs transition-colors">
                    GERIR
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <div v-if="activeTab === TabEnum.GAME" class="space-y-6 animate-fade-in">
        
        <div class="bg-ide-panel rounded-lg border border-ide-border shadow-2xl overflow-hidden p-6">
          <h3 class="text-lg font-bold text-ide-text flex items-center gap-2 mb-2">
            📢 Megafone Global
          </h3>
          <p class="text-xs text-ide-dim mb-4">Envie uma mensagem pop-up na tela de todos os usuários logados instantaneamente.</p>
          
          <div class="flex gap-2">
            <input 
              v-model="broadcastText" 
              @keyup.enter="sendBroadcast"
              type="text" 
              class="flex-1 bg-ide-bg border border-ide-border rounded p-2 text-sm text-white focus:border-ide-accent outline-none font-mono"
              placeholder="Ex: Reunião geral na copa em 5 minutos! Quem perder leva bingo!"
            >
            <button 
              @click="sendBroadcast"
              :disabled="!broadcastText"
              class="bg-ide-accent text-ide-bg px-6 py-2 rounded font-bold font-mono text-sm disabled:opacity-50 transition-colors hover:bg-white"
            >
              ENVIAR ALERT
            </button>
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div class="bg-ide-panel rounded-lg border border-ide-border shadow-2xl p-6 border-l-4 border-l-yellow-500">
            <h3 class="text-lg font-bold text-yellow-500 mb-2">Re-Sortear Cartelas</h3>
            <p class="text-xs text-ide-dim mb-6">Deleta as cartelas de hoje de todos os usuários. O sistema irá gerar cartelas novas automaticamente nos bastidores usando as frases ativas mais recentes.</p>
            <button @click="forceNewCards" class="w-full bg-yellow-500/10 text-yellow-500 border border-yellow-500/50 hover:bg-yellow-500 hover:text-white px-4 py-3 rounded font-bold font-mono text-sm transition-all">
              EMBARALHAR AGORA
            </button>
          </div>

          <div class="bg-ide-panel rounded-lg border border-red-900/50 shadow-2xl p-6 border-l-4 border-l-red-500">
            <h3 class="text-lg font-bold text-red-500 mb-2">Fim de Temporada</h3>
            <p class="text-xs text-ide-dim mb-6">Zera o <b>Season XP</b> de TODOS os usuários cadastrados (o Career XP é mantido). Esta ação é irreversível e marca o início de um novo mês de jogo.</p>
            <button @click="resetSeason" class="w-full bg-red-500/10 text-red-400 border border-red-500/50 hover:bg-red-500 hover:text-white px-4 py-3 rounded font-bold font-mono text-sm transition-all">
              ZERAR TEMPORADA
            </button>
          </div>
        </div>

      </div>

    </div>

    <transition enter-active-class="transition duration-200 ease-out" enter-from-class="opacity-0 scale-95" leave-active-class="transition duration-150 ease-in" leave-to-class="opacity-0 scale-95">
      <div v-if="showPhraseModal" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm" @click.self="closePhraseModal">
        <div class="bg-ide-panel w-full max-w-md rounded-lg border border-ide-border shadow-2xl overflow-hidden">
          <div class="bg-ide-bg px-4 py-3 border-b border-ide-border">
            <h3 class="font-mono font-bold text-white text-sm">{{ editingPhraseId ? 'EDITAR_FRASE' : 'NOVA_FRASE' }}</h3>
          </div>
          <div class="p-6 space-y-4">
            <div>
              <label class="block text-xs font-mono text-ide-dim mb-1">Texto da Frase</label>
              <textarea v-model="phraseForm.text" rows="3" class="w-full bg-ide-bg border border-ide-border rounded p-2 text-sm text-white font-sans focus:outline-none focus:border-ide-accent transition-colors" placeholder="Ex: Produção caiu..."></textarea>
            </div>
            <div class="flex items-center gap-2">
              <input type="checkbox" id="activePhrase" v-model="phraseForm.active" class="w-4 h-4 rounded border-ide-border text-ide-accent bg-ide-bg">
              <label for="activePhrase" class="text-sm font-mono text-ide-text cursor-pointer">Frase Ativa (Pode cair no bingo)</label>
            </div>
          </div>
          <div class="bg-ide-bg px-4 py-3 border-t border-ide-border flex justify-end gap-3">
            <button @click="closePhraseModal" class="px-4 py-2 text-xs font-mono text-ide-dim hover:text-white transition-colors">CANCELAR</button>
            <button @click="savePhrase" class="px-4 py-2 text-xs font-mono font-bold bg-ide-accent text-ide-bg rounded hover:bg-white transition-colors" :disabled="!phraseForm.text">SALVAR</button>
          </div>
        </div>
      </div>
    </transition>

    <transition enter-active-class="transition duration-200 ease-out" enter-from-class="opacity-0 scale-95" leave-active-class="transition duration-150 ease-in" leave-to-class="opacity-0 scale-95">
      <div v-if="showUserModal && selectedUser" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm" @click.self="closeUserModal">
        <div class="bg-ide-panel w-full max-w-lg rounded-lg border border-ide-border shadow-2xl overflow-hidden font-mono">
          
          <div class="bg-ide-bg px-4 py-3 border-b border-ide-border flex justify-between items-center">
            <h3 class="font-bold text-white text-sm">Gerindo: <span class="text-ide-accent">{{ selectedUser.username }}</span></h3>
            <button @click="closeUserModal" class="text-ide-dim hover:text-white">&times;</button>
          </div>

          <div class="p-6 space-y-6">
            
            <div class="space-y-3">
              <h4 class="text-xs text-ide-dim uppercase tracking-wider border-b border-ide-border pb-1">📊 Progresso e XP</h4>
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="block text-xs text-ide-text mb-1">Career XP (Vitalício)</label>
                  <input v-model="userForm.careerXp" type="number" class="w-full bg-ide-bg border border-ide-border rounded p-2 text-sm text-white focus:border-ide-accent outline-none">
                </div>
                <div>
                  <label class="block text-xs text-ide-text mb-1">Season XP (Mensal)</label>
                  <input v-model="userForm.seasonXp" type="number" class="w-full bg-ide-bg border border-ide-border rounded p-2 text-sm text-white focus:border-ide-accent outline-none">
                </div>
              </div>
              <button @click="saveUserXp" class="w-full bg-ide-bg border border-ide-border hover:border-ide-accent text-ide-accent px-4 py-2 rounded text-xs font-bold transition-colors">
                ATUALIZAR XP
              </button>
            </div>

            <div class="space-y-3">
              <h4 class="text-xs text-ide-dim uppercase tracking-wider border-b border-ide-border pb-1">🛡️ Acesso e Segurança</h4>
              <div class="flex items-center justify-between bg-ide-bg p-3 rounded border border-ide-border">
                <div>
                  <div class="text-sm text-white font-bold">Privilégios de Admin</div>
                  <div class="text-xs text-ide-dim">Acesso total a este painel</div>
                </div>
                <button 
                  @click="toggleUserRole" 
                  :disabled="isSelf(selectedUser.id)"
                  class="px-3 py-1.5 rounded text-xs font-bold transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                  :class="selectedUser.role === UserRole.ADMIN ? 'bg-ide-accent text-ide-bg' : 'bg-ide-panel border border-ide-border text-ide-dim hover:text-white'"
                >
                  {{ selectedUser.role === UserRole.ADMIN ? 'REMOVER ADMIN' : 'PROMOVER A ADMIN' }}
                </button>
              </div>
            </div>

            <div class="space-y-3">
              <h4 class="text-xs text-red-400 uppercase tracking-wider border-b border-red-900/50 pb-1">☢️ Danger Zone (Punições)</h4>
              
              <div class="flex items-center justify-between bg-red-950/20 p-3 rounded border border-red-900/50">
                <div>
                  <div class="text-sm text-red-400 font-bold">Shadowban (Suspender Conta)</div>
                  <div class="text-xs text-ide-dim">Impede de gerar cartelas e abrir auditorias</div>
                </div>
                <button 
                  @click="toggleUserSuspension"
                  :disabled="isSelf(selectedUser.id)"
                  class="px-3 py-1.5 rounded text-xs font-bold transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                  :class="selectedUser.suspended ? 'bg-red-500 text-white' : 'bg-ide-bg border border-red-900/50 text-red-400 hover:bg-red-900/30'"
                >
                  {{ selectedUser.suspended ? 'REMOVER SUSPENSÃO' : 'SUSPENDER' }}
                </button>
              </div>

              <div class="flex items-center justify-between bg-[#ff00ff]/10 p-3 rounded border border-[#ff00ff]/30">
                <div>
                  <div class="text-sm text-[#ff00ff] font-bold">Tema Troll (Castigo)</div>
                  <div class="text-xs text-ide-dim">Força paleta de cores rosa/neon e Comic Sans</div>
                </div>
                <button 
                  @click="toggleTrollTheme"
                  :disabled="isSelf(selectedUser.id)"
                  class="px-3 py-1.5 rounded text-xs font-bold transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                  :class="selectedUser.preferredTheme === ThemeEnum.TROLL ? 'bg-[#ff00ff] text-white hover:bg-[#cc00cc]' : 'bg-[#ff00ff]/20 text-[#ff00ff] border border-[#ff00ff]/50 hover:bg-[#ff00ff] hover:text-white'"
                >
                  {{ selectedUser.preferredTheme === ThemeEnum.TROLL ? 'REMOVER CASTIGO' : 'ATIVAR MODO TROLL' }}
                </button>
              </div>

            </div>

          </div>
        </div>
      </div>
    </transition>

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useSocket } from '@/composables/useSocket'

const api = useAPI() 
const { isConnected, connect, subscribe } = useSocket()

const activeTab = ref('phrases')
const loggedInUser = ref<{ id: number } | null>(null)

const phrases = ref<Phrase[]>([])
const isLoadingPhrases = ref(true)
const showPhraseModal = ref(false)
const editingPhraseId = ref<number | null>(null)
const phraseForm = ref({ text: '', active: true })

const onlineUsers = ref<string[]>([])
const activeCount = computed(() => phrases.value.filter(p => p.active).length)

const fetchPhrases = async () => {
  isLoadingPhrases.value = true
  try {
    phrases.value = await api<Phrase[]>('/admin/phrases')
  } catch (err) {
    console.error("Erro ao buscar frases", err)
  } finally {
    isLoadingPhrases.value = false
  }
}

const openPhraseModal = (phrase: Phrase | null = null) => {
  if (phrase) {
    editingPhraseId.value = Number(phrase.id)
    phraseForm.value = { text: phrase.text, active: phrase.active }
  } else {
    editingPhraseId.value = null
    phraseForm.value = { text: '', active: true }
  }
  showPhraseModal.value = true
}

const closePhraseModal = () => {
  showPhraseModal.value = false
  phraseForm.value = { text: '', active: true }
  editingPhraseId.value = null
}

const savePhrase = async () => {
  try {
    if (editingPhraseId.value) {
      await api(`/admin/phrases/${editingPhraseId.value}`, { method: 'PUT', body: phraseForm.value })
    } else {
      await api('/admin/phrases', { method: 'POST', body: phraseForm.value })
    }
    closePhraseModal()
    fetchPhrases()
  } catch (err) {
    alert("Erro ao salvar a frase.")
  }
}

const togglePhraseStatus = async (phrase: Phrase) => {
  try {
    await api(`/admin/phrases/${phrase.id}`, { method: 'PUT', body: { text: phrase.text, active: !phrase.active } })
    phrase.active = !phrase.active
  } catch (err) {
    alert("Erro ao alterar status.")
  }
}

const deletePhrase = async (id: number) => {
  if (!confirm("Tem certeza que deseja excluir esta frase permanentemente?")) return;
  try {
    await api(`/admin/phrases/${id}`, { method: 'DELETE' })
    fetchPhrases()
  } catch (err) {
    alert("Erro ao excluir.")
  }
}

const users = ref<User[]>([])
const isLoadingUsers = ref(true)
const showUserModal = ref(false)
const selectedUser = ref<User | null>(null)
const userForm = ref({ careerXp: 0, seasonXp: 0 })

const isSelf = (id: string) => loggedInUser.value?.id === Number(id)

const fetchUsers = async () => {
  isLoadingUsers.value = true
  try {
    users.value = await api<User[]>('/admin/users')
    onlineUsers.value = await api('/admin/users/online');
  } catch (err) {
    console.error("Erro ao buscar usuários", err)
  } finally {
    isLoadingUsers.value = false
  }
}

const openUserModal = (user: User) => {
  selectedUser.value = user
  userForm.value = { 
    careerXp: user.careerXp || 0, 
    seasonXp: user.seasonXp || 0 
  }
  showUserModal.value = true
}

const closeUserModal = () => {
  showUserModal.value = false
  selectedUser.value = null
}

const saveUserXp = async () => {
  try {
    if (!selectedUser.value) {
      alert("Nenhum usuário selecionado.");
      return;
    }
    const updated = await api<User>(`/admin/users/${selectedUser.value.id}/xp`, {
      method: 'PATCH',
      body: userForm.value
    })
    selectedUser.value.careerXp = updated.careerXp
    selectedUser.value.seasonXp = updated.seasonXp
    
    const index = users.value.findIndex(u => u.id === updated.id)
    if(index !== -1) users.value[index] = updated
    
    alert("XP atualizado com sucesso!")
  } catch (err) {
    alert("Erro ao atualizar XP.")
  }
}

const toggleUserRole = async () => {
  if (!selectedUser.value || isSelf(selectedUser.value.id)) return;
  
  const newRole = selectedUser.value.role === UserRole.ADMIN ? UserRole.USER : UserRole.ADMIN
  try {
    const updated = await api<User>(`/admin/users/${selectedUser.value.id}/role`, {
      method: 'PATCH',
      body: { role: newRole }
    })
    selectedUser.value.role = updated.role
    const index = users.value.findIndex(u => u.id === updated.id)
    if(index !== -1) users.value[index] = updated
  } catch (err) {
    alert("Erro ao alterar cargo.")
  }
}

const toggleUserSuspension = async () => {
  if (!selectedUser.value || isSelf(selectedUser.value.id)) return;

  const newStatus = !selectedUser.value.suspended
  try {
    const updated = await api<User>(`/admin/users/${selectedUser.value.id}/suspend`, {
      method: 'PATCH',
      body: { suspended: newStatus }
    })
    selectedUser.value.suspended = updated.suspended
    const index = users.value.findIndex(u => u.id === updated.id)
    if(index !== -1) users.value[index] = updated
  } catch (err) {
    alert("Erro ao aplicar suspensão.")
  }
}

const toggleTrollTheme = async () => {
  if (!selectedUser.value || isSelf(selectedUser.value.id)) return;

  const isTrolled = selectedUser.value.preferredTheme === ThemeEnum.TROLL;
  
  if (!isTrolled) {
    if (!confirm("Tem certeza? A tela deste usuário vai virar um pesadelo neon na próxima atualização!")) return;
  }

  try {
    let updated: User;
    if (isTrolled) {
      updated = await api(`/admin/users/${selectedUser.value.id}/troll`, { method: 'DELETE' })
    } else {
      updated = await api(`/admin/users/${selectedUser.value.id}/troll`, { method: 'POST' })
    }
    
    selectedUser.value.preferredTheme = updated.preferredTheme
    const index = users.value.findIndex(u => u.id === updated.id)
    if(index !== -1) users.value[index] = updated
    
  } catch (err) {
    alert("Erro ao alterar o tema Troll.")
  }
}

onMounted(() => {
  const user = localStorage.getItem('bingo_user');
  loggedInUser.value = user ? JSON.parse(user) : null;
  const savedTheme = localStorage.getItem('bingo_theme')
  if (savedTheme) document.documentElement.setAttribute('data-theme', savedTheme)
  
  if (loggedInUser.value) {
      connect(loggedInUser.value.id)
  }

  watch(isConnected, (connected) => {
    if (connected) {
      subscribe('/topic/presence', (msg: { body: string }) => {
        onlineUsers.value = JSON.parse(msg.body)
      })
    }
  }, { immediate: true })

  fetchPhrases()
  fetchUsers()
})

// ==========================================
// LÓGICA DO GAME MASTER
// ==========================================
const broadcastText = ref('')

const sendBroadcast = async () => {
  const message = broadcastText.value.trim()
  if (!message) return
  try {
    await api('/admin/game/broadcast', { method: 'POST', body: { message } })
    broadcastText.value = ''
  } catch (err) {
    alert("Erro ao enviar mensagem.")
  }
}

const forceNewCards = async () => {
  if (!confirm("TEM CERTEZA?\n\nIsso apagará todas as marcações feitas hoje por todos os usuários e fará o navegador deles piscar com uma nova cartela!")) return
  try {
    await api('/admin/game/force-cards', { method: 'POST' })
    alert("Cartelas resetadas! Os clientes serão atualizados em tempo real.")
  } catch (err) {
    alert("Erro ao resetar cartelas.")
  }
}

const resetSeason = async () => {
  if (!confirm("⚠️ ALERTA VERMELHO ⚠️\n\nTem certeza absoluta que deseja ZERAR o XP da temporada de todo mundo? Esta ação não pode ser desfeita!")) return
  
  const password = prompt("Para confirmar, digite: CONFIRMAR")
  if (password !== 'CONFIRMAR') {
    alert("Ação cancelada.")
    return
  }

  try {
    await api('/admin/game/reset-season', { method: 'POST' })
    fetchUsers()
    alert("Temporada resetada com sucesso! Um aviso foi enviado a todos.")
  } catch (err) {
    alert("Erro ao resetar temporada.")
  }
}
</script>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.2s ease-out;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(5px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>