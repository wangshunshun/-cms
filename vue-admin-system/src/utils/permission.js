import { useUserStore } from '@/store/modules/user'

/**
 * 检查是否有权限
 * @param {string|string[]} value 权限标识
 * @returns {boolean}
 */
export function hasPermission(value) {
  const userStore = useUserStore()
  const permissions = userStore.permissions || []

  if (!value) return true

  const requiredPermissions = Array.isArray(value) ? value : [value]
  return permissions.includes('*') ||
    requiredPermissions.some(p => permissions.includes(p))
}

/**
 * 检查是否有角色
 * @param {string|string[]} value 角色标识
 * @returns {boolean}
 */
export function hasRole(value) {
  const userStore = useUserStore()
  const roles = userStore.roles || []

  if (!value) return true

  const requiredRoles = Array.isArray(value) ? value : [value]
  return roles.some(r => requiredRoles.includes(r))
}
