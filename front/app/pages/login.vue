<template>
  <div class="flex min-h-screen items-center justify-center p-4 relative overflow-hidden">
    <div class="absolute inset-0 opacity-5 pointer-events-none font-mono text-xs p-4 overflow-hidden select-none">
      01010101 SYSTEM_INIT_BINGO_KERNEL... LOAD_MODULES... AUTH_REQUIRED...
    </div>

    <div class="w-full max-w-md bg-ide-panel p-8 rounded-lg shadow-2xl border border-ide-border z-10">
      
      <div class="mb-6 border-l-4 border-ide-accent pl-4">
        <h1 class="text-3xl font-bold text-white font-mono tracking-tighter">
          OFFICE_BINGO<span v-if="view === ViewEnum.REGISTER" class="text-ide-accent">_CADASTRO</span><span v-if="view.startsWith('RECOVER')" class="text-ide-error">_RECOVERY</span><span class="animate-pulse text-ide-accent">_</span>
        </h1>
        <p v-if="view === ViewEnum.LOGIN || view === ViewEnum.REGISTER" class="text-ide-dim text-sm mt-1">Ambiente de descompressão para Devs.</p>
        <p v-else-if="view === ViewEnum.RECOVER_REQ" class="text-ide-dim text-sm mt-1">Recuperação de credenciais.</p>
        <p v-else-if="view === ViewEnum.RECOVER_VERIFY" class="text-ide-success text-sm mt-1">E-mail enviado! Verifique a sua caixa de entrada.</p>
      </div>

      <div v-if="error" class="mb-4 bg-red-900/20 border border-red-500/50 p-3 rounded text-red-400 text-xs font-mono animate-fade-in">
        [ERROR] {{ error }}
      </div>
      <div v-if="successMsg" class="mb-4 bg-green-900/20 border border-green-500/50 p-3 rounded text-green-400 text-xs font-mono animate-fade-in">
        [SUCCESS] {{ successMsg }}
      </div>

      <form v-if="view === ViewEnum.LOGIN || view === ViewEnum.REGISTER" @submit.prevent="handleSubmit" class="space-y-5 animate-fade-in">
        <div class="space-y-1">
          <label class="text-xs font-mono text-ide-accent uppercase">User_ID</label>
          <input 
            v-model="form.username" 
            autocomplete="username"
            type="text" 
            required 
            class="w-full bg-ide-bg border border-ide-border rounded p-3 text-white focus:border-ide-accent focus:outline-none focus:ring-1 focus:ring-ide-accent transition-all font-mono placeholder-gray-700"
            placeholder="dev.junior"
          />
        </div>

        <div v-if="view === ViewEnum.REGISTER" class="space-y-1 animate-fade-in">
          <label class="text-xs font-mono text-ide-accent uppercase">Corporate_Email</label>
          <input 
            v-model="form.email" 
            type="email" 
            required 
            class="w-full bg-ide-bg border border-ide-border rounded p-3 text-white focus:border-ide-accent focus:outline-none focus:ring-1 focus:ring-ide-accent transition-all font-mono placeholder-gray-700"
            placeholder="dev@empresa.com"
          />
        </div>
        
        <div class="space-y-1">
          <div class="flex justify-between items-end">
            <label class="text-xs font-mono text-ide-accent uppercase">Access_Key</label>
          </div>
          <input 
          v-model="form.password" 
          autocomplete="current-password"
          type="password" 
          required 
          class="w-full bg-ide-bg border border-ide-border rounded p-3 text-white focus:border-ide-accent focus:outline-none focus:ring-1 focus:ring-ide-accent transition-all font-mono placeholder-gray-700"
          placeholder="••••••••"
          />
          <button v-if="view === ViewEnum.LOGIN" type="button" @click="changeView(ViewEnum.RECOVER_REQ)" class="text-[10px] text-ide-dim hover:text-white transition-colors font-mono underline decoration-ide-dim border-none bg-transparent p-0">
            Esqueci a Access_Key
          </button>
        </div>

        <button 
          :disabled="loading" 
          type="submit" 
          class="w-full bg-ide-accent hover:bg-sky-400 text-ide-bg font-bold py-3 rounded transition-all disabled:opacity-50 disabled:cursor-not-allowed font-mono uppercase tracking-wide"
        >
          {{ loading ? 'Authenticating...' : (view === ViewEnum.REGISTER ? 'Initialize User' : 'Connect') }}
        </button>

        <div class="mt-6 text-center border-t border-ide-border pt-4">
          <button type="button" @click="changeView(view === ViewEnum.LOGIN ? ViewEnum.REGISTER : ViewEnum.LOGIN)" class="text-xs text-ide-dim hover:text-white transition-colors font-mono">
            {{ view === ViewEnum.LOGIN ? 'Create new ssh_key (Register) >>' : '<< Back to Login' }}
          </button>
        </div>
      </form>

      <form v-if="view === ViewEnum.RECOVER_REQ" @submit.prevent="handleRecoveryRequest" class="space-y-5 animate-fade-in">
        <div class="space-y-1">
          <label class="text-xs font-mono text-ide-accent uppercase">Corporate_Email</label>
          <input 
            v-model="form.email" 
            type="email" 
            required 
            class="w-full bg-ide-bg border border-ide-border rounded p-3 text-white focus:border-ide-accent focus:outline-none focus:ring-1 focus:ring-ide-accent transition-all font-mono placeholder-gray-700"
            placeholder="Insira o seu e-mail cadastrado"
          />
        </div>

        <button :disabled="loading" type="submit" class="w-full bg-ide-error hover:bg-red-400 text-white font-bold py-3 rounded transition-all disabled:opacity-50 disabled:cursor-not-allowed font-mono uppercase tracking-wide">
          {{ loading ? 'Processando...' : 'Enviar Código (OTP)' }}
        </button>

        <div class="text-center pt-2">
          <button type="button" @click="changeView(ViewEnum.LOGIN)" class="text-xs text-ide-dim hover:text-white transition-colors font-mono">
            [ Cancelar Operação ]
          </button>
        </div>
      </form>

      <form v-if="view === ViewEnum.RECOVER_VERIFY" @submit.prevent="handleRecoveryReset" class="space-y-5 animate-fade-in">
        
        <div class="space-y-1">
          <label class="text-xs font-mono text-ide-accent uppercase">Auth_Code (OTP)</label>
          <input 
            v-model="form.recoveryCode" 
            type="text" 
            maxlength="6"
            required 
            class="w-full bg-black border border-ide-border rounded p-3 text-center text-2xl tracking-[0.5em] text-ide-accent font-bold focus:border-ide-accent focus:outline-none focus:ring-1 focus:ring-ide-accent transition-all font-mono"
            placeholder="000000"
          />
        </div>

        <div class="space-y-1">
          <label class="text-xs font-mono text-ide-accent uppercase">New_Access_Key</label>
          <input 
            v-model="form.newPassword" 
            type="password" 
            required 
            class="w-full bg-ide-bg border border-ide-border rounded p-3 text-white focus:border-ide-accent focus:outline-none focus:ring-1 focus:ring-ide-accent transition-all font-mono placeholder-gray-700"
            placeholder="Nova Senha"
          />
        </div>

        <button :disabled="loading || form.recoveryCode.length < 6" type="submit" class="w-full bg-ide-success hover:bg-green-400 text-ide-bg font-bold py-3 rounded transition-all disabled:opacity-50 disabled:cursor-not-allowed font-mono uppercase tracking-wide">
          {{ loading ? 'Atualizando...' : 'Salvar Nova Senha' }}
        </button>

        <div class="text-center pt-2">
          <button type="button" @click="changeView(ViewEnum.LOGIN)" class="text-xs text-ide-dim hover:text-white transition-colors font-mono">
            [ Abortar e Voltar ]
          </button>
        </div>
      </form>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
const api = useAPI()
const router = useRouter()

enum ViewEnum {
  LOGIN = 'LOGIN',
  REGISTER = 'REGISTER',
  RECOVER_REQ = 'RECOVER_REQ',
  RECOVER_VERIFY = 'RECOVER_VERIFY',
}
const view = ref<ViewEnum>(ViewEnum.LOGIN) 

const error = ref('')
const successMsg = ref('')
const loading = ref(false)

const form = ref({
  username: '',
  password: '',
  email: '',
  recoveryCode: '',
  newPassword: ''
})

const changeView = (newView: ViewEnum) => {
  error.value = ''
  successMsg.value = ''
  view.value = newView
  
  if (newView === ViewEnum.LOGIN || newView === ViewEnum.REGISTER) {
    sessionStorage.removeItem('recovery_email')
  }
}

const handleSubmit = async () => {
  loading.value = true
  error.value = ''
  
  try {
    const endpoint = view.value === ViewEnum.REGISTER ? '/auth/register' : '/auth/login'
    
    const payload = view.value === ViewEnum.REGISTER 
      ? { username: form.value.username, email: form.value.email, password: form.value.password }
      : { username: form.value.username, password: form.value.password }
      
    const response = await api(endpoint, {
      method: 'POST',
      body: payload
    })

    localStorage.setItem('bingo_token', response.token)
    localStorage.setItem('bingo_user', JSON.stringify(response.user))
    
    router.push('/')
  } catch (err) {
    if (err instanceof Error) {
       error.value = err.message || 'Acesso negado: Verifique credenciais ou servidor.'
     } else {
       error.value = 'Acesso negado: Verifique credenciais ou servidor.'
     }
  } finally {
    loading.value = false
  }
}

const handleRecoveryRequest = async () => {
  loading.value = true
  error.value = ''
  try {
    await api('/auth/forgot-password', {
      method: 'POST',
      body: { email: form.value.email }
    })
    sessionStorage.setItem('recovery_email', form.value.email)
    
    changeView(ViewEnum.RECOVER_VERIFY)
  } catch (err) {
    error.value = 'Erro ao processar pedido. Tente novamente.'
  } finally {
    loading.value = false
  }
}

const handleRecoveryReset = async () => {
  loading.value = true
  error.value = ''
  try {
    await api('/auth/reset-password', {
      method: 'POST',
      body: { 
        email: form.value.email,
        code: form.value.recoveryCode,
        newPassword: form.value.newPassword
      }
    })
    
    sessionStorage.removeItem('recovery_email')
    form.value.password = ''
    form.value.newPassword = ''
    form.value.recoveryCode = ''
    
    changeView(ViewEnum.LOGIN)
    successMsg.value = 'Senha alterada! Pode fazer login agora.'
  } catch (err) {
    if (err instanceof Error && err.message) {
       error.value = err.message
     } else {
       error.value = 'Código inválido ou expirado.'
     }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  const savedRecoveryEmail = sessionStorage.getItem('recovery_email')
  if (savedRecoveryEmail) {
    form.value.email = savedRecoveryEmail
    view.value = ViewEnum.RECOVER_VERIFY
  }
})
</script>

<style scoped>
.animate-fade-in {
  animation: fadeIn 0.3s ease-out;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(5px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>