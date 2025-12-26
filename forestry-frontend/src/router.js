import { createRouter, createWebHistory } from 'vue-router'

import UploadPage from './views/UploadPage.vue'
import ImagesPage from './views/ImagesPage.vue'
import ImageDetailPage from './views/ImageDetailPage.vue'
import DetectionPage from './views/DetectionPage.vue'
import TaskDetailPage from './views/TaskDetailPage.vue'

export const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/', redirect: '/upload' },
    { path: '/upload', component: UploadPage },
    { path: '/images', component: ImagesPage },
    { path: '/images/:id', component: ImageDetailPage, props: true },
    { path: '/detection', component: DetectionPage },
    { path: '/tasks/:taskId', component: TaskDetailPage, props: true },
  ],
})

export default router
