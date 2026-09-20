<template>
  <div v-loading="dashboardLoading" class="app-container property-dashboard">
    <!-- 看板在同一统计周期内集中展示汇总、状态、分类和人员工作量。 -->
    <section class="dashboard-hero">
      <div>
        <p class="eyebrow">社区物业服务中心</p>
        <h1>物业报修运营看板</h1>
        <p>集中查看报修受理、维修处理、服务评价和人员工作量，帮助物业及时发现待处理事项。</p>
      </div>
      <div class="hero-actions">
        <div class="period-filter">
          <span>统计周期</span>
          <el-select v-model="period" aria-label="统计周期" @change="getData">
            <el-option v-for="item in periodOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </div>
        <div class="hero-pulse" aria-hidden="true">
          <div class="house-icon">
            <el-icon><OfficeBuilding /></el-icon>
          </div>
          <span></span>
          <span></span>
        </div>
      </div>
    </section>

    <section class="kpi-grid">
      <div v-for="item in kpis" :key="item.label" class="kpi-card" :class="item.type">
        <div class="kpi-icon">
          <el-icon><component :is="item.icon" /></el-icon>
        </div>
        <div class="kpi-content">
          <span>{{ item.label }}</span>
          <strong>{{ item.value }}</strong>
          <em>{{ item.desc }}</em>
        </div>
      </div>
    </section>

    <section class="insight-grid">
      <div class="panel chart-panel">
        <div class="panel-head">
          <div>
            <h2>工单状态分布</h2>
            <p>查看待处理、维修中、已完成等工单占比</p>
          </div>
          <el-icon><PieChart /></el-icon>
        </div>
        <div ref="statusChart" class="chart"></div>
      </div>

      <div class="panel chart-panel">
        <div class="panel-head">
          <div>
            <h2>报修分类排行</h2>
            <p>识别高频问题类型，便于安排备件和巡检</p>
          </div>
          <el-icon><DataAnalysis /></el-icon>
        </div>
        <div ref="categoryChart" class="chart"></div>
      </div>
    </section>

    <section class="content-grid">
      <div class="panel">
        <div class="panel-head">
          <div>
            <h2>维修人员工作量</h2>
            <p>按已分配工单统计维修人员处理压力</p>
          </div>
          <el-icon><Tools /></el-icon>
        </div>
        <div class="worker-list">
          <div v-for="item in topRepairUsers" :key="item.repairUserName || '未分配'" class="worker-item">
            <div class="worker-avatar">{{ workerInitial(item.repairUserName) }}</div>
            <div class="worker-main">
              <div class="worker-row">
                <strong>{{ item.repairUserName || '未分配' }}</strong>
                <span>{{ item.count }} 单</span>
              </div>
              <el-progress :percentage="workerPercent(item.count)" :show-text="false" color="#178f7a" />
            </div>
          </div>
          <div v-if="topRepairUsers.length === 0" class="empty-line">暂无维修人员工作量数据</div>
        </div>
      </div>

      <div class="panel">
        <div class="panel-head">
          <div>
            <h2>处理提醒</h2>
            <p>优先关注影响业主体验的待办事项</p>
          </div>
          <el-icon><Warning /></el-icon>
        </div>
        <div class="todo-list">
          <div v-for="item in todoCards" :key="item.label" class="todo-item">
            <el-icon><component :is="item.icon" /></el-icon>
            <div>
              <strong>{{ item.value }}</strong>
              <span>{{ item.label }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="panel">
        <div class="panel-head">
          <div>
            <h2>服务质量</h2>
            <p>评价结果反映维修服务体验</p>
          </div>
          <el-icon><StarFilled /></el-icon>
        </div>
        <div class="quality-box">
          <div class="score-ring">
            <strong>{{ avgScore }}</strong>
            <span>平均评分</span>
          </div>
          <div class="quality-copy">
            <h3>{{ scoreText }}</h3>
            <p>建议物业管理员定期查看服务评价，对低分工单进行回访和复盘。</p>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { dashboard } from '@/api/property/order'

// 数据库存储状态编码，展示层统一映射为业务中文名称。
const STATUS_LABELS = {
  0: '待受理',
  1: '已受理',
  2: '已分配',
  3: '维修中',
  4: '待确认',
  5: '已完成',
  6: '已驳回',
  7: '已取消',
  8: '返工中'
}

export default {
  name: 'PropertyDashboard',
  data() {
    return {
      period: 'month',
      periodOptions: [
        { label: '当月', value: 'month' },
        { label: '近30天', value: '30d' },
        { label: '近90天', value: '90d' },
        { label: '近180天', value: '180d' }
      ],
      dashboardLoading: false,
      summary: {},
      statusStats: [],
      categoryStats: [],
      repairUserStats: [],
      statusChart: null,
      categoryChart: null
    }
  },
  computed: {
    totalCount() {
      return Number(this.summary.totalCount || 0)
    },
    periodLabel() {
      return this.periodOptions.find(item => item.value === this.period)?.label || '当月'
    },
    pendingCount() {
      return Number(this.summary.pendingCount || 0)
    },
    completedCount() {
      return Number(this.summary.completedCount || 0)
    },
    avgScore() {
      const score = Number(this.summary.avgScore || 0)
      return score ? score.toFixed(1) : '0.0'
    },
    completionRate() {
      if (!this.totalCount) return 0
      return Math.round((this.completedCount / this.totalCount) * 100)
    },
    activeCount() {
      return this.pendingCount
    },
    kpis() {
      return [
        { label: '报修总数', value: this.totalCount, desc: `${this.periodLabel}登记工单`, icon: 'Tickets', type: 'teal' },
        { label: '待处理', value: this.pendingCount, desc: '当前尚未办结的工单', icon: 'Bell', type: 'amber' },
        { label: '进行中', value: this.activeCount, desc: '未完成工单总量', icon: 'Operation', type: 'blue' },
        { label: '完成率', value: `${this.completionRate}%`, desc: '已完成工单占比', icon: 'CircleCheck', type: 'green' },
        { label: '平均评分', value: this.avgScore, desc: '业主服务评价', icon: 'StarFilled', type: 'violet' }
      ]
    },
    todoCards() {
      return [
        { label: '待受理工单', value: this.statusCount(['0']), icon: 'Clock' },
        { label: '待确认/返工', value: this.statusCount(['4', '8']), icon: 'RefreshLeft' },
        { label: '未完成工单', value: this.activeCount, icon: 'List' },
        { label: '已完成工单', value: this.completedCount, icon: 'Finished' }
      ]
    },
    topRepairUsers() {
      return this.repairUserStats.slice(0, 6).map(item => ({
        ...item,
        count: Number(item.count || item.title || 0)
      }))
    },
    maxWorkerCount() {
      return Math.max(...this.topRepairUsers.map(item => item.count), 1)
    },
    scoreText() {
      const score = Number(this.avgScore)
      if (score >= 4.5) return '服务表现优秀'
      if (score >= 4) return '服务整体稳定'
      if (score > 0) return '建议关注低分反馈'
      return '暂无评价数据'
    }
  },
  created() {
    this.getData()
  },
  mounted() {
    window.addEventListener('resize', this.resizeCharts)
  },
  beforeUnmount() {
    // 离开页面时释放监听器和图表实例，避免反复进入首页造成内存累积。
    window.removeEventListener('resize', this.resizeCharts)
    if (this.statusChart) this.statusChart.dispose()
    if (this.categoryChart) this.categoryChart.dispose()
  },
  methods: {
    getData() {
      const requestedPeriod = this.period
      this.dashboardLoading = true
      dashboard(requestedPeriod).then(res => {
        // 快速切换周期时忽略旧请求，防止较慢响应覆盖用户的最新选择。
        if (requestedPeriod !== this.period) return
        const data = res.data || {}
        this.summary = data.summary || {}
        this.statusStats = this.normalizeRows(data.statusStats || [], 'status')
        this.categoryStats = this.normalizeRows(data.categoryStats || [], 'categoryName')
        this.repairUserStats = this.normalizeRows(data.repairUserStats || [], 'repairUserName')
        this.$nextTick(this.renderCharts)
      }).finally(() => {
        if (requestedPeriod === this.period) this.dashboardLoading = false
      })
    },
    normalizeRows(rows, labelKey) {
      // 统一处理空名称和数字类型，让多个图表使用同一数据格式。
      return rows.map(item => ({
        ...item,
        name: labelKey === 'status' ? (STATUS_LABELS[item.status] || item.status || '未知') : (item[labelKey] || '未分类'),
        count: Number(item.count || item.title || 0)
      }))
    },
    statusCount(statuses) {
      return this.statusStats
        .filter(item => statuses.includes(String(item.status)))
        .reduce((sum, item) => sum + item.count, 0)
    },
    workerInitial(name) {
      return name ? name.slice(-2) : '待派'
    },
    workerPercent(count) {
      return Math.min(100, Math.round((count / this.maxWorkerCount) * 100))
    },
    renderCharts() {
      this.renderStatusChart()
      this.renderCategoryChart()
    },
    renderStatusChart() {
      if (!this.$refs.statusChart) return
      // 复用已有实例，只更新图表配置，减少重复初始化和事件监听。
      this.statusChart = this.statusChart || echarts.init(this.$refs.statusChart)
      const data = this.statusStats.map(item => ({ name: item.name, value: item.count }))
      this.statusChart.setOption({
        color: ['#178f7a', '#38bdf8', '#f59e0b', '#6366f1', '#22c55e', '#94a3b8', '#ef4444', '#64748b', '#a855f7'],
        tooltip: { trigger: 'item' },
        legend: { bottom: 0, icon: 'circle' },
        series: [{
          type: 'pie',
          radius: ['48%', '70%'],
          center: ['50%', '43%'],
          avoidLabelOverlap: true,
          label: { formatter: '{b}\n{c}单' },
          data
        }]
      })
    },
    renderCategoryChart() {
      if (!this.$refs.categoryChart) return
      this.categoryChart = this.categoryChart || echarts.init(this.$refs.categoryChart)
      const rows = this.categoryStats.slice(0, 8).reverse()
      this.categoryChart.setOption({
        color: ['#178f7a'],
        grid: { left: 20, right: 24, top: 20, bottom: 18, containLabel: true },
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        xAxis: { type: 'value', splitLine: { lineStyle: { color: '#edf2f7' } } },
        yAxis: { type: 'category', data: rows.map(item => item.name), axisTick: { show: false } },
        series: [{
          type: 'bar',
          data: rows.map(item => item.count),
          barWidth: 14,
          itemStyle: { borderRadius: [0, 8, 8, 0] }
        }]
      })
    },
    resizeCharts() {
      if (this.statusChart) this.statusChart.resize()
      if (this.categoryChart) this.categoryChart.resize()
    }
  }
}
</script>

<style scoped lang="scss">
.property-dashboard {
  color: #122522;
  background: #f4f8f7;
  min-height: calc(100vh - 90px);
}

.dashboard-hero {
  position: relative;
  min-height: 180px;
  padding: 30px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  overflow: hidden;
  border-radius: 8px;
  color: #fff;
  background:
    linear-gradient(135deg, rgba(11, 94, 86, .96), rgba(20, 116, 101, .88)),
    linear-gradient(90deg, #0f766e, #2563eb);
  box-shadow: 0 16px 38px rgba(15, 118, 110, .2);

  .eyebrow {
    margin: 0 0 8px;
    opacity: .78;
    font-size: 13px;
  }

  h1 {
    margin: 0;
    font-size: 32px;
    line-height: 1.25;
  }

  p {
    max-width: 680px;
    margin: 12px 0 0;
    color: rgba(255, 255, 255, .86);
    line-height: 1.8;
  }
}

.hero-pulse {
  position: relative;
  width: 170px;
  height: 130px;
  display: flex;
  align-items: center;
  justify-content: center;

  span {
    position: absolute;
    border-radius: 50%;
    border: 1px solid rgba(255, 255, 255, .28);
  }

  span:nth-child(2) {
    width: 110px;
    height: 110px;
  }

  span:nth-child(3) {
    width: 160px;
    height: 160px;
  }
}

.hero-actions {
  display: flex;
  align-items: center;
  gap: 18px;
}

.period-filter {
  width: 160px;
  padding: 12px;
  border: 1px solid rgba(255, 255, 255, .24);
  border-radius: 8px;
  background: rgba(255, 255, 255, .12);
  backdrop-filter: blur(10px);

  > span {
    display: block;
    margin-bottom: 7px;
    color: rgba(255, 255, 255, .78);
    font-size: 12px;
  }

  :deep(.el-select) {
    width: 100%;
  }
}

.house-icon {
  position: relative;
  z-index: 1;
  width: 76px;
  height: 76px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  color: #0f766e;
  background: #fff;
  font-size: 40px;
  box-shadow: 0 18px 40px rgba(0, 0, 0, .2);
}

.kpi-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 14px;
  margin-top: 16px;
}

.kpi-card,
.panel {
  border: 1px solid #e4efeb;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 8px 24px rgba(15, 23, 42, .06);
}

.kpi-card {
  position: relative;
  padding: 18px;
  display: flex;
  gap: 14px;
  min-height: 118px;
  overflow: hidden;

  &:before {
    content: "";
    position: absolute;
    inset: 0;
    opacity: .08;
  }

  &.teal:before { background: #178f7a; }
  &.amber:before { background: #f59e0b; }
  &.blue:before { background: #2563eb; }
  &.green:before { background: #16a34a; }
  &.violet:before { background: #7c3aed; }
}

.kpi-icon {
  position: relative;
  z-index: 1;
  width: 44px;
  height: 44px;
  flex: 0 0 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  color: #fff;
  font-size: 22px;
  background: #178f7a;

  .amber & { background: #f59e0b; }
  .blue & { background: #2563eb; }
  .green & { background: #16a34a; }
  .violet & { background: #7c3aed; }
}

.kpi-content {
  position: relative;
  z-index: 1;

  span {
    display: block;
    color: #64748b;
    font-size: 13px;
  }

  strong {
    display: block;
    margin: 8px 0 5px;
    font-size: 28px;
    line-height: 1;
    color: #0f172a;
  }

  em {
    font-style: normal;
    color: #94a3b8;
    font-size: 12px;
  }
}

.insight-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-top: 16px;
}

.content-grid {
  display: grid;
  grid-template-columns: 1.2fr .9fr .9fr;
  gap: 16px;
  margin-top: 16px;
}

.panel {
  padding: 20px;
  min-height: 260px;
}

.panel-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;

  h2 {
    margin: 0;
    color: #102a27;
    font-size: 18px;
  }

  p {
    margin: 6px 0 0;
    color: #94a3b8;
  }

  > .el-icon {
    width: 36px;
    height: 36px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 8px;
    color: #178f7a;
    background: #e8f8f4;
    font-size: 20px;
  }
}

.chart {
  width: 100%;
  height: 310px;
}

.worker-list {
  display: grid;
  gap: 14px;
}

.worker-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.worker-avatar {
  width: 42px;
  height: 42px;
  flex: 0 0 42px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  color: #fff;
  background: linear-gradient(135deg, #178f7a, #2563eb);
  font-weight: 700;
}

.worker-main {
  flex: 1;
  min-width: 0;
}

.worker-row {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 8px;

  strong {
    color: #1f2937;
  }

  span {
    color: #64748b;
  }
}

.todo-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.todo-item {
  min-height: 94px;
  padding: 16px;
  border-radius: 8px;
  background: #f8fbfa;
  border: 1px solid #e5eee9;

  .el-icon {
    color: #178f7a;
    font-size: 22px;
  }

  strong {
    display: block;
    margin: 10px 0 4px;
    color: #0f172a;
    font-size: 24px;
  }

  span {
    color: #64748b;
  }
}

.quality-box {
  display: flex;
  align-items: center;
  gap: 18px;
  min-height: 170px;
}

.score-ring {
  width: 118px;
  height: 118px;
  flex: 0 0 118px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background:
    radial-gradient(circle at center, #fff 58%, transparent 59%),
    conic-gradient(#178f7a 0 78%, #e5eee9 78% 100%);

  strong {
    font-size: 30px;
    color: #102a27;
  }

  span {
    color: #64748b;
  }
}

.quality-copy {
  h3 {
    margin: 0 0 8px;
    color: #102a27;
    font-size: 20px;
  }

  p {
    margin: 0;
    color: #64748b;
    line-height: 1.7;
  }
}

.empty-line {
  padding: 34px 0;
  color: #94a3b8;
  text-align: center;
}

@media (max-width: 1300px) {
  .kpi-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .content-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 900px) {
  .dashboard-hero {
    align-items: flex-start;
    flex-direction: column;
    gap: 16px;
  }

  .hero-pulse {
    display: none;
  }

  .hero-actions {
    width: 100%;
  }

  .period-filter {
    width: 100%;
  }

  .kpi-grid,
  .insight-grid,
  .todo-list {
    grid-template-columns: 1fr;
  }
}
</style>
