import Mock from 'mockjs'

// 操作类型
const operations = ['登录', '登出', '新增', '修改', '删除', '导出', '导入']

// 获取操作日志列表
Mock.mock(RegExp('/api/log/list.*'), 'get', (options) => {
  const url = new URL('http://localhost' + options.url)
  const page = parseInt(url.searchParams.get('page')) || 1
  const pageSize = parseInt(url.searchParams.get('pageSize')) || 10

  const list = []
  for (let i = 0; i < pageSize; i++) {
    list.push({
      id: (page - 1) * pageSize + i + 1,
      operator: Mock.Random.cname(),
      operation: Mock.Random.pick(operations),
      ip: Mock.Random.ip(),
      description: Mock.Random.csentence(10, 30),
      createTime: Mock.Random.datetime()
    })
  }

  return {
    code: 200,
    message: 'success',
    data: {
      list,
      total: 200,
      page,
      pageSize
    }
  }
})

// 获取日志详情
Mock.mock(RegExp('/api/log/.*'), 'get', () => {
  return {
    code: 200,
    message: 'success',
    data: {
      id: 1,
      operator: 'Admin',
      operation: '登录',
      ip: '192.168.1.100',
      description: '用户登录系统',
      requestUrl: '/api/auth/login',
      requestMethod: 'POST',
      requestParams: '{"username":"admin"}',
      response: '{"code":200}',
      status: 1,
      createTime: Mock.Random.datetime()
    }
  }
})

// 删除日志
Mock.mock(RegExp('/api/log/.*'), 'delete', () => {
  return {
    code: 200,
    message: 'success'
  }
})

// 清空日志
Mock.mock('/api/log/clear', 'delete', () => {
  return {
    code: 200,
    message: 'success'
  }
})

// 导出日志
Mock.mock('/api/log/export', 'get', () => {
  return {
    code: 200,
    message: 'success'
  }
})
