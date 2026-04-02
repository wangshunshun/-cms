import Mock from 'mockjs'

const users = [
  {
    id: 1,
    username: 'admin',
    password: 'password',
    nickname: 'Administrator',
    email: 'admin@example.com',
    phone: '13800138000',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    roles: ['admin'],
    permissions: ['*'],
    status: 1
  },
  {
    id: 2,
    username: 'editor',
    password: 'password',
    nickname: '编辑员',
    email: 'editor@example.com',
    phone: '13800138001',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    roles: ['editor'],
    permissions: ['system:user:view', 'system:role:view', 'system:menu:view', 'system:log:view'],
    status: 1
  }
]

// 获取当前登录用户
function getCurrentUser() {
  const currentUsername = sessionStorage.getItem('currentUsername')
  return users.find(u => u.username === currentUsername) || users[0]
}

// 登录
Mock.mock('/api/auth/login', 'post', (options) => {
  const { username, password } = JSON.parse(options.body)
  const user = users.find(u => u.username === username && u.password === password)

  if (user) {
    // 存储当前登录用户名
    sessionStorage.setItem('currentUsername', username)

    return {
      code: 200,
      message: 'success',
      data: {
        token: Mock.Random.guid()
      }
    }
  }

  return {
    code: 401,
    message: '用户名或密码错误'
  }
})

// 获取用户信息
Mock.mock('/api/user/info', 'get', () => {
  const user = getCurrentUser()

  return {
    code: 200,
    message: 'success',
    data: user
  }
})

// 登出
Mock.mock('/api/auth/logout', 'post', () => {
  // 清除当前登录用户
  sessionStorage.removeItem('currentUsername')

  return {
    code: 200,
    message: 'success'
  }
})

// 获取用户列表
Mock.mock(RegExp('/api/user/list.*'), 'get', (options) => {
  const url = new URL('http://localhost' + options.url)
  const page = parseInt(url.searchParams.get('page')) || 1
  const pageSize = parseInt(url.searchParams.get('pageSize')) || 10

  const list = []
  for (let i = 0; i < pageSize; i++) {
    list.push({
      id: (page - 1) * pageSize + i + 1,
      username: Mock.Random.first(),
      nickname: Mock.Random.cname(),
      email: Mock.Random.email(),
      phone: /^1[3-9]\d{9}$/,
      status: Mock.Random.integer(0, 1),
      roles: Mock.Random.pick(['admin', 'editor', 'viewer']),
      createTime: Mock.Random.datetime()
    })
  }

  return {
    code: 200,
    message: 'success',
    data: {
      list,
      total: 100,
      page,
      pageSize
    }
  }
})

// 创建用户
Mock.mock('/api/user', 'post', () => {
  return {
    code: 200,
    message: 'success'
  }
})

// 更新用户
Mock.mock('/api/user', 'put', () => {
  return {
    code: 200,
    message: 'success'
  }
})

// 删除用户
Mock.mock(RegExp('/api/user/.*'), 'delete', () => {
  return {
    code: 200,
    message: 'success'
  }
})
