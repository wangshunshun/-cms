import { defineStore } from 'pinia'

export const useTagsViewStore = defineStore('tagsView', {
  state: () => ({
    visitedViews: [],
    cachedViews: []
  }),

  actions: {
    addView(view) {
      this.addVisitedView(view)
      this.addCachedView(view)
    },

    addVisitedView(view) {
      if (this.visitedViews.some(v => v.path === view.path)) return
      this.visitedViews.push({
        name: view.name,
        path: view.path,
        title: view.meta?.title || 'no-name',
        fullPath: view.fullPath,
        query: view.query,
        affix: view.meta?.affix
      })
    },

    addCachedView(view) {
      if (view.name === null) return
      if (this.cachedViews.includes(view.name)) return
      if (view.meta?.noCache) return
      this.cachedViews.push(view.name)
    },

    delView(view) {
      return new Promise(resolve => {
        this.delVisitedView(view)
        this.delCachedView(view)
        resolve({
          visitedViews: [...this.visitedViews],
          cachedViews: [...this.cachedViews]
        })
      })
    },

    delVisitedView(view) {
      return new Promise(resolve => {
        for (const [i, v] of this.visitedViews.entries()) {
          if (v.path === view.path) {
            this.visitedViews.splice(i, 1)
            break
          }
        }
        resolve([...this.visitedViews])
      })
    },

    delCachedView(view) {
      return new Promise(resolve => {
        const index = this.cachedViews.indexOf(view.name)
        index > -1 && this.cachedViews.splice(index, 1)
        resolve([...this.cachedViews])
      })
    },

    delOthersViews(view) {
      return new Promise(resolve => {
        this.visitedViews = this.visitedViews.filter(v => v.affix || v.path === view.path)
        this.cachedViews = this.cachedViews.filter(name => name === view.name)
        resolve({
          visitedViews: [...this.visitedViews],
          cachedViews: [...this.cachedViews]
        })
      })
    },

    delAllViews() {
      return new Promise(resolve => {
        this.visitedViews = this.visitedViews.filter(tag => tag.affix)
        this.cachedViews = []
        resolve({
          visitedViews: [...this.visitedViews],
          cachedViews: [...this.cachedViews]
        })
      })
    },

    updateVisitedView(view) {
      for (let v of this.visitedViews) {
        if (v.path === view.path) {
          v = Object.assign(v, view)
          break
        }
      }
    }
  },

  persist: {
    key: 'tagsView-store',
    paths: ['visitedViews']
  }
})
