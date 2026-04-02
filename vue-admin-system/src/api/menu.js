import request from '@/utils/request'

// 获取用户路由菜单
export function getRoutes() {
  return request({
    url: 'routes',
    method: 'get'
  })
}

// 获取菜单列表
export function getMenuList() {
  return request({
    url: 'menu/list',
    method: 'get'
  })
}

// 获取菜单树
export function getMenuTree() {
  return request({
    url: 'menu/tree',
    method: 'get'
  })
}

// 创建菜单
export function createMenu(data) {
  return request({
    url: 'menu',
    method: 'post',
    data
  })
}

// 更新菜单
export function updateMenu(data) {
  return request({
    url: 'menu',
    method: 'put',
    data
  })
}

// 删除菜单
export function deleteMenu(id) {
  return request({
    url: `menu/${id}`,
    method: 'delete'
  })
}
