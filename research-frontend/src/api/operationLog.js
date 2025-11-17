import request from '@/utils/request'

export function getOperationLogList(params) {
  return request({
    url: '/system/operationLog/list',
    method: 'get',
    params
  })
}

export function getOperationLogDetail(logId) {
  return request({
    url: `/system/operationLog/${logId}`,
    method: 'get'
  })
}

export function deleteOperationLogs(logIds) {
  return request({
    url: `/system/operationLog/${logIds}`,
    method: 'delete'
  })
}