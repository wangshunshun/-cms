/**
 * 日期格式化
 * @param {string|Date} date 日期
 * @param {string} format 格式
 * @returns {string}
 */
export function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
  if (!date) return ''
  const d = new Date(date)
  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')

  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 深拷贝
 * @param {Object} obj 对象
 * @returns {Object}
 */
export function deepClone(obj) {
  if (obj === null || typeof obj !== 'object') return obj
  const clone = Array.isArray(obj) ? [] : {}
  for (const key in obj) {
    if (Object.prototype.hasOwnProperty.call(obj, key)) {
      clone[key] = deepClone(obj[key])
    }
  }
  return clone
}

/**
 * 防抖函数
 * @param {Function} fn 函数
 * @param {number} delay 延迟时间
 * @returns {Function}
 */
export function debounce(fn, delay = 300) {
  let timer = null
  return function (...args) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

/**
 * 节流函数
 * @param {Function} fn 函数
 * @param {number} delay 延迟时间
 * @returns {Function}
 */
export function throttle(fn, delay = 300) {
  let last = 0
  return function (...args) {
    const now = Date.now()
    if (now - last >= delay) {
      fn.apply(this, args)
      last = now
    }
  }
}

/**
 * 生成UUID
 * @returns {string}
 */
export function generateUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    const r = (Math.random() * 16) | 0
    const v = c === 'x' ? r : (r & 0x3) | 0x8
    return v.toString(16)
  })
}

/**
 * 树形数据转换
 * @param {Array} data 数据
 * @param {string} idKey ID字段
 * @param {string} parentKey 父ID字段
 * @param {string} childrenKey 子节点字段
 * @returns {Array}
 */
export function arrayToTree(data, idKey = 'id', parentKey = 'parentId', childrenKey = 'children') {
  const result = []
  const map = {}

  data.forEach(item => {
    map[item[idKey]] = item
  })

  data.forEach(item => {
    const parent = map[item[parentKey]]
    if (parent) {
      if (!parent[childrenKey]) {
        parent[childrenKey] = []
      }
      parent[childrenKey].push(item)
    } else {
      result.push(item)
    }
  })

  return result
}
