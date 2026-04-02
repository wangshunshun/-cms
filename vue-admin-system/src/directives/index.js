// 权限指令 - 控制按钮显示
export const permission = {
  mounted(el, binding) {
    const { value } = binding

    // 从 localStorage 读取持久化的用户数据
    const storedData = localStorage.getItem('user-store')
    if (!storedData) return

    try {
      const userState = JSON.parse(storedData)
      const permissions = userState?.permissions || []

      if (value) {
        // 支持字符串或数组
        const requiredPermissions = Array.isArray(value) ? value : [value]
        // 超级权限 * 可以操作所有
        const hasPermission = permissions.includes('*') ||
          requiredPermissions.some(p => permissions.includes(p))

        if (!hasPermission) {
          el.parentNode && el.parentNode.removeChild(el)
        }
      }
    } catch (e) {
      console.error('权限指令解析错误:', e)
    }
  }
}

// 角色指令 - 控制按钮显示
export const role = {
  mounted(el, binding) {
    const { value } = binding

    // 从 localStorage 读取持久化的用户数据
    const storedData = localStorage.getItem('user-store')
    if (!storedData) return

    try {
      const userState = JSON.parse(storedData)
      const roles = userState?.roles || []

      if (value) {
        const requiredRoles = Array.isArray(value) ? value : [value]
        const hasRole = roles.some(r => requiredRoles.includes(r))

        if (!hasRole) {
          el.parentNode && el.parentNode.removeChild(el)
        }
      }
    } catch (e) {
      console.error('角色指令解析错误:', e)
    }
  }
}

// 防抖指令
export const debounce = {
  mounted(el, binding) {
    let timer = null
    el.addEventListener('click', () => {
      if (timer) {
        clearTimeout(timer)
      }
      timer = setTimeout(() => {
        binding.value()
      }, binding.arg || 300)
    })
  }
}

// 复制指令
export const copy = {
  mounted(el, binding) {
    el.addEventListener('click', () => {
      const text = binding.value
      navigator.clipboard.writeText(text).then(() => {
        console.log('复制成功')
      })
    })
  }
}

// 长按指令
export const longpress = {
  mounted(el, binding) {
    let timer = null
    el.addEventListener('mousedown', () => {
      timer = setTimeout(() => {
        binding.value()
      }, 500)
    })
    el.addEventListener('mouseup', () => {
      clearTimeout(timer)
    })
    el.addEventListener('mouseleave', () => {
      clearTimeout(timer)
    })
  }
}
