import request from '@/utils/request'

export function getContractList(params) {
  return request({
    url: 'contract/list',
    method: 'get',
    params
  })
}

export function getContractDetail(id) {
  return request({
    url: `contract/${id}`,
    method: 'get'
  })
}

export function createContract(data) {
  return request({
    url: 'contract',
    method: 'post',
    data
  })
}

export function updateContract(id, data) {
  return request({
    url: `contract/${id}`,
    method: 'put',
    data
  })
}

export function deleteContract(id) {
  return request({
    url: `contract/${id}`,
    method: 'delete'
  })
}

export function getContractHistory(id) {
  return request({
    url: `contract/${id}/history`,
    method: 'get'
  })
}

export function uploadContractAttachment(data) {
  return request({
    url: 'contract/attachment',
    method: 'post',
    data
  })
}
