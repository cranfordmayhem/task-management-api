<script setup lang="ts">
import { ref } from 'vue';
import { SocketService } from '@/services/socket';
import { TaskResponse, TaskRequest } from '@/types/dto';
import { TaskService } from '@/api/services';

const props = defineProps<{
  boardId: number;
  onClose: () => void; // callback to close the modal
}>();

const title = ref('');
const description = ref('');
const error = ref('');
const loading = ref(false);

async function createTask() {
  if (!title.value.trim()) {
    error.value = 'Title is required';
    return;
  }

  loading.value = true;
  error.value = '';

  try {
    // Call backend API to create the task
    // Call backend API to create the task
    const newTask: TaskResponse = await TaskService.addTask(props.boardId, {
        title: title.value,
        description: description.value || '',
        status: 'TODO',
    });

    // Ensure comments exists
    if (!newTask.comments) newTask.comments = [];

    // Fetch current tasks safely
    const fetchedTasks = await TaskService.getTasks(props.boardId);
    const currentTasks = SocketService.tasks[props.boardId] || fetchedTasks.content || [];


    // Merge new task
    const updatedTasks = [...currentTasks, newTask];

    // Update SocketService and emit update
    SocketService.tasks[props.boardId] = updatedTasks;
    SocketService.emitTaskUpdate(props.boardId, updatedTasks);

    // Close modal
    props.onClose();
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'Failed to create task';
  } finally {
    loading.value = false;
  }
}
</script>


<template>
  <div class="fixed inset-0 flex items-center justify-center bg-black/40 z-50">
    <div class="bg-white p-6 rounded-xl shadow-lg w-full max-w-md">
      <h2 class="text-xl font-bold mb-4">Add Task</h2>

      <p v-if="error" class="text-red-500 text-sm mb-2">{{ error }}</p>

      <div class="space-y-4">
        <input
          v-model="title"
          type="text"
          placeholder="Task Title"
          class="w-full border rounded-lg p-2 focus:ring focus:ring-blue-300"
        />
        <textarea
          v-model="description"
          placeholder="Description (optional)"
          class="w-full border rounded-lg p-2 focus:ring focus:ring-blue-300"
        />
      </div>

      <div class="mt-4 flex justify-end gap-2">
        <button
          @click="props.onClose()"
          class="px-4 py-2 rounded-lg border hover:bg-gray-100"
        >
          Cancel
        </button>
        <button
          @click="createTask"
          :disabled="loading"
          class="px-4 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700 disabled:opacity-50"
        >
          Create
        </button>
      </div>
    </div>
  </div>
</template>
