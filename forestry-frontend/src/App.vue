<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div class="app">
    <header class="top">
      <div class="brand">
        <h1 class="title">林业变化检测</h1>
        <div class="sub">模块：影像上传 / 影像管理 / 变化检测 / 任务详情</div>
      </div>

      <nav class="nav">
        <RouterLink class="navItem" to="/upload">影像上传</RouterLink>
        <RouterLink class="navItem" to="/images">影像管理</RouterLink>
        <RouterLink class="navItem" to="/detection">变化检测</RouterLink>
      </nav>
    </header>

    <main class="main">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.app { width: min(1100px, 96vw); margin: 0 auto; padding: 18px 0 28px; }
.top { display: flex; justify-content: space-between; gap: 16px; align-items: flex-end; }
.brand { display: grid; gap: 6px; }
.title { margin: 0; font-size: 22px; text-align: left; }
.sub { color: #6b7280; font-size: 13px; text-align: left; }
.nav { display: flex; gap: 10px; flex-wrap: wrap; }
.navItem { padding: 8px 10px; border: 1px solid #e5e7eb; border-radius: 10px; color: inherit; text-decoration: none; }
.navItem.router-link-active { border-color: #111827; }
.main { margin-top: 14px; }
</style>
