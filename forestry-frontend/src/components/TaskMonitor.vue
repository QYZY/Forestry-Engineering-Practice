<script setup>
import { computed, onBeforeUnmount, ref, watch } from 'vue'
import { getTaskStatus } from '../lib/api'

const props = defineProps({
  taskId: { type: String, default: '' },
  pollIntervalMs: { type: Number, default: 1000 },
})

const emit = defineEmits(['taskUpdate', 'taskFinished'])

const task = ref(null)
const loading = ref(false)
const error = ref('')

let timer = null

const isFinished = computed(() => {
  const s = task.value?.status
  return s === 'success' || s === 'error'
})

async function fetchOnce() {
  if (!props.taskId) return
  loading.value = true
  error.value = ''
  try {
    const data = await getTaskStatus(props.taskId)
    task.value = data
    emit('taskUpdate', data)
    if (data?.status === 'success' || data?.status === 'error') {
      emit('taskFinished', data)
      stop()
    }
  } catch (e) {
    error.value = e?.message || '查询失败'
  } finally {
    loading.value = false
  }
}

function start() {
  stop()
  fetchOnce()
  timer = setInterval(fetchOnce, props.pollIntervalMs)
}

function stop() {
  if (timer) {
    clearInterval(timer)
    timer = null
  }
}

watch(
  () => props.taskId,
  (id) => {
    task.value = null
    error.value = ''
    if (id) start()
    else stop()
  },
  { immediate: true }
)

onBeforeUnmount(stop)

defineExpose({ refresh: fetchOnce, stop })
</script>

<template>
  <section class="card">
    <div class="header">
      <h2>任务监控</h2>
      <button v-if="taskId" class="btn" @click="fetchOnce" :disabled="loading">{{ loading ? '刷新中...' : '手动刷新' }}</button>
    </div>

    <div v-if="!taskId" class="empty">尚未创建任务</div>

    <p v-if="error" class="error">{{ error }}</p>

    <div v-if="task" class="panel">
      <div class="row"><span class="k">任务ID</span><span class="v mono">{{ task.id }}</span></div>
      <div class="row"><span class="k">状态</span><span class="v">{{ task.status }}</span></div>
      <div class="row"><span class="k">进度</span><span class="v">{{ task.progress }}%</span></div>
      <div class="row"><span class="k">结果路径</span><span class="v mono">{{ task.resultPath || '-' }}</span></div>
      <div class="row"><span class="k">创建时间</span><span class="v mono">{{ task.createTime || '-' }}</span></div>
      <div class="row"><span class="k">完成时间</span><span class="v mono">{{ task.finishTime || '-' }}</span></div>

      <div class="bar">
        <div class="barInner" :style="{ width: (task.progress || 0) + '%' }"></div>
      </div>

      <div class="done" v-if="isFinished">
        {{ task.status === 'success' ? '任务已完成' : '任务失败' }}
      </div>
    </div>
  </section>
</template>

<style scoped>
.card { padding: 16px; border: 1px solid #e5e7eb; border-radius: 10px; background: #fff; }
.header { display: flex; align-items: center; justify-content: space-between; gap: 12px; }
/* 移除 .btn 的局部配色，使用全局 button/.btn 主题 */
.btn:disabled { opacity: 0.6; cursor: not-allowed; }
.empty { color: #6b7280; padding: 10px 0; }
.error { color: #b91c1c; }
.panel { margin-top: 10px; display: grid; gap: 8px; }
.row { display: grid; grid-template-columns: 90px 1fr; gap: 10px; align-items: start; }
.k { color: #6b7280; font-size: 12px; }
.v { font-size: 14px; }
.mono { font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace; font-size: 12px; }
.bar { height: 10px; background: #e5e7eb; border-radius: 999px; overflow: hidden; }
.barInner { height: 100%; background: #111827; }
.done { margin-top: 8px; color: #065f46; }
</style>
