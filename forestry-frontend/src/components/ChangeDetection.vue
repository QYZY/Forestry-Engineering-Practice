<script setup>
import { computed, ref } from 'vue'
import { startChangeDetection } from '../lib/api'

const props = defineProps({
  baseImageId: { type: String, default: '' },
  targetImageId: { type: String, default: '' },
})

const emit = defineEmits(['taskCreated'])

const loading = ref(false)
const error = ref('')

const disabled = computed(() => !props.baseImageId || !props.targetImageId || props.baseImageId === props.targetImageId)

async function start() {
  error.value = ''
  if (disabled.value) {
    error.value = '请选择不同的基准/目标图像'
    return
  }

  loading.value = true
  try {
    const data = await startChangeDetection({ baseImageId: props.baseImageId, targetImageId: props.targetImageId })
    emit('taskCreated', data.taskId)
  } catch (e) {
    error.value = e?.message || '启动失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="card">
    <h2>变化检测</h2>

    <div class="grid">
      <div class="item">
        <div class="label">基准图像ID</div>
        <div class="mono">{{ baseImageId || '-' }}</div>
      </div>
      <div class="item">
        <div class="label">目标图像ID</div>
        <div class="mono">{{ targetImageId || '-' }}</div>
      </div>
    </div>

    <div class="row">
      <button class="btn btn-success" @click="start" :disabled="loading || disabled">
        {{ loading ? '启动中...' : '启动检测任务' }}
      </button>
      <span class="error" v-if="error">{{ error }}</span>
    </div>
  </section>
</template>

<style scoped>
.card { padding: 16px; border: 1px solid #e5e7eb; border-radius: 10px; background: #fff; }
.grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; margin: 10px 0; }
.item { padding: 10px; border: 1px dashed #d1d5db; border-radius: 10px; }
.label { font-size: 12px; color: #6b7280; }
.mono { font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace; font-size: 12px; }
.row { display: flex; gap: 12px; align-items: center; }
/* 移除 .btn 的局部配色，使用全局 button/.btn + .btn-success */
.btn:disabled { opacity: 0.6; cursor: not-allowed; }
.error { color: #b91c1c; font-size: 13px; }
@media (max-width: 760px) { .grid { grid-template-columns: 1fr; } }
</style>
