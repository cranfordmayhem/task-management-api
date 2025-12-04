<script setup lang="ts">
import { ref } from 'vue';
import { BoardResponse } from '@/types/dto';
import { BoardService } from '@/api/services';

const props = defineProps<{
  board: BoardResponse;
  onClose: () => void;
}>();

const name = ref(props.board.name);
const description = ref(props.board.description || '');
const error = ref('');
const loading = ref(false);

async function saveBoard() {
  if (!name.value.trim()) {
    error.value = 'Name is required';
    return;
  }

  loading.value = true;
  error.value = '';

  try {
    // Use BoardService.updateBoard (you should add this method in services.ts if missing)
    await BoardService.updateBoard(props.board.id, {
      name: name.value,
      description: description.value,
    });

    // Update the local board object so the UI reflects the changes
    props.board.name = name.value;
    props.board.description = description.value;

    props.onClose();
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'Failed to update board';
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div class="fixed inset-0 flex items-center justify-center bg-black/40 z-50">
    <div class="bg-white p-6 rounded-xl shadow-lg w-full max-w-md">
      <h2 class="text-xl font-bold mb-4">Edit Board</h2>

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
          placeholder="Description"
          class="w-full border rounded-lg p-2 focus:ring focus:ring-blue-300"
        />
      </div>

      <div class="mt-4 flex justify-end gap-2">
        <button
          @click="props.onClose"
          class="px-4 py-2 rounded-lg border hover:bg-gray-100"
        >
          Cancel
        </button>
        <button
          @click="saveBoard"
          :disabled="loading"
          class="px-4 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700 disabled:opacity-50"
        >
          Save
        </button>
      </div>
    </div>
  </div>
</template>
