import Mock from 'mockjs'
import './user'
import './role'
import './menu'
import './log'
import './product'
import './provider'
import './contract'
import './license'

export function setupMock() {
  Mock.setup({
    timeout: '200-600'
  })
}
