const API_BASE = import.meta.env.VITE_API_BASE || '/api';

async function request(path, { method = 'GET', headers, body } = {}) {
  const res = await fetch(`${API_BASE}${path}`, {
    method,
    headers,
    body,
  });

  const data = await res.json().catch(() => null);

  if (!res.ok) {
    const msg = data?.message || `HTTP ${res.status}`;
    throw new Error(msg);
  }

  if (!data) throw new Error('Empty response');
  if (data.code !== 0) throw new Error(data.message || 'Request failed');
  return data.data;
}

export async function uploadImage({ file, name, remark }) {
  const fd = new FormData();
  fd.append('file', file);
  if (name) fd.append('name', name);
  if (remark) fd.append('remark', remark);

  return request('/image/upload', { method: 'POST', body: fd });
}

export async function listImages() {
  return request('/image/list');
}

export async function startChangeDetection({ baseImageId, targetImageId }) {
  return request('/analysis/change-detection', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ baseImageId, targetImageId }),
  });
}

export async function getTaskStatus(taskId) {
  return request(`/task/${encodeURIComponent(taskId)}/status`);
}

export function fileUrlFromPath(absPath) {
  // 后端返回的是 Windows 绝对路径；若后端未暴露静态资源，这里只能做“本地文件无法直接访问”的兜底。
  // 建议后端补充 /files/** 静态映射后再按 URL 规则拼接。
  return absPath;
}

export function imageContentUrl(imageId) {
  return `${API_BASE}/image/${encodeURIComponent(imageId)}/content`;
}

export function resultUrl(taskId, type = 'overlay') {
  return `${API_BASE}/file/result/${encodeURIComponent(taskId)}/${encodeURIComponent(type)}`
}

export function resultMaskUrl(taskId) {
  return `${API_BASE}/file/result/${encodeURIComponent(taskId)}`
}

export async function getImage(imageId) {
  return request(`/image/${encodeURIComponent(imageId)}`)
}
