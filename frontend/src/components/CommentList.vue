<script setup lang="ts">
import type { TaskCommentResponse } from '@/types/dto';
const props = defineProps<{ comments: TaskCommentResponse[] }>();
</script>

<template>
  <ul class="mt-1 ml-4">
    <li v-for="comment in comments" :key="comment.id" class="text-gray-500 text-sm">
      {{ comment.content }} - <span class="italic">{{ comment.authorEmail }}</span>
    </li>
  </ul>
</template>
