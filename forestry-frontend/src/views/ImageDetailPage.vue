<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { imageContentUrl } from '../lib/api'

const route = useRoute()
const router = useRouter()

const id = computed(() => route.params.id)
</script>

<template>
  <section class="page">
    <div class="header">
      <h2 class="h">影像详情</h2>
      <button class="btn btn-ghost" @click="router.back()">返回</button>
    </div>

    <div class="row mono">ID: {{ id }}</div>

    <div class="imgWrap">
      <img :src="imageContentUrl(id)" alt="image" />
    </div>
  </section>
</template>

<style scoped>
.page { display: grid; gap: 12px; }
.header { display: flex; justify-content: space-between; gap: 12px; align-items: center; }
.h { margin: 0; font-size: 18px; text-align: left; }
.row { text-align: left; }
/* 移除 .btn 局部配色，使用全局 .btn / .btn-ghost */
.imgWrap { border: 1px solid #e5e7eb; border-radius: 10px; overflow: hidden; background: #f9fafb; }
.imgWrap img { width: 100%; height: auto; display: block; }
.mono { font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, "Liberation Mono", "Courier New", monospace; font-size: 12px; }
</style>
