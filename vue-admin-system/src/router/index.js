import { createRouter, createWebHistory } from 'vue-router'
import NProgress from 'nprogress'
import { useUserStore } from '@/store/modules/user'
import { staticRoutes } from './routes'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: staticRoutes,
  scrollBehavior: () => ({ left: 0, top: 0 })
})

// 白名单路由
const whiteList = ['/login', '/404', '/403']

// 路由守卫
router.beforeEach(async (to, from, next) => {
  NProgress.start()

  const userStore = useUserStore()
  const token = userStore.token

  // 有token
  if (token) {
    if (to.path === '/login') {
      // 已登录，跳转到首页
      next({ path: '/dashboard' })
      NProgress.done()
    } else {
      // 检查是否已加载路由（通过检查路由是否存在来判断）
      const hasDashboard = router.hasRoute('Dashboard')

      if (hasDashboard && userStore.routesLoaded) {
        next()
      } else {
        try {
          // 获取用户信息
          await userStore.getUserInfo()
          // 从后端获取动态路由（失败会使用默认路由）
          const accessRoutes = await userStore.generateRoutes()

          // 动态添加路由
          accessRoutes.forEach(route => {
            router.addRoute(route)
          })

          // 添加 404 路由（必须在最后）
          router.addRoute({
            path: '/:pathMatch(.*)*',
            redirect: '/404'
          })

          // 重新导航
          next({ ...to, replace: true })
        } catch (error) {
          console.error('路由守卫错误:', error)
          // 清除token，跳转登录页
          userStore.resetState()
          next(`/login?redirect=${to.path}`)
          NProgress.done()
        }
      }
    }
  } else {
    // 无token
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login?redirect=${to.path}`)
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})

export default router
