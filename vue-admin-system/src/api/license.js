import request from '@/utils/request'

export function getLicenseList(params) {
  return request({
    url: 'license/list',
    method: 'get',
    params
  })
}

export function getLicenseDetail(id) {
  return request({
    url: `license/${id}`,
    method: 'get'
  })
}

export function createLicense(data) {
  return request({
    url: 'license',
    method: 'post',
    data
  })
}

export function updateLicense(id, data) {
  return request({
    url: `license/${id}`,
    method: 'put',
    data
  })
}

export function deleteLicense(id) {
  return request({
    url: `license/${id}`,
    method: 'delete'
  })
}

export function getLicenseHistory(id) {
  return request({
    url: `license/${id}/history`,
    method: 'get'
  })
}
