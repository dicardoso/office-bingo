<template>
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
</template>
<script setup>

</script>