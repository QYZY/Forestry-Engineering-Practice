<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import ChangeDetection from '../components/ChangeDetection.vue'
import { imageContentUrl, listImages } from '../lib/api'

const router = useRouter()

const images = ref([])
const loading = ref(false)
const error = ref('')

const baseImageId = ref('')
const targetImageId = ref('')

const baseImage = computed(() => images.value.find((x) => x.id === baseImageId.value) || null)
const targetImage = computed(() => images.value.find((x) => x.id === targetImageId.value) || null)

async function refreshList() {
  loading.value = true
  error.value = ''
  try {
    images.value = await listImages()
  } catch (e) {
    error.value = e?.message || '加载影像列表失败'
  } finally {
    loading.value = false
  }
}

function onTaskCreated(taskId) {
  router.push(`/tasks/${encodeURIComponent(taskId)}`)
}

onMounted(refreshList)
</script>

<template>
  <section class="page">
    <h2 class="h">变化检测</h2>

    <div class="card">
      <div class="bar">
        <h3 class="h3">1) 选择影像</h3>
        <button class="btn" @click="refreshList" :disabled="loading">{{ loading ? '刷新中...' : '刷新列表' }}</button>
      </div>

      <p v-if="error" class="error">{{ error }}</p>

      <div class="grid">
        <div>
          <label class="label">基准影像</label>
          <select v-model="baseImageId">
            <option value="">请选择</option>
            <option v-for="img in images" :key="img.id" :value="img.id">{{ img.name }} ({{ img.id }})</option>
          </select>
          <div v-if="baseImage" class="preview">
            <img :src="imageContentUrl(baseImage.id)" alt="base" />
          </div>
        </div>

        <div>
          <label class="label">目标影像</label>
          <select v-model="targetImageId">
            <option value="">请选择</option>
            <option v-for="img in images" :key="img.id" :value="img.id">{{ img.name }} ({{ img.id }})</option>
          </select>
          <div v-if="targetImage" class="preview">
            <img :src="imageContentUrl(targetImage.id)" alt="target" />
          </div>
        </div>
      </div>
    </div>

    <ChangeDetection :baseImageId="baseImageId" :targetImageId="targetImageId" @taskCreated="onTaskCreated" />
  </section>
</template>

<style scoped>
.page { display: grid; gap: 12px; }
.h { margin: 0; font-size: 18px; text-align: left; }
.h3 { margin: 0; font-size: 14px; color: #374151; text-align: left; }
.bar { display: flex; align-items: center; justify-content: space-between; gap: 12px; margin-bottom: 10px; }
.grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.label { display: block; margin-bottom: 6px; font-size: 13px; color: #374151; }
.preview { margin-top: 10px; border: 1px solid #e5e7eb; border-radius: 10px; overflow: hidden; background: #f9fafb; }
.preview img { width: 100%; height: auto; display: block; }
.error { color: #b91c1c; }
@media (max-width: 760px) { .grid { grid-template-columns: 1fr; } }
</style>
