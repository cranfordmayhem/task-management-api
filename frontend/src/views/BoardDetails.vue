<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import { BoardService, TaskService, MemberService } from '@/api/services';
import EditBoard from '@/components/EditBoard.vue';
import CreateTask from '@/components/AddTask.vue';
import EditTask from '@/components/EditTask.vue';

import type { BoardResponse, TaskResponse, BoardMemberResponse } from '@/types/dto';

const route = useRoute();
const router = useRouter();
const boardId = Number(route.params.id);

// Board
const board = ref<BoardResponse | null>(null);
const loadingBoard = ref(true);
const showEditBoard = ref(false);

// Tasks
const tasks = ref<TaskResponse[]>([]);
const loadingTasks = ref(true);
const showCreateTask = ref(false);
const editingTask = ref<TaskResponse | null>(null);

// Members
const members = ref<BoardMemberResponse[]>([]);
const loadingMembers = ref(true);
const newMemberId = ref<number | null>(null);

const error = ref('');

// Load board details
async function loadBoard() {
  loadingBoard.value = true;
  try {
    const res = await BoardService.getBoard(boardId);
    board.value = res.data;
  } catch (e: any) {
    error.value = e?.response?.data?.message ?? 'Failed to load board';
  } finally {
    loadingBoard.value = false;
  }
}

// Load tasks
async function loadTasks() {
  loadingTasks.value = true;
  try {
    const res = await TaskService.getTasks(boardId, 0, 100);
    tasks.value = res.content;
  } catch {
    console.error('Failed to load tasks');
  } finally {
    loadingTasks.value = false;
  }
}

// Load members
async function loadMembers() {
  loadingMembers.value = true;
  try {
    const res = await MemberService.getMembers(boardId, 0, 100);
    members.value = res.data.content;
  } catch {
    console.error('Failed to load members');
  } finally {
    loadingMembers.value = false;
  }
}

// Board actions
async function deleteBoard() {
  if (!confirm('Delete this board permanently?')) return;
  try {
    await BoardService.deleteBoard(boardId);
    router.push('/');
  } catch {
    alert('Failed to delete board');
  }
}

// Member actions
async function addMember() {
  if (!newMemberId.value) return;
  try {
    await MemberService.addMember(boardId, newMemberId.value);
    newMemberId.value = null;
    loadMembers();
  } catch {
    alert('Failed to add member');
  }
}

async function removeMember(userId: number) {
  if (!confirm('Remove this member?')) return;
  try {
    await MemberService.removeMember(boardId, userId);
    loadMembers();
  } catch {
    alert('Failed to remove member');
  }
}

// Task actions
function openTask(task: TaskResponse) {
  editingTask.value = task;
}

function closeTaskModal() {
  editingTask.value = null;
}

function refreshAfterTaskUpdate(updatedTask: TaskResponse) {
  const i = tasks.value.findIndex(t => t.id === updatedTask.id);
  if (i !== -1) tasks.value[i] = updatedTask;
}

// Task API updates
async function updateTaskStatus(task: TaskResponse, status: string) {
  try {
    const updatedTask = await TaskService.updateStatus(boardId, task.id, status);
    const i = tasks.value.findIndex(t => t.id === task.id);
    if (i !== -1) tasks.value[i] = updatedTask;
  } catch {
    alert('Failed to update status');
  }
}

async function updateTaskAssignee(task: TaskResponse, userId: number | null) {
  try {
    const updatedTask = await TaskService.updateTask(boardId, task.id, {
      title: task.title,
      description: task.description,
      status: task.status,
      assignedUserId: userId,
    });
    const i = tasks.value.findIndex(t => t.id === task.id);
    if (i !== -1) tasks.value[i] = updatedTask;
  } catch {
    alert('Failed to update assignee');
  }
}

onMounted(async () => {
  await loadBoard();
  await loadTasks();
  await loadMembers();
});
</script>

<template>
  <div class="p-6 min-h-screen bg-gray-100">
    <button @click="router.back()" class="text-blue-600 underline mb-4">← Back</button>

    <!-- Board Info -->
    <div class="flex justify-between items-start mb-6">
      <div>
        <h1 class="text-3xl font-bold">{{ board?.name }}</h1>
        <p class="text-gray-600">{{ board?.description }}</p>
      </div>
      <div class="flex gap-2">
        <button @click="showEditBoard = true" class="px-4 py-2 bg-yellow-500 text-white rounded hover:bg-yellow-600">
          Edit Board
        </button>
        <button @click="deleteBoard" class="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700">
          Delete
        </button>
      </div>
    </div>

    <!-- Members -->
    <div class="mb-6">
      <h2 class="text-xl font-semibold mb-2">Members</h2>
      <div class="flex gap-2 mb-2">
        <select v-model="newMemberId" class="border px-2 py-1 rounded">
          <option value="" disabled>Select user ID to add</option>
          <!-- Replace with real user options -->
          <option v-for="i in 10" :key="i" :value="i">User {{ i }}</option>
        </select>
        <button @click="addMember" class="px-3 py-1 bg-green-600 text-white rounded hover:bg-green-700">Add</button>
      </div>
      <ul>
        <li v-for="member in members" :key="member.userId" class="flex justify-between items-center bg-white p-2 rounded mb-1">
          {{ member.email }} ({{ member.roles }})
          <button @click="removeMember(member.userId)" class="px-2 py-1 bg-red-500 text-white rounded hover:bg-red-600">Remove</button>
        </li>
      </ul>
    </div>

    <!-- Tasks -->
    <div class="flex justify-between mb-3">
      <h2 class="text-xl font-semibold">Tasks</h2>
      <button @click="showCreateTask = true" class="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700">Add Task</button>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <div v-for="task in tasks" :key="task.id" class="p-4 border rounded-xl hover:bg-gray-50">
        <div class="flex justify-between items-start mb-2">
          <div>
            <h3 class="font-semibold">{{ task.title }}</h3>
            <p class="text-gray-500 text-sm">{{ task.description }}</p>
          </div>
          <!-- Status -->
          <select v-model="task.status" @change="updateTaskStatus(task, task.status)" class="border px-2 py-1 rounded">
            <option value="TODO">TODO</option>
            <option value="IN_PROGRESS">IN_PROGRESS</option>
            <option value="DONE">DONE</option>
          </select>
        </div>
        <!-- Assignee -->
        <div class="mt-2 flex items-center gap-2">
          <label class="text-sm">Assigned:</label>
          <select v-model="task.assignedUserId" @change="updateTaskAssignee(task, task.assignedUserId)" class="border px-2 py-1 rounded flex-1">
            <option :value="null">Unassigned</option>
            <option v-for="member in members" :key="member.userId" :value="member.userId">
              {{ member.email}}
            </option>
          </select>
        </div>
        <button @click="openTask(task)" class="mt-2 px-3 py-1 bg-gray-300 rounded hover:bg-gray-400">Edit</button>
      </div>
    </div>

    <!-- Modals -->
    <EditBoard v-if="showEditBoard && board" :board="board" @close="showEditBoard = false" @updated="loadBoard" />
    <CreateTask v-if="showCreateTask" :boardId="boardId" @close="showCreateTask = false" @created="loadTasks" />
    <EditTask v-if="editingTask" :boardId="boardId" :task="editingTask" @close="closeTaskModal" @updated="refreshAfterTaskUpdate" @deleted="loadTasks" />
  </div>
</template>

<style scoped>
</style>