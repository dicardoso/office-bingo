<template>
  <div class="flex min-h-screen items-center justify-center p-4 relative overflow-hidden">
    <div class="absolute inset-0 opacity-5 pointer-events-none font-mono text-xs p-4 overflow-hidden select-none">
      01010101 SYSTEM_INIT_BINGO_KERNEL... LOAD_MODULES... AUTH_REQUIRED...
    </div>

    <div class="w-full max-w-md bg-ide-panel p-8 rounded-lg shadow-2xl border border-ide-border z-10">
      
      <div class="mb-6 border-l-4 border-ide-accent pl-4">
        <h1 class="text-3xl font-bold text-white font-mono tracking-tighter">
          OFFICE_BINGO<span v-if="view.startsWith('REGISTER')" class="text-ide-accent">_CADASTRO</span><span v-if="view.startsWith('RECOVER')" class="text-ide-error">_RECOVERY</span><span class="animate-pulse text-ide-accent">_</span>
        </h1>
        <p v-if="view === ViewEnum.LOGIN" class="text-ide-dim text-sm mt-1">Ambiente de descompressão para Devs.</p>
        <p v-else-if="view === ViewEnum.REGISTER" class="text-ide-dim text-sm mt-1">Inicializar novo utilizador.</p>
        <p v-else-if="view === ViewEnum.REGISTER_VERIFY" class="text-ide-success text-sm mt-1">E-mail enviado! Confirme a sua identidade.</p>
        <p v-else-if="view === ViewEnum.RECOVER_REQ" class="text-ide-dim text-sm mt-1">Recuperação de credenciais.</p>
        <p v-else-if="view === ViewEnum.RECOVER_VERIFY" class="text-ide-success text-sm mt-1">E-mail enviado! Verifique a sua caixa de entrada.</p>
      </div>

      <div v-if="error" class="mb-4 bg-red-900/20 border border-red-500/50 p-3 rounded text-red-400 text-xs font-mono animate-fade-in">
        [ERROR] {{ error }}
      </div>
      <div v-if="successMsg" class="mb-4 bg-green-900/20 border border-green-500/50 p-3 rounded text-green-400 text-xs font-mono animate-fade-in">
        [SUCCESS] {{ successMsg }}
      </div>

      <form v-if="view === ViewEnum.LOGIN" @submit.prevent="handleLogin" class="space-y-5 animate-fade-in">
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
        
        <div class="space-y-1">
          <div class="flex justify-between items-end">
            <label class="text-xs font-mono text-ide-accent uppercase">Access_Key</label>
            <button type="button" @click="changeView(ViewEnum.RECOVER_REQ)" class="text-[10px] text-ide-dim hover:text-white transition-colors font-mono underline decoration-ide-dim border-none bg-transparent p-0">
              Esqueci a Access_Key
            </button>
          </div>
          <input 
            v-model="form.password" 
            autocomplete="current-password"
            type="password" 
            required 
            class="w-full bg-ide-bg border border-ide-border rounded p-3 text-white focus:border-ide-accent focus:outline-none focus:ring-1 focus:ring-ide-accent transition-all font-mono placeholder-gray-700"
            placeholder="••••••••"
          />
        </div>

        <button :disabled="loading" type="submit" class="w-full bg-ide-accent hover:bg-sky-400 text-ide-bg font-bold py-3 rounded transition-all disabled:opacity-50 disabled:cursor-not-allowed font-mono uppercase tracking-wide">
          {{ loading ? 'Authenticating...' : 'Connect' }}
        </button>

        <div class="mt-6 text-center border-t border-ide-border pt-4">
          <button type="button" @click="changeView(ViewEnum.REGISTER)" class="text-xs text-ide-dim hover:text-white transition-colors font-mono">
            Create new ssh_key (Register) >>
          </button>
        </div>
      </form>

      <form v-if="view === ViewEnum.REGISTER" @submit.prevent="handleRegisterOtp" class="space-y-5 animate-fade-in">
        <div class="space-y-1">
          <label class="text-xs font-mono text-ide-accent uppercase">Corporate_Email</label>
          <input 
            v-model="form.email" 
            type="email" 
            required 
            class="w-full bg-ide-bg border border-ide-border rounded p-3 text-white focus:border-ide-accent focus:outline-none focus:ring-1 focus:ring-ide-accent transition-all font-mono placeholder-gray-700"
            placeholder="dev@empresa.com"
          />
        </div>
        
        <button :disabled="loading" type="submit" class="w-full bg-ide-accent hover:bg-sky-400 text-ide-bg font-bold py-3 rounded transition-all disabled:opacity-50 disabled:cursor-not-allowed font-mono uppercase tracking-wide">
          {{ loading ? 'Processando...' : 'Receber Código de Acesso' }}
        </button>

        <div class="mt-6 text-center border-t border-ide-border pt-4">
          <button type="button" @click="changeView(ViewEnum.LOGIN)" class="text-xs text-ide-dim hover:text-white transition-colors font-mono">
            << Back to Login
          </button>
        </div>
      </form>

      <form v-if="view === ViewEnum.REGISTER_VERIFY" @submit.prevent="handleRegisterFinalize" class="space-y-5 animate-fade-in">
        <p class="text-xs text-ide-dim mb-2 text-center">Enviado para: <span class="font-bold text-white">{{ form.email }}</span></p>

        <div class="space-y-1">
          <label class="text-xs font-mono text-ide-accent uppercase">Auth_Code (OTP)</label>
          <input 
            v-model="form.code" 
            type="text" 
            maxlength="6"
            required 
            class="w-full bg-black border border-ide-border rounded p-3 text-center text-2xl tracking-[0.5em] text-ide-accent font-bold focus:border-ide-accent focus:outline-none focus:ring-1 focus:ring-ide-accent transition-all font-mono"
            placeholder="000000"
          />
        </div>

        <div class="space-y-1">
          <label class="text-xs font-mono text-ide-accent uppercase">User_ID (Username)</label>
          <input 
            v-model="form.username" 
            type="text" 
            required 
            class="w-full bg-ide-bg border border-ide-border rounded p-3 text-white focus:border-ide-accent focus:outline-none focus:ring-1 focus:ring-ide-accent transition-all font-mono placeholder-gray-700"
            placeholder="ex: jose.silva"
          />
        </div>

        <div class="space-y-1">
          <label class="text-xs font-mono text-ide-accent uppercase">Access_Key (Password)</label>
          <input 
            v-model="form.password" 
            type="password" 
            required 
            class="w-full bg-ide-bg border border-ide-border rounded p-3 text-white focus:border-ide-accent focus:outline-none focus:ring-1 focus:ring-ide-accent transition-all font-mono placeholder-gray-700"
            placeholder="Nova Senha"
          />
        </div>

        <button :disabled="loading || form.code.length < 6" type="submit" class="w-full bg-ide-success hover:bg-green-400 text-ide-bg font-bold py-3 rounded transition-all disabled:opacity-50 disabled:cursor-not-allowed font-mono uppercase tracking-wide">
          {{ loading ? 'A criar conta...' : 'Initialize User' }}
        </button>

        <div class="text-center pt-2 flex flex-col gap-2">
          <button type="button" @click="changeView(ViewEnum.REGISTER)" class="text-[10px] text-ide-dim hover:text-white transition-colors font-mono">
            [ Alterar E-mail ]
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
  REGISTER_VERIFY = 'REGISTER_VERIFY',
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
  code: '',
  recoveryCode: '',
  newPassword: ''
})

const changeView = (newView: ViewEnum) => {
  error.value = ''
  successMsg.value = ''
  view.value = newView
  
  if (newView === ViewEnum.LOGIN) {
    sessionStorage.removeItem('recovery_email')
    sessionStorage.removeItem('register_email')
    form.value.password = ''
  }
}

const handleLogin = async () => {
  loading.value = true
  error.value = ''
  try {
    const response = await api('/auth/login', {
      method: 'POST',
      body: { username: form.value.username, password: form.value.password }
    })
    localStorage.setItem('bingo_token', response.token)
    localStorage.setItem('bingo_user', JSON.stringify(response.user))
    router.push('/')
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Acesso negado: Verifique credenciais ou servidor.'
  } finally {
    loading.value = false
  }
}

const handleRegisterOtp = async () => {
  loading.value = true
  error.value = ''
  try {
    await api('/auth/register-otp', {
      method: 'POST',
      body: { email: form.value.email }
    })
    sessionStorage.setItem('register_email', form.value.email)
    changeView(ViewEnum.REGISTER_VERIFY)
    successMsg.value = 'Código enviado! Verifique o seu e-mail corporativo.'
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Erro ao processar pedido. Tente novamente.'
  } finally {
    loading.value = false
  }
}

const handleRegisterFinalize = async () => {
  loading.value = true
  error.value = ''
  try {
    const response = await api('/auth/register', {
      method: 'POST',
      body: { 
        username: form.value.username, 
        email: form.value.email, 
        password: form.value.password,
        code: form.value.code
      }
    })
    
    localStorage.setItem('bingo_token', response.token)
    localStorage.setItem('bingo_user', JSON.stringify(response.user))
    sessionStorage.removeItem('register_email')
    
    router.push('/')
  } catch (err) {
    error.value = err instanceof Error ? err.message : 'Erro ao concluir registo. Código inválido?'
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
    error.value = err instanceof Error && err.message ? err.message : 'Código inválido ou expirado.'
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

  const savedRegisterEmail = sessionStorage.getItem('register_email')
  if (savedRegisterEmail) {
    form.value.email = savedRegisterEmail
    view.value = ViewEnum.REGISTER_VERIFY
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