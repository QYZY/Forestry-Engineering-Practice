<script setup>
import ImageUpload from '../components/ImageUpload.vue'
</script>

<template>
  <section class="page">
    <h2 class="h">影像上传</h2>
    <ImageUpload />
  </section>
</template>

<style scoped>
.page { display: grid; gap: 12px; }
.h { margin: 0; font-size: 18px; text-align: left; }
</style>

