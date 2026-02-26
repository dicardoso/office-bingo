<template>
    <div class="min-h-screen bg-ide-bg p-4 md:p-8">
      <div class="max-w-4xl mx-auto">
        
        <header class="flex justify-between items-center mb-8 border-b border-ide-border pb-4">
          <div>
            <h1 class="text-2xl font-bold font-mono text-ide-accent">~/admin/phrases.sh</h1>
            <p class="text-xs text-ide-dim font-mono mt-1">Gestão do Dicionário do Bingo</p>
          </div>
          <button @click="$router.push('/')" class="text-sm font-mono text-ide-dim hover:text-white transition-colors border border-ide-border px-3 py-1.5 rounded hover:bg-ide-panel">
            [ VOLTAR AO JOGO ]
          </button>
        </header>
  
        <div class="bg-ide-panel rounded-lg border border-ide-border shadow-2xl overflow-hidden">
          
          <div class="bg-ide-bg px-4 py-3 border-b border-ide-border flex justify-between items-center">
            <div class="flex items-center gap-4">
              <span class="text-xs font-mono text-ide-dim uppercase tracking-widest">Total: {{ phrases.length }}</span>
              <span class="text-xs font-mono text-ide-success uppercase tracking-widest">Ativas: {{ activeCount }}</span>
            </div>
            <button @click="openModal()" class="bg-ide-success/10 text-ide-success border border-ide-success/50 hover:bg-ide-success hover:text-white px-3 py-1.5 rounded text-xs font-mono font-bold transition-all">
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
                <tr v-if="isLoading" class="border-b border-ide-border/50">
                  <td colspan="3" class="px-6 py-8 text-center text-ide-dim animate-pulse">Carregando dicionário...</td>
                </tr>
                <tr v-else v-for="phrase in phrases" :key="phrase.id" class="border-b border-ide-border/50 hover:bg-ide-bg/30 transition-colors" :class="!phrase.active ? 'opacity-50' : ''">
                  <td class="px-6 py-4">
                    <button @click="toggleStatus(phrase)" class="px-2 py-1 rounded text-[10px] font-bold tracking-wider transition-colors" :class="phrase.active ? 'bg-green-500/20 text-green-400 border border-green-500/30' : 'bg-red-500/20 text-red-400 border border-red-500/30'">
                      {{ phrase.active ? 'ATIVA' : 'INATIVA' }}
                    </button>
                  </td>
                  <td class="px-6 py-4 font-bold" :class="!phrase.active ? 'line-through decoration-ide-error' : ''">
                    {{ phrase.text }}
                  </td>
                  <td class="px-6 py-4 text-right space-x-3">
                    <button @click="openModal(phrase)" class="text-ide-accent hover:text-white transition-colors">Editar</button>
                    <button @click="deletePhrase(phrase.id)" class="text-ide-error hover:text-red-400 transition-colors">Excluir</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
  
      </div>
  
      <transition enter-active-class="transition duration-200 ease-out" enter-from-class="opacity-0 scale-95" leave-active-class="transition duration-150 ease-in" leave-to-class="opacity-0 scale-95">
        <div v-if="showModal" class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/80 backdrop-blur-sm" @click.self="closeModal">
          <div class="bg-ide-panel w-full max-w-md rounded-lg border border-ide-border shadow-2xl overflow-hidden">
            <div class="bg-ide-bg px-4 py-3 border-b border-ide-border">
              <h3 class="font-mono font-bold text-white text-sm">
                {{ editingId ? 'EDITAR_FRASE' : 'NOVA_FRASE' }}
              </h3>
            </div>
            <div class="p-6 space-y-4">
              <div>
                <label class="block text-xs font-mono text-ide-dim mb-1">Texto da Frase</label>
                <textarea v-model="form.text" rows="3" class="w-full bg-ide-bg border border-ide-border rounded p-2 text-sm text-white font-sans focus:outline-none focus:border-ide-accent transition-colors" placeholder="Ex: Produção caiu..."></textarea>
              </div>
              <div class="flex items-center gap-2">
                <input type="checkbox" id="active" v-model="form.active" class="w-4 h-4 rounded border-ide-border text-ide-accent bg-ide-bg">
                <label for="active" class="text-sm font-mono text-ide-text cursor-pointer">Frase Ativa (Pode cair no bingo)</label>
              </div>
            </div>
            <div class="bg-ide-bg px-4 py-3 border-t border-ide-border flex justify-end gap-3">
              <button @click="closeModal" class="px-4 py-2 text-xs font-mono text-ide-dim hover:text-white transition-colors">
                CANCELAR
              </button>
              <button @click="savePhrase" class="px-4 py-2 text-xs font-mono font-bold bg-ide-accent text-ide-bg rounded hover:bg-white transition-colors" :disabled="!form.text">
                SALVAR_ALTERAÇÕES
              </button>
            </div>
          </div>
        </div>
      </transition>
  
    </div>
  </template>
  
  <script setup>
  import { ref, computed, onMounted } from 'vue'
  
  const api = useAPI() // Reaproveitando o seu composable existente
  const phrases = ref([])
  const isLoading = ref(true)
  
  // Estado do Modal
  const showModal = ref(false)
  const editingId = ref(null)
  const form = ref({ text: '', active: true })
  
  const activeCount = computed(() => phrases.value.filter(p => p.active).length)
  
  const fetchPhrases = async () => {
    isLoading.value = true
    try {
      phrases.value = await api('/admin/phrases')
    } catch (err) {
      console.error("Erro ao buscar frases", err)
    } finally {
      isLoading.value = false
    }
  }
  
  const openModal = (phrase = null) => {
    if (phrase) {
      editingId.value = phrase.id
      form.value = { text: phrase.text, active: phrase.active }
    } else {
      editingId.value = null
      form.value = { text: '', active: true }
    }
    showModal.value = true
  }
  
  const closeModal = () => {
    showModal.value = false
    form.value = { text: '', active: true }
    editingId.value = null
  }
  
  const savePhrase = async () => {
    try {
      if (editingId.value) {
        await api(`/admin/phrases/${editingId.value}`, {
          method: 'PUT',
          body: form.value
        })
      } else {
        await api('/admin/phrases', {
          method: 'POST',
          body: form.value
        })
      }
      closeModal()
      fetchPhrases() // Recarrega a lista
    } catch (err) {
      alert("Erro ao salvar a frase.")
    }
  }
  
  const toggleStatus = async (phrase) => {
    try {
      await api(`/admin/phrases/${phrase.id}`, {
        method: 'PUT',
        body: { text: phrase.text, active: !phrase.active }
      })
      phrase.active = !phrase.active
    } catch (err) {
      alert("Erro ao alterar status.")
    }
  }
  
  const deletePhrase = async (id) => {
    if (!confirm("Tem certeza que deseja excluir esta frase permanentemente?")) return;
    
    try {
      await api(`/admin/phrases/${id}`, { method: 'DELETE' })
      fetchPhrases()
    } catch (err) {
      alert("Erro ao excluir.")
    }
  }
  
  onMounted(() => {
    // Você pode aplicar o tema aqui também se quiser!
    const savedTheme = localStorage.getItem('bingo_theme')
    if (savedTheme) document.documentElement.setAttribute('data-theme', savedTheme)
    
    fetchPhrases()
  })
  </script>