import Mock from 'mockjs'

// 获取角色列表
Mock.mock(RegExp('/api/role/list.*'), 'get', (options) => {
  const url = new URL('http://localhost' + options.url)
  const page = parseInt(url.searchParams.get('page')) || 1
  const pageSize = parseInt(url.searchParams.get('pageSize')) || 10

  const list = []
  for (let i = 0; i < pageSize; i++) {
    list.push({
      id: (page - 1) * pageSize + i + 1,
      roleName: Mock.Random.ctitle(2, 4),
      roleKey: Mock.Random.word(4, 8),
      description: Mock.Random.csentence(10, 20),
      status: Mock.Random.integer(0, 1),
      createTime: Mock.Random.datetime()
    })
  }

  return {
    code: 200,
    message: 'success',
    data: {
      list,
      total: 50,
      page,
      pageSize
    }
  }
})

// 获取所有角色
Mock.mock('/api/role/all', 'get', () => {
  return {
    code: 200,
    message: 'success',
    data: [
      { id: 1, roleName: '管理员', roleKey: 'admin' },
      { id: 2, roleName: '编辑员', roleKey: 'editor' },
      { id: 3, roleName: '访客', roleKey: 'viewer' }
    ]
  }
})

// 创建角色
Mock.mock('/api/role', 'post', () => {
  return {
    code: 200,
    message: 'success'
  }
})

// 更新角色
Mock.mock('/api/role', 'put', () => {
  return {
    code: 200,
    message: 'success'
  }
})

// 删除角色
Mock.mock(RegExp('/api/role/.*'), 'delete', () => {
  return {
    code: 200,
    message: 'success'
  }
})

// 获取角色权限
Mock.mock(RegExp('/api/role/.*/permissions'), 'get', () => {
  return {
    code: 200,
    message: 'success',
    data: ['system:user:view', 'system:role:view', 'system:menu:view']
  }
})

// 更新角色权限
Mock.mock(RegExp('/api/role/.*/permissions'), 'put', () => {
  return {
    code: 200,
    message: 'success'
  }
})
