<script setup>
import { computed, ref, watch } from 'vue'
import { resultUrl } from '../lib/api'

const props = defineProps({
  task: { type: Object, default: null },
})

const canShow = computed(() => props.task?.status === 'success' && !!props.task?.id)

const mode = ref('overlay') // overlay | mask
const imgError = ref('')

const imgSrc = computed(() => {
  if (!props.task?.id) return ''
  // 用 query 破缓存，避免切换时浏览器复用旧图
  return `${resultUrl(props.task.id, mode.value)}?t=${Date.now()}`
})

watch(() => mode.value, () => { imgError.value = '' })

function onImgError() {
  imgError.value = mode.value === 'overlay' ? '叠加结果加载失败' : 'mask 加载失败'
}
</script>

<template>
  <section class="card">
    <h2>变化检测结果展示</h2>

    <div v-if="!task" class="empty">暂无任务</div>

    <div v-else-if="!canShow" class="empty">
      结果尚不可用（需要任务 success）。当前状态：<span class="mono">{{ task.status }}</span>
    </div>

    <div v-else class="panel">
      <div class="toolbar">
        <button class="btn btn-ghost" :class="{ active: mode === 'overlay' }" @click="mode = 'overlay'">叠加效果</button>
        <button class="btn btn-ghost" :class="{ active: mode === 'mask' }" @click="mode = 'mask'">二值Mask</button>
      </div>

      <p v-if="imgError" class="error mono">{{ imgError }}</p>

      <div class="imgWrap">
        <img :src="imgSrc" :alt="mode" @error="onImgError" />
      </div>
    </div>
  </section>
</template>

<style scoped>
.card { padding: 16px; border: 1px solid #e5e7eb; border-radius: 10px; background: #fff; }
.empty { color: #6b7280; }
.panel { margin-top: 10px; display: grid; gap: 8px; }
.mono { font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace; font-size: 12px; }
.toolbar { display: flex; gap: 10px; flex-wrap: wrap; }
.btn-ghost.active { background: rgba(46, 91, 255, 0.12); border-color: rgba(46, 91, 255, 0.35); }
.imgWrap { margin-top: 10px; border: 1px solid #e5e7eb; border-radius: 10px; overflow: hidden; background: #f9fafb; }
.imgWrap img { width: 100%; height: auto; display: block; }
.error { color: #b91c1c; }
</style>
