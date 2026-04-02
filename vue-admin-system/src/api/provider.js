import request from '@/utils/request'

export function getProviderList(params) {
  return request({
    url: 'provider/list',
    method: 'get',
    params
  })
}

export function getAllProviders() {
  return request({
    url: 'provider/all',
    method: 'get'
  })
}

export function getProviderDetail(id) {
  return request({
    url: `provider/${id}`,
    method: 'get'
  })
}

export function createProvider(data) {
  return request({
    url: 'provider',
    method: 'post',
    data
  })
}

export function updateProvider(id, data) {
  return request({
    url: `provider/${id}`,
    method: 'put',
    data
  })
}

export function deleteProvider(id) {
  return request({
    url: `provider/${id}`,
    method: 'delete'
  })
}

export function getProviderHistory(id) {
  return request({
    url: `provider/${id}/history`,
    method: 'get'
  })
}
