import Mock from 'mockjs'
import { businessStore } from './businessStore'

const parseUrlParams = (url) => {
  const queryString = url.split('?')[1]
  if (!queryString) return {}
  return Object.fromEntries(new URLSearchParams(queryString).entries())
}

Mock.mock(RegExp('/api/provider/list.*'), 'get', (options) => {
  const params = parseUrlParams(options.url)
  const page = Number(params.page || 1)
  const pageSize = Number(params.pageSize || 10)
  const providerName = (params.providerName || '').trim().toLowerCase()
  const country = (params.country || '').trim().toLowerCase()

  let list = businessStore.listProviders()
  if (providerName) {
    list = list.filter(item => item.providerName.toLowerCase().includes(providerName))
  }
  if (country) {
    list = list.filter(item => item.country.toLowerCase().includes(country))
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

Mock.mock('/api/provider/all', 'get', () => {
  const data = businessStore.providers.map(item => ({
    id: item.id,
    providerName: item.providerName
  }))
  return { code: 200, message: 'success', data }
})

Mock.mock(RegExp('/api/provider/\\d+/history$'), 'get', (options) => {
  const id = Number(options.url.split('/').slice(-2)[0])
  const provider = businessStore.getProviderById(id)
  return { code: 200, message: 'success', data: provider?.history || [] }
})

Mock.mock(RegExp('/api/provider/\\d+$'), 'get', (options) => {
  const id = Number(options.url.split('/').pop())
  const provider = businessStore.getProviderById(id)
  if (!provider) {
    return { code: 404, message: '供应商不存在', data: null }
  }
  const contracts = businessStore.listContracts().filter(item => item.providerId === id)
  return {
    code: 200,
    message: 'success',
    data: {
      ...provider,
      contracts
    }
  }
})

Mock.mock('/api/provider', 'post', (options) => {
  const body = JSON.parse(options.body || '{}')
  const entity = businessStore.addProvider(body)
  return { code: 200, message: 'success', data: entity }
})

Mock.mock(RegExp('/api/provider/\\d+$'), 'put', (options) => {
  const id = Number(options.url.split('/').pop())
  const body = JSON.parse(options.body || '{}')
  const entity = businessStore.updateProvider(id, body)
  if (!entity) return { code: 404, message: '供应商不存在' }
  return { code: 200, message: 'success', data: entity }
})

Mock.mock(RegExp('/api/provider/\\d+$'), 'delete', (options) => {
  const id = Number(options.url.split('/').pop())
  businessStore.removeProvider(id)
  return { code: 200, message: 'success' }
})
