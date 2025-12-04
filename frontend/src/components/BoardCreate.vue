<script setup lang="ts">
    import { ref } from 'vue';
    import { BoardService } from '@/api/services';
    import type { BoardRequest } from '@/types/dto';
    import { useRouter } from 'vue-router';

    const name = ref('');
    const description = ref('');
    const error = ref('');
    const loading = ref(false);
    const props = defineProps<{
        show: boolean
    }>();

    const emit = defineEmits<{
        (e: 'close'): void
        (e: 'created'): void
    }>();

    const router = useRouter();

    async function createBoard() {
        error.value = '';
        loading.value = true;

        try {
            const payload: BoardRequest = {
                name: name.value,
                description: description.value || null,
            };
            await BoardService.createBoard(payload);
            emit('created');
            emit('close');
            router.push('/');
        } catch (e: any) {
            error.value = e?.response?.data?.message ?? 'Failed to create board';
        } finally {
            loading.value = false;
        }
    }
</script>

<template>
    <div v-if="show" class="fixed inset-0 flex items-center justify-center bg-black/40 z-50">
        <div class="bg-white p-6 rounded-xl shadow-lg w-full max-w-md">
            <h2 class="text-xl font-bold mb-4">Create Board</h2>

            <p v-if="error" class="text-red-500 text-sm mb-2">{{ error }}</p>

            <div class="space-y-4">
                <input
                    v-model="name"
                    type="text"
                    placeholder="Board Name"
                    class="w-full border rounded-lg p-2 focus:ring focus:ring-blue-300"
                />
                <textarea
                    v-model="description"
                    placeholder="Description (optional)"
                    class="w-full border rounded-lg p-2 focus:ring focus:ring-blue-300"
                >
                </textarea>
            </div>

            <div class="mt-4 flex justify-end gap-2">
                <button
                    @click="emit('close')"
                    class="px-4 py-2 rounded-lg border hover:bg-gray-100"
                >
                    Cancel
                </button>
                <button
                    @click="createBoard"
                    :disabled="loading"
                    class="px-4 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700 disabled:opacity-50"
                >
                    Create
                </button>
            </div>
        </div>
    </div>
</template>