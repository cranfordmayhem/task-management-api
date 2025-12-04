<script setup lang="ts">
import Navbar from '@/components/layout/Navbar.vue';
import BoardCard from '@/components/BoardCard.vue';
import BoardCreate from '@/components/BoardCreate.vue';

import { ref, onMounted } from 'vue';
import { BoardService } from '@/api/services';
import type { BoardResponse } from '@/types/dto';

const boards = ref<BoardResponse[]>([]);
const page = ref(0);
const size = ref(5);
const loading = ref(false);

const showForm = ref(false);
const error = ref('');

async function fetchBoards() {
  loading.value = true;
  try {
    const { data } = await BoardService.getBoardsByMember(
      page.value,
      size.value
    );

    boards.value = data.content ?? data;
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'Failed to load boards';
  } finally {
    loading.value = false;
  }
}

onMounted(fetchBoards);

function openCreateBoard() {
  showForm.value = true;
}

function closeCreateBoard() {
  showForm.value = false;
}

function boardCreated() {
  showForm.value = false;
  fetchBoards(); // refresh after creation
}
</script>

<template>
  <div class="min-h-screen bg-gray-100">
    <!-- NAVBAR -->
    <Navbar />

    <main class="max-w-6xl mx-auto py-10 px-4">
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-2xl font-bold">Boards</h2>

        <button
          @click="openCreateBoard"
          class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
        >
          + Create Board
        </button>
      </div>

      <!-- CREATE BOARD MODAL -->
      <BoardCreate
        :show="showForm"
        @close="closeCreateBoard"
        @created="boardCreated"
      />

      <!-- LOADING -->
      <p v-if="loading" class="text-gray-600">Loading boards...</p>

      <!-- ERROR -->
      <p v-if="error" class="text-red-500">{{ error }}</p>

      <!-- EMPTY STATE -->
      <div
        v-if="!loading && boards.length === 0"
        class="text-gray-600 text-center py-8 border rounded-lg bg-white"
      >
        No boards yet. Create one!
      </div>

      <!-- BOARD LIST -->
      <div
        v-if="boards.length > 0"
        class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6"
      >
        <BoardCard
          v-for="board in boards"
          :key="board.id"
          :board="board"
        />
      </div>
    </main>
  </div>
</template>
