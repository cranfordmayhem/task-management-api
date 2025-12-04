<script setup lang="ts">
    import { ref } from 'vue';
    import { useAuthStore } from '@/stores/auth';
    import { useRouter } from 'vue-router';

    const auth = useAuthStore();
    const router = useRouter();

    const email = ref("");
    const password = ref("");
    const error = ref("");

    async function handleLogin() {
        error.value = "";
        try { 
            await auth.login({
                username: email.value,
                password: password.value
            });
            router.push("/");
        } catch (e: any) {
            error.value = e?.response?.data?.message ?? "Invalid login credentials"
        }
    }
</script>

<template>
    <div class="min-h-screen bg-gray-100 flex items-center justify-center px-4">
        <div class="w-full max-w-md bg-white shadow-lg rounded-xl p-8">
            <h1 class="text-2xl font-bold text-gray-800 mb-6 text-center">
                Login to your Account
            </h1>

            <p v-if="error" class="text-red-600 text-sm mb-4 text-center">
                {{error}}
            </p>
            
            <form @submit.prevent="handleLogin" class="space-y-4">
                <div>
                    <label class="block text-sm font-medium text-gray-700">Email</label>
                    <input
                        type="email"
                        v-model="email"
                        class="w-full mt-1 px-3 py-2 border rounded-lg outline-none focus:ring focus:ring-blue-300"
                        placeholder="you@example.com"
                        required                  
                    />
                </div>
                <div>
                    <label class="block text-sm font-medkum text-gray-700">Password</label>
                    <input
                        type="password"
                        v-model="password"
                        class="w-full mt-1 px-3 py-2 border rounded-lg outline-none focus:ring focus:ring-blue-300"
                        placeholder="••••••••"
                        required
                    />
                </div>

                <button
                    type="submit"
                    class="w-full bg-blue-600 hover:bg-blue-700 text-white py-2 rounded-lg font-medium transition"
                >
                    Login
                </button>
            </form>

            <p class="text-center text-gray-600 text-sm mt-4">
                Don't have an account?
                <router-link
                    to="/register"
                    class="text-blue-600 hover: underline"
                >
                    Create One
                </router-link>
            </p>
        </div>
    </div>
</template>