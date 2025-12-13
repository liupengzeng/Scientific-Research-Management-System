import request from '@/utils/request'

export function listProjectTypes(params) {
  return request({
    url: '/research/projectType/list',
    method: 'get',
    params
  })
}

export function getProjectType(typeId) {
  return request({
    url: `/research/projectType/${typeId}`,
    method: 'get'
  })
}

export function addProjectType(data) {
  return request({
    url: '/research/projectType',
    method: 'post',
    data
  })
}

export function updateProjectType(data) {
  return request({
    url: '/research/projectType',
    method: 'put',
    data
  })
}

export function deleteProjectTypes(typeIds) {
  return request({
    url: `/research/projectType/${typeIds}`,
    method: 'delete'
  })
}

