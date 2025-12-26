<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import TaskMonitor from '../components/TaskMonitor.vue'
import TaskImagesAndResult from '../components/TaskImagesAndResult.vue'

const route = useRoute()
const router = useRouter()

const taskId = ref(route.params.taskId)
const task = ref(null)

function onTaskUpdate(t) {
  task.value = t
}
</script>

<template>
  <section class="page">
    <div class="header">
      <h2 class="h">任务详情</h2>
      <button class="btn btn-ghost" @click="router.push('/detection')">新建检测</button>
    </div>

    <TaskMonitor :taskId="taskId" @taskUpdate="onTaskUpdate" />

    <!-- 持续展示两张输入影像与结果 -->
    <TaskImagesAndResult :task="task" />
  </section>
</template>

<style scoped>
.page { display: grid; gap: 12px; }
.header { display: flex; justify-content: space-between; gap: 12px; align-items: center; }
.h { margin: 0; font-size: 18px; text-align: left; }
</style>
