import request from '@/utils/request'

// 登录
export function login(data) {
  return request({
    url: 'auth/login',
    method: 'post',
    data
  })
}

// 获取用户信息
export function getUserInfo() {
  return request({
    url: 'user/info',
    method: 'get'
  })
}

// 登出
export function logout() {
  return request({
    url: 'auth/logout',
    method: 'post'
  })
}

// 获取用户列表
export function getUserList(params) {
  return request({
    url: 'user/list',
    method: 'get',
    params
  })
}

// 创建用户
export function createUser(data) {
  return request({
    url: 'user',
    method: 'post',
    data
  })
}

// 更新用户
export function updateUser(data) {
  return request({
    url: 'user',
    method: 'put',
    data
  })
}

// 删除用户
export function deleteUser(id) {
  return request({
    url: `user/${id}`,
    method: 'delete'
  })
}

// 修改密码
export function changePassword(data) {
  return request({
    url: 'user/password',
    method: 'put',
    data
  })
}
