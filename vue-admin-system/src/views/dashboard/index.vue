<template>
  <div class="dashboard-container">
    <div class="welcome-card">
      <h2>{{ $t('dashboard.welcome') }}, {{ username }}!</h2>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :xs="24" :sm="12" :lg="6" v-for="stat in stats" :key="stat.key">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-icon" :style="{ backgroundColor: stat.color }">
            <el-icon><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="16">
        <el-card shadow="hover">
          <template #header>
            <span>{{ $t('dashboard.statistics') }}</span>
          </template>
          <div ref="lineChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="8">
        <el-card shadow="hover">
          <template #header>
            <span>用户分布</span>
          </template>
          <div ref="pieChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/store/modules/user'
import { getStats } from '@/api/dashboard'
import * as echarts from 'echarts'

const { t } = useI18n()
const userStore = useUserStore()

const username = computed(() => userStore.username)
const lineChartRef = ref(null)
const pieChartRef = ref(null)

const statData = ref({
  userCount: 0,
  roleCount: 0,
  menuCount: 0,
  logCount: 0
})

const stats = computed(() => [
  { key: 'users', label: t('dashboard.totalUsers'), value: statData.value.userCount, icon: 'User', color: '#409eff' },
  { key: 'roles', label: t('dashboard.totalRoles'), value: statData.value.roleCount, icon: 'UserFilled', color: '#67c23a' },
  { key: 'menus', label: t('dashboard.totalMenus'), value: statData.value.menuCount, icon: 'Menu', color: '#e6a23c' },
  { key: 'logs', label: t('dashboard.totalLogs'), value: statData.value.logCount, icon: 'Document', color: '#f56c6c' }
])

const chartData = ref({
  visitDates: [],
  visitData: [],
  userDistribution: []
})

onMounted(async () => {
  await fetchStats()
  initLineChart()
  initPieChart()
})

const fetchStats = async () => {
  try {
    const res = await getStats()
    if (res.data) {
      statData.value = {
        userCount: res.data.userCount || 0,
        roleCount: res.data.roleCount || 0,
        menuCount: res.data.menuCount || 0,
        logCount: res.data.logCount || 0
      }
      chartData.value = {
        visitDates: res.data.visitDates || [],
        visitData: res.data.visitData || [],
        userDistribution: res.data.userDistribution || []
      }
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const initLineChart = () => {
  const chart = echarts.init(lineChartRef.value)
  const option = {
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: chartData.value.visitDates
    },
    yAxis: { type: 'value' },
    series: [
      {
        name: '访问量',
        type: 'line',
        smooth: true,
        data: chartData.value.visitData,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
          ])
        },
        lineStyle: { color: '#409eff' },
        itemStyle: { color: '#409eff' }
      }
    ]
  }
  chart.setOption(option)
  window.addEventListener('resize', () => chart.resize())
}

const initPieChart = () => {
  const chart = echarts.init(pieChartRef.value)
  const option = {
    tooltip: { trigger: 'item' },
    legend: { orient: 'vertical', left: 'left' },
    series: [
      {
        name: '用户分布',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        label: { show: false },
        emphasis: {
          label: { show: true, fontSize: 14, fontWeight: 'bold' }
        },
        labelLine: { show: false },
        data: chartData.value.userDistribution
      }
    ]
  }
  chart.setOption(option)
  window.addEventListener('resize', () => chart.resize())
}
</script>

<style lang="scss" scoped>
.dashboard-container {
  .welcome-card {
    padding: 24px 28px;
    background: linear-gradient(135deg, var(--primary-color) 0%, #6b93fc 100%);
    border-radius: var(--radius-xl);
    margin-bottom: 20px;
    box-shadow: var(--shadow-lg);

    h2 {
      margin: 0;
      color: #fff;
      font-size: 22px;
      font-weight: 600;
      letter-spacing: 0.5px;
    }
  }

  .stat-row {
    margin-bottom: 20px;

    .stat-card {
      height: 100%;
      transition: transform var(--transition-normal), box-shadow var(--transition-normal);

      &:hover {
        transform: translateY(-4px);
        box-shadow: var(--shadow-hover);
      }
    }
  }

  .chart-row {
    .chart {
      height: 320px;
    }
  }
}
</style>
