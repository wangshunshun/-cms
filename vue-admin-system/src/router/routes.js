import Layout from '@/components/layout/Layout.vue'

// 静态路由
export const staticRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', hidden: true }
  },
  {
    path: '/404',
    name: '404',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404', hidden: true }
  },
  {
    path: '/403',
    name: '403',
    component: () => import('@/views/error/403.vue'),
    meta: { title: '403', hidden: true }
  }
]

// 默认路由（后备方案）
const defaultRoutes = [
  {
    path: '/dashboard',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '仪表盘', icon: 'Odometer', affix: true }
      }
    ]
  },
  {
    path: '/system',
    component: Layout,
    redirect: '/system/user',
    meta: { title: '系统管理', icon: 'Setting' },
    children: [
      {
        path: 'user',
        name: 'User',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', icon: 'User', roles: ['admin'] }
      },
      {
        path: 'role',
        name: 'Role',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理', icon: 'UserFilled', roles: ['admin'] }
      },
      {
        path: 'menu',
        name: 'Menu',
        component: () => import('@/views/system/menu/index.vue'),
        meta: { title: '菜单管理', icon: 'Menu', roles: ['admin'] }
      },
      {
        path: 'product',
        name: 'Product',
        component: () => import('@/views/system/product/index.vue'),
        meta: { title: '商品管理', icon: 'Goods', roles: ['admin'] }
      },
      {
        path: 'provider',
        name: 'Provider',
        component: () => import('@/views/system/provider/index.vue'),
        meta: { title: '供应商管理', icon: 'OfficeBuilding', roles: ['admin'] }
      },
      {
        path: 'contract',
        name: 'Contract',
        component: () => import('@/views/system/contract/index.vue'),
        meta: { title: '合同管理', icon: 'Document', roles: ['admin'] }
      },
      {
        path: 'license',
        name: 'License',
        component: () => import('@/views/system/license/index.vue'),
        meta: { title: '许可证管理', icon: 'Tickets', roles: ['admin'] }
      },
      {
        path: 'log',
        name: 'Log',
        component: () => import('@/views/system/log/index.vue'),
        meta: { title: '操作日志', icon: 'Document', roles: ['admin', 'editor'] }
      }
    ]
  },
  {
    path: '/permission',
    component: Layout,
    redirect: '/permission/index',
    meta: { title: '权限示例', icon: 'Lock' },
    children: [
      {
        path: 'index',
        name: 'Permission',
        component: () => import('@/views/permission/index.vue'),
        meta: { title: '权限示例', icon: 'Lock' }
      }
    ]
  }
]

export default [...staticRoutes]
