<script setup lang="ts">
    import { ref } from 'vue';
    import { useAuthStore } from '@/stores/auth'
    import { UserService, AuthService } from '@/api/services';
    import { useRouter } from 'vue-router';

    const router = useRouter();
    const auth = useAuthStore();

    const firstName = ref("");
    const lastName = ref("");
    const age = ref("");
    const email = ref("");
    const password = ref("");
    const error = ref("");

    async function handleRegister() {
        error.value = "";
        try { 
            await AuthService.register({
                email: email.value,
                password: password.value
            });
            await auth.login({
                username: email.value,
                password: password.value
            });
            await UserService.createProfile({
                firstName: firstName.value,
                lastName: lastName.value,
                age: age.value
            });
            router.push('/')
        } catch (e: any) {
            error.value = e?.response?.data?.message ?? "Failed to register account"
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
            
            <form @submit.prevent="handleRegister" class="space-y-4">
                <div>
                    <label class="block text-sm font-medium text-gray-700">First Name</label>
                    <input
                        type="text"
                        v-model="firstName"
                        class="w-full mt-1 px-3 py-2 border rounded-lg outline-none focus:ring focus:ring-blue-300"
                        placeholder="First Name"
                        required                  
                    />
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700">Last Name</label>
                    <input
                        type="text"
                        v-model="lastName"
                        class="w-full mt-1 px-3 py-2 border rounded-lg outline-none focus:ring focus:ring-blue-300"
                        placeholder="Last Name"
                        required                  
                    />
                </div>
                <div>
                    <label class="block text-sm font-medium text-gray-700">Age</label>
                    <input
                        type="number"
                        v-model="age"
                        class="w-full mt-1 px-3 py-2 border rounded-lg outline-none focus:ring focus:ring-blue-300"
                        placeholder="Age"
                        required                  
                    />
                </div>
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
                    Register
                </button>
            </form>

            <p class="text-center text-gray-600 text-sm mt-4">
                Already have an account?
                <router-link
                    to="/login"
                    class="text-blue-600 hover: underline"
                >
                    Login
                </router-link>
            </p>
        </div>
    </div>
</template>