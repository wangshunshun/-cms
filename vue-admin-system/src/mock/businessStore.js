const now = () => new Date().toISOString().slice(0, 19).replace('T', ' ')

const operator = 'admin'

let nextProviderId = 3
let nextContractId = 3
let nextLicenseId = 3

const platforms = ['IPTV', 'Mobile', 'SmartTV', 'Web']

const providers = [
  {
    id: 1,
    providerName: 'Global Media Group',
    country: 'USA',
    notes: '北美区域核心内容供应商',
    createTime: now(),
    history: [
      { id: 1, action: '创建', operator, time: now(), content: '创建供应商' }
    ]
  },
  {
    id: 2,
    providerName: 'Asia Vision Studio',
    country: 'Singapore',
    notes: '覆盖东南亚内容版权',
    createTime: now(),
    history: [
      { id: 1, action: '创建', operator, time: now(), content: '创建供应商' }
    ]
  }
]

const contracts = [
  {
    id: 1,
    contractName: 'GMG-2026-Main',
    startDate: '2026-01-01',
    endDate: '2026-12-31',
    providerId: 1,
    commercialRights: { IPTV: true, Mobile: true, SmartTV: false, Web: true },
    notes: '包含体育与综艺内容',
    attachmentName: 'GMG-main.pdf',
    attachmentUrl: '/mock-files/GMG-main.pdf',
    createTime: now(),
    history: [
      { id: 1, action: '创建', operator, time: now(), content: '创建合同' }
    ]
  },
  {
    id: 2,
    contractName: 'AVS-2026-Regional',
    startDate: '2026-03-01',
    endDate: '2027-02-28',
    providerId: 2,
    commercialRights: { IPTV: true, Mobile: false, SmartTV: true, Web: true },
    notes: '区域独家发行',
    attachmentName: '',
    attachmentUrl: '',
    createTime: now(),
    history: [
      { id: 1, action: '创建', operator, time: now(), content: '创建合同' }
    ]
  }
]

const licenses = [
  {
    id: 1,
    licenseName: 'GMG Sports Pack',
    contractId: 1,
    startDate: '2026-01-15',
    endDate: '2026-10-15',
    serviceType: 'SVOD',
    regions: ['US', 'CA'],
    platforms: ['IPTV', 'Mobile'],
    advertorialRights: { IPTV: true, Mobile: false },
    downloadableForMobile: true,
    downloadDuration: 7,
    previewForMobile: true,
    previewBeginTime: '08:00',
    previewEndTime: '22:00',
    notes: '移动端限部分片源下载',
    createTime: now(),
    history: [
      { id: 1, action: '创建', operator, time: now(), content: '创建许可证' }
    ]
  },
  {
    id: 2,
    licenseName: 'AVS Drama Basic',
    contractId: 2,
    startDate: '2026-04-01',
    endDate: '2026-12-31',
    serviceType: 'TVOD',
    regions: ['SG', 'MY'],
    platforms: ['SmartTV', 'Web'],
    advertorialRights: { SmartTV: true, Web: true },
    downloadableForMobile: false,
    downloadDuration: null,
    previewForMobile: false,
    previewBeginTime: '',
    previewEndTime: '',
    notes: '',
    createTime: now(),
    history: [
      { id: 1, action: '创建', operator, time: now(), content: '创建许可证' }
    ]
  }
]

const getProviderById = (id) => providers.find(item => item.id === Number(id))
const getContractById = (id) => contracts.find(item => item.id === Number(id))
const getLicenseById = (id) => licenses.find(item => item.id === Number(id))

const appendHistory = (list, action, content) => {
  list.push({
    id: list.length + 1,
    action,
    operator,
    time: now(),
    content
  })
}

const formatContract = (item) => {
  const provider = getProviderById(item.providerId)
  return {
    ...item,
    providerName: provider?.providerName || ''
  }
}

const formatLicense = (item) => {
  const contract = getContractById(item.contractId)
  return {
    ...item,
    contractName: contract?.contractName || ''
  }
}

export const businessStore = {
  platforms,
  providers,
  contracts,
  licenses,
  listProviders() {
    return providers.map(item => ({
      ...item,
      contractCount: contracts.filter(contract => contract.providerId === item.id).length
    }))
  },
  addProvider(data) {
    const entity = {
      id: nextProviderId++,
      providerName: data.providerName,
      country: data.country,
      notes: data.notes || '',
      createTime: now(),
      history: []
    }
    appendHistory(entity.history, '创建', '创建供应商')
    providers.unshift(entity)
    return entity
  },
  updateProvider(id, data) {
    const entity = getProviderById(id)
    if (!entity) return null
    entity.providerName = data.providerName
    entity.country = data.country
    entity.notes = data.notes || ''
    appendHistory(entity.history, '修改', '更新供应商基本信息')
    return entity
  },
  removeProvider(id) {
    const index = providers.findIndex(item => item.id === Number(id))
    if (index > -1) providers.splice(index, 1)
  },
  listContracts() {
    return contracts.map(formatContract)
  },
  addContract(data) {
    const entity = {
      id: nextContractId++,
      contractName: data.contractName,
      startDate: data.startDate,
      endDate: data.endDate,
      providerId: Number(data.providerId),
      commercialRights: data.commercialRights || {},
      notes: data.notes || '',
      attachmentName: data.attachmentName || '',
      attachmentUrl: data.attachmentUrl || '',
      createTime: now(),
      history: []
    }
    appendHistory(entity.history, '创建', '创建合同')
    contracts.unshift(entity)
    return formatContract(entity)
  },
  updateContract(id, data) {
    const entity = getContractById(id)
    if (!entity) return null
    entity.contractName = data.contractName
    entity.startDate = data.startDate
    entity.endDate = data.endDate
    entity.commercialRights = data.commercialRights || {}
    entity.notes = data.notes || ''
    entity.attachmentName = data.attachmentName || ''
    entity.attachmentUrl = data.attachmentUrl || ''
    appendHistory(entity.history, '修改', '更新合同基本信息')
    return formatContract(entity)
  },
  removeContract(id) {
    const contractId = Number(id)
    const index = contracts.findIndex(item => item.id === contractId)
    if (index > -1) contracts.splice(index, 1)
    for (let i = licenses.length - 1; i >= 0; i -= 1) {
      if (licenses[i].contractId === contractId) {
        licenses.splice(i, 1)
      }
    }
  },
  listLicenses() {
    return licenses.map(formatLicense)
  },
  addLicense(data) {
    const entity = {
      id: nextLicenseId++,
      licenseName: data.licenseName,
      contractId: Number(data.contractId),
      startDate: data.startDate,
      endDate: data.endDate,
      serviceType: data.serviceType,
      regions: data.regions || [],
      platforms: data.platforms || [],
      advertorialRights: data.advertorialRights || {},
      downloadableForMobile: !!data.downloadableForMobile,
      downloadDuration: data.downloadDuration || null,
      previewForMobile: !!data.previewForMobile,
      previewBeginTime: data.previewBeginTime || '',
      previewEndTime: data.previewEndTime || '',
      notes: data.notes || '',
      createTime: now(),
      history: []
    }
    appendHistory(entity.history, '创建', '创建许可证')
    licenses.unshift(entity)
    return formatLicense(entity)
  },
  updateLicense(id, data) {
    const entity = getLicenseById(id)
    if (!entity) return null
    entity.licenseName = data.licenseName
    entity.startDate = data.startDate
    entity.endDate = data.endDate
    entity.serviceType = data.serviceType
    entity.regions = data.regions || []
    entity.platforms = data.platforms || []
    entity.advertorialRights = data.advertorialRights || {}
    entity.downloadableForMobile = !!data.downloadableForMobile
    entity.downloadDuration = data.downloadDuration || null
    entity.previewForMobile = !!data.previewForMobile
    entity.previewBeginTime = data.previewBeginTime || ''
    entity.previewEndTime = data.previewEndTime || ''
    entity.notes = data.notes || ''
    appendHistory(entity.history, '修改', '更新许可证基本信息')
    return formatLicense(entity)
  },
  removeLicense(id) {
    const index = licenses.findIndex(item => item.id === Number(id))
    if (index > -1) licenses.splice(index, 1)
  },
  getProviderById,
  getContractById,
  getLicenseById,
  formatContract,
  formatLicense
}
