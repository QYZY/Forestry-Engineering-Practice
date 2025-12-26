# 林业变化检测系统（前后端）技术文档

> 适用范围：`nefu/practice` 项目。后端 Spring Boot + JPA + MySQL；前端 Vue3 + Vite + Vue Router。

## 1. 项目概述

本系统提供遥感/航拍影像的上传、管理，并基于两期影像执行像素级变化检测，生成变化区域结果图（支持二值 Mask 与叠加可视化 Overlay 两种展示）。

主要能力：
- 影像上传与管理（列表、详情预览）
- 变化检测任务创建（异步处理）
- 任务进度/状态查询（轮询监控）
- 结果展示（Overlay/Mask 两种结果切换）

系统采用前后端分离：
- 后端提供 REST API（统一返回结构 `Result<T>`）
- 前端通过 `/api` 代理调用后端接口

---

## 2. 技术栈与依赖

### 2.1 后端
- Java：JDK 17/11（以项目构建环境为准）
- Spring Boot：Web、Data JPA
- 数据库：MySQL
- ORM：Hibernate (JPA)
- 文件上传：Spring Multipart
- 异步任务：`ExecutorService` 线程池

### 2.2 前端
- Vue 3
- Vite
- Vue Router
- Fetch API（未引入 Axios）

---

## 3. 系统架构

### 3.1 模块划分

后端：
- Controller 层：对外 REST API
  - `ImageController`：影像上传/列表/详情/二进制读取
  - `AnalysisController`：启动变化检测
  - `TaskController`：任务状态查询
  - `FileController`：结果文件读取（overlay/mask）
- Service 层：业务逻辑
  - `ImageService`：影像文件保存、元数据入库、列表排序
  - `TaskService`：任务创建、状态流转、进度更新
  - `ChangeDetectionService`：创建任务 + 异步提交 worker
  - `ChangeDetectionWorker`：执行检测流程
- Util 层：算法工具
  - `ImageDiffUtil`：像素差分 + 形态学后处理 + 生成 overlay/mask

前端：
- Views（页面）
  - `/upload`：影像上传
  - `/images`：影像管理（列表）
  - `/images/:id`：影像详情
  - `/detection`：变化检测（选择基准/目标 + 启动任务）
  - `/tasks/:taskId`：任务详情（监控 + 两图+结果）
- Components（组件）
  - `ImageUpload`、`ImageManager`
  - `ChangeDetection`
  - `TaskMonitor`
  - `TaskImagesAndResult`
  - `ResultViewer`（overlay/mask 切换）

### 3.2 关键流程

#### 3.2.1 上传影像
1. 前端上传文件 + 可选 name/remark
2. 后端保存到 `file.image-dir` 目录
3. 写入数据库 `image` 表
4. 返回 `imageId`

#### 3.2.2 变化检测
1. 前端选择两张影像（base/target），调用创建检测
2. 后端创建任务记录 `task`（status=waiting, progress=0）
3. 后端线程池异步执行 worker
4. worker 更新 `progress/status`，生成结果文件
5. 任务完成，status=success，写入 `resultPath`

#### 3.2.3 结果查看
前端任务详情页：
- 轮询任务状态
- 连续显示 base/target 两张图
- 支持切换展示：
  - overlay：叠加效果图（目标图 + 半透明红色变化区）
  - mask：二值 mask（BMP 或 PNG）

---

## 4. 目录结构

```
practice/
  src/main/java/com/nefu/practice/
    controller/        # REST API
    serivce/           # 业务服务
    util/              # 算法工具
    entity/            # JPA 实体
    repository/        # JPA Repository
    pojo/              # DTO
  src/main/resources/
    application.yml
  forestry-frontend/
    src/
      views/           # 页面
      components/      # 组件
      lib/api.js       # API 封装
      router.js
      style.css
```

---

## 5. 数据模型

### 5.1 Image（影像）
表：`image`
- `id`：String（UUID）
- `name`：String
- `filePath`：String（服务器本地路径）
- `fileSize`：Long
- `uploadTime`：LocalDateTime
- `remark`：String

### 5.2 Task（任务）
表：`task`
- `id`：String（UUID）
- `baseImageId`：String
- `targetImageId`：String
- `status`：String（waiting/running/success/error）
- `progress`：Integer（0~100）
- `resultPath`：String（结果文件绝对路径）
- `createTime`：LocalDateTime
- `finishTime`：LocalDateTime

---

## 6. 后端接口文档

### 6.1 统一返回结构
`Result<T>`：
- `code`：0 成功，-1 失败
- `message`：描述
- `data`：业务数据

### 6.2 影像接口

#### 6.2.1 上传影像
- `POST /api/image/upload`
- form-data：
  - `file`：文件（必填）
  - `name`：String（可选）
  - `remark`：String（可选）
- 返回：`{ imageId }`

#### 6.2.2 影像列表（按 uploadTime 倒序）
- `GET /api/image/list`
- 返回：`ImageResponse[]`
  - `id/name/uploadTime/filePath/remark`

#### 6.2.3 影像元数据
- `GET /api/image/{imageId}`

#### 6.2.4 影像内容（二进制）
- `GET /api/image/{imageId}/content`
- 返回：图片流（image/png|image/jpeg|image/bmp...）

### 6.3 变化检测

#### 6.3.1 启动变化检测任务
- `POST /api/analysis/change-detection`
- JSON：`{ baseImageId, targetImageId }`
- 返回：`{ taskId }`

### 6.4 任务监控

#### 6.4.1 查询任务状态
- `GET /api/task/{taskId}/status`
- 返回 Task 实体（含 progress/status/resultPath 等）

### 6.5 结果文件读取

- `GET /api/file/result/{taskId}`
  - 默认：优先 overlay，失败回退 mask
- `GET /api/file/result/{taskId}/overlay`
  - 读取：`{taskId}_overlay.png`
- `GET /api/file/result/{taskId}/mask`
  - 读取：`{taskId}_mask.bmp`（优先）或 `{taskId}_mask.png`

---

## 7. 变化检测算法说明

### 7.1 输入约束
- 当前实现按注释为 BMP-only
- 要求 base/target 两张影像尺寸一致

### 7.2 核心算法
- 像素级 RGB 差分：
  - `diff = |r1-r2| + |g1-g2| + |b1-b2|`
  - `diff > DIFF_THRESHOLD` 判定为变化

### 7.3 后处理（美观优化）
- 对二值结果进行形态学闭运算：
  - 膨胀（连接碎片）
  - 腐蚀（去除毛刺）
- 输出：
  - `*_mask.bmp`：二值灰度
  - `*_overlay.png`：目标图底图 + 红色半透明变化区

参数：
- `DIFF_THRESHOLD=40`
- `MORPH_RADIUS=1`
- `OVERLAY_ALPHA=0.35`

---

## 8. 前端页面与交互

### 8.1 页面路由
- `/upload`：上传
- `/images`：影像管理
- `/images/:id`：影像详情
- `/detection`：变化检测
- `/tasks/:taskId`：任务详情

### 8.2 关键组件
- `ImageUpload`：上传成功弹窗提示（alert），并触发刷新
- `ImageManager`：列表展示（预览、上传时间、备注）
- `TaskMonitor`：轮询任务状态
- `ResultViewer`：overlay/mask 切换

---

## 9. 配置说明

`src/main/resources/application.yml`：
- `spring.datasource.*`：MySQL 连接
- `spring.servlet.multipart.*`：上传大小限制
- `file.image-dir`：图片保存目录
- `file.result-dir`：结果保存目录

前端：
- `vite.config.js`：开发代理 `/api -> http://localhost:8080`
- `VITE_API_BASE`：可覆盖 API 前缀（默认 `/api`）

---

## 10. 运行与部署

### 10.1 本地开发
- 启动后端：Spring Boot（默认 8080）
- 启动前端：Vite dev server（默认 5173），通过代理访问后端

### 10.2 生产部署建议
- 前端构建产物可由 Nginx/后端静态资源托管
- 推荐在后端增加统一静态资源服务或 Nginx 映射目录（images/results）
- 注意 Windows 路径与 Linux 路径差异

---

## 11. 常见问题排查

### 11.1 结果图不显示
- 检查任务状态是否 success
- 检查结果文件是否存在：
  - overlay：`{taskId}_overlay.png`
  - mask：`{taskId}_mask.bmp`
- 直接访问：
  - `/api/file/result/{taskId}/overlay`
  - `/api/file/result/{taskId}/mask`

### 11.2 上传时间/备注为空
- 确认后端 `ImageResponse` 已包含 `remark`，并且 `uploadTime` 已转字符串
- 确认数据库 `image.upload_time` / `image.remark` 字段有值

---

## 12. 后续可扩展项

- 支持更多格式（PNG/JPG）与自动重采样/配准
- 增加任务列表/历史记录接口
- 结果统计：变化面积、变化比例、矢量化边界
- 前端 overlay 与目标图的透明度调节、对比滑块

