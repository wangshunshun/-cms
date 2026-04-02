import request from '@/utils/request'

// 获取操作日志列表
export function getLogList(params) {
  return request({
    url: 'log/list',
    method: 'get',
    params
  })
}

// 获取日志详情
export function getLogDetail(id) {
  return request({
    url: `log/${id}`,
    method: 'get'
  })
}

// 删除日志
export function deleteLog(id) {
  return request({
    url: `log/${id}`,
    method: 'delete'
  })
}

// 清空日志
export function clearLogs() {
  return request({
    url: 'log/clear',
    method: 'delete'
  })
}

// 导出日志
export function exportLogs(params) {
  return request({
    url: 'log/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
