import request from '@/utils/request'

export function listProjects(params) {
  return request({
    url: '/research/project/list',
    method: 'get',
    params
  })
}

export function getProject(projectId) {
  return request({
    url: `/research/project/${projectId}`,
    method: 'get'
  })
}

export function addProject(data) {
  return request({
    url: '/research/project',
    method: 'post',
    data
  })
}

export function updateProject(data) {
  return request({
    url: '/research/project',
    method: 'put',
    data
  })
}

export function submitProject(projectId) {
  return request({
    url: `/research/project/submit/${projectId}`,
    method: 'post'
  })
}

export function deleteProjects(projectIds) {
  return request({
    url: `/research/project/${projectIds}`,
    method: 'delete'
  })
}

/**
 * 项目启动（立项后进入进行中）
 * @param {Number} projectId - 项目ID
 */
export function startProject(projectId) {
  return request({
    url: `/research/project/start/${projectId}`,
    method: 'post'
  })
}

/**
 * 上传文件到MinIO
 * @param {FormData} formData - 文件表单数据
 */
export function uploadFile(formData) {
  return request({
    url: '/research/project/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

