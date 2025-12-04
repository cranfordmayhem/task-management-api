<script setup lang="ts">
    import { computed, ref, onMounted } from 'vue';
    import { useAuthStore } from '@/stores/auth';
    import { useRouter } from 'vue-router';
    import { UserProfileResponse } from '@/types/dto';
    import { UserService } from '@/api/services'

    const auth = useAuthStore();
    const router = useRouter();
    const profile = ref<UserProfileResponse>();

    onMounted(async() => {
        const { data } = await UserService.getProfileViaLogin();
        profile.value = data;
    });

    const userName = computed(() => profile.value?.firstName || 'User')

    function handleLogout() {
        auth.logout();
        router.push('/login');
    }
</script>

<template>
    <nav class="bg-white shadow-md px-6 py-4 flex justify-between items-center">
        <div class="text-xl font-bold text-gray-800">
            Welcome, {{userName}}
        </div>

        <button
            @click="handleLogout"
            class="bg-red-500 hover:bg-red-600 text-white px-4 py-2 rounded-lg transition"
        >
            Logout
        </button>
    </nav>
</template>