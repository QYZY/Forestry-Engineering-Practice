<script setup>
import { onMounted, ref } from 'vue'
import { imageContentUrl, listImages } from '../lib/api'

const emit = defineEmits(['openDetail'])

const images = ref([])
const loading = ref(false)
const error = ref('')

async function refresh() {
  loading.value = true
  error.value = ''
  try {
    images.value = await listImages()
  } catch (e) {
    error.value = e?.message || '加载失败'
  } finally {
    loading.value = false
  }
}

onMounted(refresh)

defineExpose({ refresh })
</script>

<template>
  <section class="card">
    <div class="header">
      <h2>图像展示/管理</h2>
      <button class="btn" @click="refresh" :disabled="loading">{{ loading ? '刷新中...' : '刷新' }}</button>
    </div>

    <p v-if="error" class="error">{{ error }}</p>

    <div v-if="!loading && images.length === 0" class="empty">暂无图像，请先上传</div>

    <div class="tableWrap" v-if="images.length">
      <table class="table">
        <thead>
          <tr>
            <th>预览</th>
            <th>ID</th>
            <th>名称</th>
            <th>上传时间</th>
            <th>备注</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="img in images" :key="img.id">
            <td>
              <div class="thumb" role="button" tabindex="0" @click="emit('openDetail', img.id)">
                <img :src="imageContentUrl(img.id)" alt="preview" loading="lazy" />
              </div>
            </td>
            <td class="mono">{{ img.id }}</td>
            <td>{{ img.name }}</td>
            <td class="mono">{{ img.uploadTime || '-' }}</td>
            <td>{{ img.remark || '-' }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </section>
</template>

<style scoped>
.card { padding: 16px; border: 1px solid #e5e7eb; border-radius: 10px; background: #fff; }
.header { display: flex; align-items: center; justify-content: space-between; gap: 12px; }
.btn:disabled { opacity: 0.6; cursor: not-allowed; }
.error { color: #b91c1c; }
.empty { color: #6b7280; padding: 10px 0; }
.tableWrap { overflow: auto; }
.table { width: 100%; border-collapse: collapse; margin-top: 10px; }
th, td { border-bottom: 1px solid #e5e7eb; text-align: left; padding: 8px; font-size: 14px; }
.mono { font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace; font-size: 12px; }
.thumb { width: 84px; height: 56px; border: 1px solid #e5e7eb; border-radius: 8px; overflow: hidden; background: #f3f4f6; display: grid; place-items: center; cursor: pointer; }
.thumb img { width: 100%; height: 100%; object-fit: cover; display: block; }
</style>
