<script setup lang="ts">
import { ref } from 'vue';
import type { TaskResponse, TaskUpdateRequest, TaskStatus } from '@/types/dto';
import { TaskService } from '@/api/services';
import { SocketService } from '@/services/socket';

const props = defineProps<{
  boardId: number;
  task: TaskResponse;
  onClose: () => void;
}>();

const title = ref(props.task.title);
const description = ref(props.task.description || '');
const status = ref<TaskStatus>(props.task.status);
const error = ref('');
const loading = ref(false);

// Save title/description
async function saveTask() {
  if (!title.value.trim()) {
    error.value = 'Title is required';
    return;
  }

  loading.value = true;
  error.value = '';

  try {
    const data: TaskUpdateRequest = {
      title: title.value,
      description: description.value,
      status: status.value, // keep current status
    };

    const updatedTask = await TaskService.updateTask(props.boardId, props.task.id, data);

    // Merge updated task into existing tasks safely
    const tasks = SocketService.tasks[props.boardId] || [];
    const index = tasks.findIndex(t => t.id === updatedTask.id);
    if (index !== -1) tasks[index] = updatedTask;
    SocketService.tasks[props.boardId] = [...tasks]; // trigger reactivity
    SocketService.emitTaskUpdate(props.boardId, tasks);

    props.onClose();
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'Failed to update task';
  } finally {
    loading.value = false;
  }
}

// Update status immediately
async function updateStatus(newStatus: TaskStatus) {
  if (newStatus === status.value) return;

  const oldStatus = status.value;
  status.value = newStatus;

  try {
    const updatedTask = await TaskService.updateStatus(props.boardId, props.task.id, newStatus);

    const tasks = SocketService.tasks[props.boardId] || [];
    const index = tasks.findIndex(t => t.id === updatedTask.id);
    if (index !== -1) tasks[index] = updatedTask;
    SocketService.tasks[props.boardId] = [...tasks];
    SocketService.emitTaskUpdate(props.boardId, tasks);
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'Failed to update status';
    status.value = oldStatus; // revert on failure
  }
}

// Delete task
async function deleteTask() {
  if (!confirm('Are you sure you want to delete this task?')) return;

  try {
    await TaskService.deleteTask(props.boardId, props.task.id);

    const tasks = (SocketService.tasks[props.boardId] || []).filter(t => t.id !== props.task.id);
    SocketService.tasks[props.boardId] = [...tasks]; // trigger reactivity
    SocketService.emitTaskUpdate(props.boardId, tasks);

    props.onClose();
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'Failed to delete task';
  }
}
</script>

<template>
  <div class="fixed inset-0 flex items-center justify-center bg-black/40 z-50">
    <div class="bg-white p-6 rounded-xl shadow-lg w-full max-w-md">
      <h2 class="text-xl font-bold mb-4">Edit Task</h2>

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
          placeholder="Description"
          class="w-full border rounded-lg p-2 focus:ring focus:ring-blue-300"
        />
        <select v-model="status" @change="updateStatus(status)" class="w-full border rounded-lg p-2">
          <option value="TODO">TODO</option>
          <option value="IN_PROGRESS">IN_PROGRESS</option>
          <option value="DONE">DONE</option>
        </select>
      </div>

      <div class="mt-4 flex justify-between items-center">
        <button
          @click="deleteTask"
          class="px-4 py-2 rounded-lg bg-red-600 text-white hover:bg-red-700"
        >
          Delete
        </button>
        <div class="flex gap-2">
          <button
            @click="props.onClose"
            class="px-4 py-2 rounded-lg border hover:bg-gray-100"
          >
            Cancel
          </button>
          <button
            @click="saveTask"
            :disabled="loading"
            class="px-4 py-2 rounded-lg bg-blue-600 text-white hover:bg-blue-700 disabled:opacity-50"
          >
            Save
          </button>
        </div>
      </div>
    </div>
  </div>
</template>
