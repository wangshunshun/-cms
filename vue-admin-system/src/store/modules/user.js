import { defineStore } from 'pinia'
import { login, getUserInfo, logout } from '@/api/user'
import { getRoutes } from '@/api/menu'
import router from '@/router'
import Layout from '@/components/layout/Layout.vue'

// 默认路由（后备方案）- 直接使用完整配置
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

export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    userInfo: {},
    roles: [],
    permissions: [],
    routes: [],
    addRoutes: [],
    routesLoaded: false
  }),

  getters: {
    isLoggedIn: (state) => !!state.token,
    username: (state) => state.userInfo?.username || '',
    avatar: (state) => state.userInfo?.avatar || ''
  },

  actions: {
    async login(loginForm) {
      const result = await login(loginForm)
      if (result && result.data && result.data.token) {
        this.token = result.data.token
      } else {
        throw new Error('登录失败：返回数据格式错误')
      }
      return result.data
    },

    async getUserInfo() {
      try {
        const result = await getUserInfo()
        this.userInfo = result.data
        this.roles = result.data.roles || ['admin']
        this.permissions = result.data.permissions || ['*']
        return result.data
      } catch (error) {
        console.error('获取用户信息失败:', error)
        // 使用默认值
        this.roles = ['admin']
        this.permissions = ['*']
        this.userInfo = { username: 'admin' }
        return this.userInfo
      }
    },

    async generateRoutes() {
      if (this.routesLoaded) {
        return this.routes
      }

      // 直接使用默认路由，不再尝试从后端获取
      this.routes = defaultRoutes
      this.routesLoaded = true
      return defaultRoutes
    },

    async logout() {
      try {
        await logout()
      } catch (e) {
        // ignore
      }
      // 清除 session 中的当前用户
      sessionStorage.removeItem('currentUsername')
      this.resetState()
      router.push('/login')
    },

    resetState() {
      this.token = ''
      this.userInfo = {}
      this.roles = []
      this.permissions = []
      this.routes = []
      this.addRoutes = []
      this.routesLoaded = false
    }
  },

  persist: {
    key: 'user-store',
    paths: ['token', 'roles', 'permissions', 'userInfo']
  }
})
