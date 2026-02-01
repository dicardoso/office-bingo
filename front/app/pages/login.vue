<template>
  <div class="flex min-h-screen items-center justify-center p-4 relative overflow-hidden">
    <div class="absolute inset-0 opacity-5 pointer-events-none font-mono text-xs p-4 overflow-hidden select-none">
      01010101 SYSTEM_INIT_BINGO_KERNEL... LOAD_MODULES... AUTH_REQUIRED...
    </div>

    <div class="w-full max-w-md bg-ide-panel p-8 rounded-lg shadow-2xl border border-ide-border z-10">
      <div class="mb-6 border-l-4 border-ide-accent pl-4">
        <h1 class="text-3xl font-bold text-white font-mono tracking-tighter">
          OFFICE_BINGO<span v-if="isRegister" class=" text-ide-accent">_CADASTRO</span><span class="animate-pulse text-ide-accent">_</span>
        </h1>
        <p class="text-ide-dim text-sm mt-1">Ambiente de descompressão para Devs.</p>
      </div>

      <form @submit.prevent="handleSubmit" class="space-y-5">
        <div class="space-y-1">
          <label class="text-xs font-mono text-ide-accent uppercase">User_ID</label>
          <input 
            v-model="username" 
            type="text" 
            required 
            class="w-full bg-ide-bg border border-ide-border rounded p-3 text-white focus:border-ide-accent focus:outline-none focus:ring-1 focus:ring-ide-accent transition-all font-mono placeholder-gray-700"
            placeholder="dev.junior"
          />
        </div>
        
        <div class="space-y-1">
          <label class="text-xs font-mono text-ide-accent uppercase">Access_Key</label>
          <input 
            v-model="password" 
            type="password" 
            required 
            class="w-full bg-ide-bg border border-ide-border rounded p-3 text-white focus:border-ide-accent focus:outline-none focus:ring-1 focus:ring-ide-accent transition-all font-mono placeholder-gray-700"
            placeholder="••••••••"
          />
        </div>

        <div v-if="error" class="bg-red-900/20 border border-red-500/50 p-3 rounded text-red-400 text-xs font-mono">
          [ERROR] {{ error }}
        </div>

        <button 
          :disabled="loading" 
          type="submit" 
          class="w-full bg-ide-accent hover:bg-sky-400 text-ide-bg font-bold py-3 rounded transition-all disabled:opacity-50 disabled:cursor-not-allowed font-mono uppercase tracking-wide"
        >
          {{ loading ? 'Authenticating...' : (isRegister ? 'Initialize User' : 'Connect') }}
        </button>
      </form>

      <div class="mt-6 text-center border-t border-ide-border pt-4">
        <button @click="isRegister = !isRegister" class="text-xs text-ide-dim hover:text-white transition-colors font-mono">
          {{ isRegister ? '<< Back to Login' : 'Create new ssh_key (Register) >>' }}
        </button>
      </div>
    </div>
  </div>
</template>
<script setup>
const api = useAPI()
const router = useRouter()

const isRegister = ref(false)
const username = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

const handleSubmit = async () => {
  loading.value = true
  error.value = ''
  
  try {
    const endpoint = isRegister.value ? '/auth/register' : '/auth/login'
    
    const response = await api(endpoint, {
      method: 'POST',
      body: { username: username.value, password: password.value }
    })

    localStorage.setItem('bingo_token', response.token)
    localStorage.setItem('bingo_user', JSON.stringify(response.user))
    
    router.push('/')
  } catch (err) {
    error.value = 'Acesso negado: Verifique credenciais ou servidor.'
  } finally {
    loading.value = false
  }
}
</script>