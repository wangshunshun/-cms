import Mock from 'mockjs'

// 获取菜单列表
Mock.mock('/api/menu/list', 'get', () => {
  return {
    code: 200,
    message: 'success',
    data: [
      {
        id: 1,
        menuName: '仪表盘',
        path: '/dashboard',
        icon: 'Odometer',
        sort: 1,
        status: 1,
        children: []
      },
      {
        id: 2,
        menuName: '系统管理',
        path: '/system',
        icon: 'Setting',
        sort: 2,
        status: 1,
        children: [
          { id: 21, menuName: '用户管理', path: '/system/user', icon: 'User', sort: 1, status: 1 },
          { id: 22, menuName: '角色管理', path: '/system/role', icon: 'UserFilled', sort: 2, status: 1 },
          { id: 23, menuName: '菜单管理', path: '/system/menu', icon: 'Menu', sort: 3, status: 1 },
          { id: 24, menuName: '商品管理', path: '/system/product', icon: 'Goods', sort: 4, status: 1 },
          { id: 25, menuName: '供应商管理', path: '/system/provider', icon: 'OfficeBuilding', sort: 5, status: 1 },
          { id: 26, menuName: '合同管理', path: '/system/contract', icon: 'Document', sort: 6, status: 1 },
          { id: 27, menuName: '许可证管理', path: '/system/license', icon: 'Tickets', sort: 7, status: 1 },
          { id: 28, menuName: '操作日志', path: '/system/log', icon: 'Document', sort: 8, status: 1 }
        ]
      }
    ]
  }
})

// 获取菜单树
Mock.mock('/api/menu/tree', 'get', () => {
  return {
    code: 200,
    message: 'success',
    data: [
      {
        id: 1,
        label: '仪表盘',
        path: '/dashboard'
      },
      {
        id: 2,
        label: '系统管理',
        path: '/system',
        children: [
          { id: 21, label: '用户管理', path: '/system/user' },
          { id: 22, label: '角色管理', path: '/system/role' },
          { id: 23, label: '菜单管理', path: '/system/menu' },
          { id: 24, label: '商品管理', path: '/system/product' },
          { id: 25, label: '供应商管理', path: '/system/provider' },
          { id: 26, label: '合同管理', path: '/system/contract' },
          { id: 27, label: '许可证管理', path: '/system/license' },
          { id: 28, label: '操作日志', path: '/system/log' }
        ]
      }
    ]
  }
})

// 创建菜单
Mock.mock('/api/menu', 'post', () => {
  return {
    code: 200,
    message: 'success'
  }
})

// 更新菜单
Mock.mock('/api/menu', 'put', () => {
  return {
    code: 200,
    message: 'success'
  }
})

// 删除菜单
Mock.mock(RegExp('/api/menu/.*'), 'delete', () => {
  return {
    code: 200,
    message: 'success'
  }
})
