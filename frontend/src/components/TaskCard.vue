<script setup lang="ts">
import type { TaskResponse } from '@/types/dto';
import CommentList from './CommentList.vue';
import { defineEmits } from 'vue';

const props = defineProps<{ task: TaskResponse }>();
const emit = defineEmits<{
  (e: 'edit', task: TaskResponse): void;
  (e: 'delete', taskId: number): void;
}>();

function editTask() {
  emit('edit', props.task);
}

function deleteTask() {
  emit('delete', props.task.id);
}
</script>

<template>
  <div class="p-2 border rounded-lg flex justify-between items-start">
    <div class="flex-1">
      <p class="font-semibold">{{ task.title }}</p>
      <p class="text-gray-500 text-sm">{{ task.description }}</p>
      <CommentList :comments="task.comments" />
    </div>

    <div class="flex flex-col gap-1 ml-2">
      <button
        @click="editTask"
        class="px-2 py-1 text-xs bg-blue-600 text-white rounded hover:bg-blue-700"
      >
        Edit
      </button>
      <button
        @click="deleteTask"
        class="px-2 py-1 text-xs bg-red-600 text-white rounded hover:bg-red-700"
      >
        Delete
      </button>
    </div>
  </div>
</template>
