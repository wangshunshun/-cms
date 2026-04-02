import Mock from 'mockjs'
import { businessStore } from './businessStore'

const parseUrlParams = (url) => {
  const queryString = url.split('?')[1]
  if (!queryString) return {}
  return Object.fromEntries(new URLSearchParams(queryString).entries())
}

Mock.mock(RegExp('/api/license/list.*'), 'get', (options) => {
  const params = parseUrlParams(options.url)
  const page = Number(params.page || 1)
  const pageSize = Number(params.pageSize || 10)
  const licenseName = (params.licenseName || '').trim().toLowerCase()
  const contractId = params.contractId ? Number(params.contractId) : null

  let list = businessStore.listLicenses()
  if (licenseName) {
    list = list.filter(item => item.licenseName.toLowerCase().includes(licenseName))
  }
  if (contractId) {
    list = list.filter(item => item.contractId === contractId)
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

Mock.mock(RegExp('/api/license/\\d+/history$'), 'get', (options) => {
  const id = Number(options.url.split('/').slice(-2)[0])
  const license = businessStore.getLicenseById(id)
  return { code: 200, message: 'success', data: license?.history || [] }
})

Mock.mock(RegExp('/api/license/\\d+$'), 'get', (options) => {
  const id = Number(options.url.split('/').pop())
  const license = businessStore.getLicenseById(id)
  if (!license) return { code: 404, message: '许可证不存在', data: null }
  return {
    code: 200,
    message: 'success',
    data: businessStore.formatLicense(license)
  }
})

Mock.mock('/api/license', 'post', (options) => {
  const body = JSON.parse(options.body || '{}')
  const entity = businessStore.addLicense(body)
  return { code: 200, message: 'success', data: entity }
})

Mock.mock(RegExp('/api/license/\\d+$'), 'put', (options) => {
  const id = Number(options.url.split('/').pop())
  const body = JSON.parse(options.body || '{}')
  const entity = businessStore.updateLicense(id, body)
  if (!entity) return { code: 404, message: '许可证不存在' }
  return { code: 200, message: 'success', data: entity }
})

Mock.mock(RegExp('/api/license/\\d+$'), 'delete', (options) => {
  const id = Number(options.url.split('/').pop())
  businessStore.removeLicense(id)
  return { code: 200, message: 'success' }
})
