import Mock from 'mockjs'
import { businessStore } from './businessStore'

const parseUrlParams = (url) => {
  const queryString = url.split('?')[1]
  if (!queryString) return {}
  return Object.fromEntries(new URLSearchParams(queryString).entries())
}

Mock.mock(RegExp('/api/contract/list.*'), 'get', (options) => {
  const params = parseUrlParams(options.url)
  const page = Number(params.page || 1)
  const pageSize = Number(params.pageSize || 10)
  const contractName = (params.contractName || '').trim().toLowerCase()
  const providerId = params.providerId ? Number(params.providerId) : null

  let list = businessStore.listContracts()
  if (contractName) {
    list = list.filter(item => item.contractName.toLowerCase().includes(contractName))
  }
  if (providerId) {
    list = list.filter(item => item.providerId === providerId)
  }

  const total = list.length
  const start = (page - 1) * pageSize
  const pageList = list.slice(start, start + pageSize)

  return {
    code: 200,
    message: 'success',
    data: {
      list: pageList,
      total
    }
  }
})

Mock.mock(RegExp('/api/contract/\\d+/history$'), 'get', (options) => {
  const id = Number(options.url.split('/').slice(-2)[0])
  const contract = businessStore.getContractById(id)
  return { code: 200, message: 'success', data: contract?.history || [] }
})

Mock.mock(RegExp('/api/contract/\\d+$'), 'get', (options) => {
  const id = Number(options.url.split('/').pop())
  const contract = businessStore.getContractById(id)
  if (!contract) return { code: 404, message: '合同不存在', data: null }
  const licenses = businessStore.listLicenses().filter(item => item.contractId === id)
  return {
    code: 200,
    message: 'success',
    data: {
      ...businessStore.formatContract(contract),
      licenses
    }
  }
})

Mock.mock('/api/contract', 'post', (options) => {
  const body = JSON.parse(options.body || '{}')
  const entity = businessStore.addContract(body)
  return { code: 200, message: 'success', data: entity }
})

Mock.mock(RegExp('/api/contract/\\d+$'), 'put', (options) => {
  const id = Number(options.url.split('/').pop())
  const body = JSON.parse(options.body || '{}')
  const entity = businessStore.updateContract(id, body)
  if (!entity) return { code: 404, message: '合同不存在' }
  return { code: 200, message: 'success', data: entity }
})

Mock.mock(RegExp('/api/contract/\\d+$'), 'delete', (options) => {
  const id = Number(options.url.split('/').pop())
  businessStore.removeContract(id)
  return { code: 200, message: 'success' }
})
