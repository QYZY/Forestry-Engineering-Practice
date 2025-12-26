<script setup>
import { computed, ref, watch } from 'vue'
import { getImage, imageContentUrl } from '../lib/api'
import ResultViewer from './ResultViewer.vue'

const props = defineProps({
  task: { type: Object, default: null },
})

const baseImage = ref(null)
const targetImage = ref(null)
const error = ref('')

const baseId = computed(() => props.task?.baseImageId || '')
const targetId = computed(() => props.task?.targetImageId || '')

async function load() {
  error.value = ''
  baseImage.value = null
  targetImage.value = null

  try {
    if (baseId.value) baseImage.value = await getImage(baseId.value)
    if (targetId.value) targetImage.value = await getImage(targetId.value)
  } catch (e) {
    error.value = e?.message || '加载影像信息失败'
  }
}

watch(() => [baseId.value, targetId.value], load, { immediate: true })
</script>

<template>
  <section class="card">
    <h2>输入影像与检测结果</h2>

    <p v-if="error" class="error">{{ error }}</p>

    <div class="grid">
      <div class="panel">
        <div class="title">基准影像</div>
        <div v-if="!baseId" class="empty">无</div>
        <template v-else>
          <div class="meta mono">{{ baseId }}</div>
          <div class="meta">{{ baseImage?.name || '-' }}</div>
          <div class="imgWrap">
            <img :src="imageContentUrl(baseId)" alt="base" />
          </div>
        </template>
      </div>

      <div class="panel">
        <div class="title">目标影像</div>
        <div v-if="!targetId" class="empty">无</div>
        <template v-else>
          <div class="meta mono">{{ targetId }}</div>
          <div class="meta">{{ targetImage?.name || '-' }}</div>
          <div class="imgWrap">
            <img :src="imageContentUrl(targetId)" alt="target" />
          </div>
        </template>
      </div>
    </div>

    <div class="result">
      <ResultViewer :task="task" />
    </div>
  </section>
</template>

<style scoped>
.grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.panel { border: 1px solid #e5e7eb; border-radius: 12px; padding: 12px; background: #fff; }
.title { font-weight: 700; margin-bottom: 8px; }
.meta { font-size: 13px; color: #374151; }
.mono { font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace; font-size: 12px; }
.imgWrap { margin-top: 10px; border: 1px solid #e5e7eb; border-radius: 10px; overflow: hidden; background: #f9fafb; }
.imgWrap img { width: 100%; height: auto; display: block; }
.result { margin-top: 12px; }
.empty { color: #6b7280; }
.error { color: #b91c1c; }
@media (max-width: 900px) { .grid { grid-template-columns: 1fr; } }
</style>

