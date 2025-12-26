<script setup>
import { useRouter } from 'vue-router'
import ImageManager from '../components/ImageManager.vue'

const router = useRouter()

function openDetail(id) {
  router.push(`/images/${encodeURIComponent(id)}`)
}
</script>

<template>
  <section class="page">
    <div class="header">
      <h2 class="h">影像管理</h2>
    </div>

    <ImageManager @openDetail="openDetail" />
  </section>
</template>

<style scoped>
.page { display: grid; gap: 12px; }
.header { display: flex; justify-content: space-between; gap: 12px; align-items: baseline; }
.h { margin: 0; font-size: 18px; text-align: left; }
</style>
