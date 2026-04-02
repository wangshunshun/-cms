export default {
  // 登录页
  login: {
    title: 'Vue Admin System',
    username: '用户名',
    password: '密码',
    rememberMe: '记住我',
    login: '登录',
    forgotPassword: '忘记密码?',
    register: '注册账号',
    usernamePlaceholder: '请输入用户名',
    passwordPlaceholder: '请输入密码',
    loginSuccess: '登录成功',
    loginFailed: '登录失败'
  },

  // 导航栏
  navbar: {
    profile: '个人中心',
    logout: '退出登录',
    fullscreen: '全屏',
    exitFullscreen: '退出全屏',
    lightMode: '浅色模式',
    darkMode: '深色模式'
  },

  // 标签视图
  tagsView: {
    refresh: '刷新',
    close: '关闭',
    closeOthers: '关闭其他',
    closeAll: '关闭所有'
  },

  // 仪表盘
  dashboard: {
    title: '仪表盘',
    welcome: '欢迎回来',
    totalUsers: '用户总数',
    totalRoles: '角色总数',
    totalMenus: '菜单总数',
    totalLogs: '操作日志',
    recentLogs: '最近操作',
    statistics: '数据统计'
  },

  // 系统管理
  system: {
    user: {
      title: '用户管理',
      username: '用户名',
      nickname: '昵称',
      email: '邮箱',
      phone: '手机号',
      status: '状态',
      roles: '角色',
      createTime: '创建时间',
      actions: '操作',
      add: '新增用户',
      edit: '编辑用户',
      delete: '删除用户',
      resetPassword: '重置密码',
      enabled: '启用',
      disabled: '禁用'
    },
    role: {
      title: '角色管理',
      roleName: '角色名称',
      roleKey: '角色标识',
      description: '描述',
      status: '状态',
      createTime: '创建时间',
      actions: '操作',
      add: '新增角色',
      edit: '编辑角色',
      delete: '删除角色',
      assignPermissions: '分配权限'
    },
    menu: {
      title: '菜单管理',
      menuName: '菜单名称',
      path: '路由路径',
      component: '组件路径',
      icon: '图标',
      sort: '排序',
      status: '状态',
      actions: '操作',
      add: '新增菜单',
      edit: '编辑菜单',
      delete: '删除菜单'
    },
    log: {
      title: '操作日志',
      operator: '操作人',
      operation: '操作类型',
      ip: 'IP地址',
      description: '操作描述',
      createTime: '操作时间',
      actions: '操作',
      detail: '详情',
      delete: '删除',
      clearAll: '清空日志',
      export: '导出'
    }
  },

  // 个人中心
  profile: {
    title: '个人中心',
    basicInfo: '基本信息',
    changePassword: '修改密码',
    oldPassword: '原密码',
    newPassword: '新密码',
    confirmPassword: '确认密码',
    save: '保存'
  },

  // 通用
  common: {
    search: '搜索',
    reset: '重置',
    add: '新增',
    edit: '编辑',
    delete: '删除',
    cancel: '取消',
    confirm: '确定',
    export: '导出',
    import: '导入',
    actions: '操作',
    status: '状态',
    enabled: '启用',
    disabled: '禁用',
    yes: '是',
    no: '否',
    success: '操作成功',
    failed: '操作失败',
    confirmDelete: '确定要删除吗？',
    noData: '暂无数据'
  },

  // 错误页
  error: {
    notFound: '页面未找到',
    forbidden: '没有权限',
    backHome: '返回首页',
    goBack: '返回上一页'
  },

  // 权限
  permission: {
    noPermission: '没有操作权限',
    admin: '管理员',
    editor: '编辑员',
    user: '普通用户'
  }
}
