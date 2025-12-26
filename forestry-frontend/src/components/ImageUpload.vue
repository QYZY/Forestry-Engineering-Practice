<script setup>
import { ref } from 'vue'
import { uploadImage } from '../lib/api'

const emit = defineEmits(['uploaded'])

const file = ref(null)
const name = ref('')
const remark = ref('')

const loading = ref(false)
const error = ref('')

async function onSubmit() {
  error.value = ''
  if (!file.value) {
    error.value = '请选择文件'
    return
  }

  loading.value = true
  try {
    const data = await uploadImage({
      file: file.value,
      name: name.value || undefined,
      remark: remark.value || undefined,
    })

    // 弹窗提示
    window.alert(`上传成功\nimageId: ${data?.imageId || '-'}`)

    emit('uploaded', data)

    // reset
    file.value = null
    name.value = ''
    remark.value = ''
  } catch (e) {
    error.value = e?.message || '上传失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="card">
    <h2>图像上传</h2>

    <form class="form" @submit.prevent="onSubmit">
      <label>
        文件
        <input type="file" accept="image/*" @change="(ev) => (file = ev.target.files?.[0] || null)" />
      </label>

      <label>
        名称(可选)
        <input v-model="name" type="text" placeholder="如：2025-12-航拍-1" />
      </label>

      <label>
        备注(可选)
        <input v-model="remark" type="text" placeholder="备注" />
      </label>

      <div class="row">
        <button class="btn" type="submit" :disabled="loading">
          {{ loading ? '上传中...' : '上传' }}
        </button>
        <span class="error" v-if="error">{{ error }}</span>
      </div>
    </form>
  </section>
</template>

<style scoped>
.card { padding: 16px; border: 1px solid #e5e7eb; border-radius: 10px; background: #fff; }
.form { display: grid; gap: 10px; }
label { display: grid; gap: 6px; font-size: 14px; }
input { padding: 8px 10px; border: 1px solid #d1d5db; border-radius: 8px; }
.row { display: flex; gap: 12px; align-items: center; }
/* 移除 .btn 的局部配色，使用全局 button/.btn 主题 */
.btn:disabled { opacity: 0.6; cursor: not-allowed; }
.error { color: #b91c1c; font-size: 13px; }
</style>
