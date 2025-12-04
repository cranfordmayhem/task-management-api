<script setup lang="ts">
import { useRouter } from 'vue-router';
import type { BoardResponse } from '@/types/dto';

const props = defineProps<{
    board: BoardResponse
}>();

const router = useRouter();

function openBoard() {
    router.push(`/board/${props.board.id}`);
}
</script>

<template>
    <div
        @click="openBoard"
        class="cursor-pointer p-4 bg-white shadow rounded-lg hover:bg-gray-50 transition"
    >
        <h3 class="text-lg font-bold">{{ board.name }}</h3>
        <p class="text-gray-600 text-sm mt-1">{{ board.description }}</p>
    </div>
</template>
