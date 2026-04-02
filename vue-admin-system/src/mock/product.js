import Mock from 'mockjs'

let productList = [
  {
    id: 1,
    name: '无线蓝牙耳机',
    description: '主动降噪，40 小时续航',
    price: 399.0,
    stock: 120,
    status: 1,
    image: '',
    createdAt: '2026-03-20T10:30:00'
  },
  {
    id: 2,
    name: '机械键盘',
    description: '87 键，热插拔，RGB 背光',
    price: 299.0,
    stock: 80,
    status: 1,
    image: '',
    createdAt: '2026-03-18T15:20:00'
  },
  {
    id: 3,
    name: '便携显示器',
    description: '15.6 英寸，1080P，Type-C 供电',
    price: 899.0,
    stock: 35,
    status: 0,
    image: '',
    createdAt: '2026-03-12T09:00:00'
  }
]

const parseUrlParams = (url) => {
  const queryString = url.split('?')[1]
  if (!queryString) return {}
  return Object.fromEntries(new URLSearchParams(queryString).entries())
}

// 商品列表
Mock.mock(RegExp('/api/products.*'), 'get', (options) => {
  const params = parseUrlParams(options.url)
  const page = Number(params.page || 1)
  const pageSize = Number(params.pageSize || params.page_size || 10)
  const name = (params.name || '').trim()
  const status = params.status === '' || params.status == null ? null : Number(params.status)
  const sortField = params.sort_field
  const sortOrder = params.sort_order

  let list = [...productList]

  if (name) {
    list = list.filter(item => item.name.includes(name))
  }
  if (status !== null && !Number.isNaN(status)) {
    list = list.filter(item => item.status === status)
  }

  if (sortField && sortOrder) {
    const factor = sortOrder === 'ascending' ? 1 : -1
    list.sort((a, b) => {
      const aValue = a[sortField]
      const bValue = b[sortField]
      if (aValue > bValue) return factor
      if (aValue < bValue) return -factor
      return 0
    })
  }

  const total = list.length
  const start = (page - 1) * pageSize
  const items = list.slice(start, start + pageSize)

  return {
    code: 200,
    message: 'success',
    data: {
      items,
      total
    }
  }
})

// 商品详情
Mock.mock(RegExp('/api/products/\\d+$'), 'get', (options) => {
  const id = Number(options.url.split('/').pop())
  const item = productList.find(product => product.id === id)

  return {
    code: item ? 200 : 404,
    message: item ? 'success' : '商品不存在',
    data: item || null
  }
})

// 新增商品
Mock.mock('/api/products', 'post', (options) => {
  const body = JSON.parse(options.body || '{}')
  const nextId = productList.length ? Math.max(...productList.map(item => item.id)) + 1 : 1
  const now = new Date().toISOString()

  const newProduct = {
    id: nextId,
    name: body.name || '',
    description: body.description || '',
    price: Number(body.price || 0),
    stock: Number(body.stock || 0),
    status: body.status ?? 1,
    image: body.image || '',
    createdAt: now
  }

  productList.unshift(newProduct)

  return {
    code: 200,
    message: 'success',
    data: newProduct
  }
})

// 修改商品
Mock.mock(RegExp('/api/products/\\d+$'), 'put', (options) => {
  const id = Number(options.url.split('/').pop())
  const body = JSON.parse(options.body || '{}')
  const index = productList.findIndex(item => item.id === id)

  if (index === -1) {
    return { code: 404, message: '商品不存在' }
  }

  productList[index] = {
    ...productList[index],
    ...body,
    id,
    price: Number(body.price ?? productList[index].price),
    stock: Number(body.stock ?? productList[index].stock)
  }

  return {
    code: 200,
    message: 'success',
    data: productList[index]
  }
})

// 修改状态
Mock.mock(RegExp('/api/products/\\d+/status$'), 'put', (options) => {
  const id = Number(options.url.split('/').slice(-2)[0])
  const body = JSON.parse(options.body || '{}')
  const index = productList.findIndex(item => item.id === id)

  if (index === -1) {
    return { code: 404, message: '商品不存在' }
  }

  productList[index].status = Number(body.status ?? productList[index].status)

  return {
    code: 200,
    message: 'success'
  }
})

// 删除商品
Mock.mock(RegExp('/api/products/\\d+$'), 'delete', (options) => {
  const id = Number(options.url.split('/').pop())
  productList = productList.filter(item => item.id !== id)

  return {
    code: 200,
    message: 'success'
  }
})
