<template>
  <div class="app-container role-home">
    <property-dashboard v-if="isManager" class="embedded-dashboard" />

    <template v-else>
      <section class="role-hero" :class="isRepairWorker ? 'worker' : 'owner'">
        <div>
          <p class="eyebrow">{{ isRepairWorker ? 'MAINTENANCE WORKBENCH' : 'OWNER SERVICE CENTER' }}</p>
          <h1>{{ greetingTitle }}</h1>
          <p>{{ greetingDescription }}</p>
        </div>
        <div class="hero-stat">
          <span>当前进行中</span>
          <strong>{{ activeOrders.length }}</strong>
          <em>个报修工单</em>
        </div>
      </section>

      <section class="order-panel">
        <div class="section-head">
          <div>
            <h2>进行中的报修工单</h2>
            <p>{{ isRepairWorker ? '仅展示当前账户负责且尚未结束的维修任务' : '仅展示当前账户提交且尚未结束的报修事项' }}</p>
          </div>
          <div class="head-actions">
            <el-button :icon="Refresh" :loading="orderLoading" @click="loadActiveOrders">刷新</el-button>
            <el-button type="primary" :icon="Tickets" @click="goToOrders">进入工单列表</el-button>
          </div>
        </div>

        <el-table v-if="orderLoading || activeOrders.length > 0" v-loading="orderLoading" :data="activeOrders" class="active-order-table">
          <el-table-column label="工单号" prop="orderNo" min-width="190" />
          <el-table-column label="标题" prop="title" min-width="170" show-overflow-tooltip />
          <el-table-column label="报修分类" prop="categoryName" min-width="120" />
          <el-table-column label="房屋" prop="roomText" min-width="160" show-overflow-tooltip />
          <el-table-column v-if="isRepairWorker" label="业主" prop="ownerName" min-width="110" />
          <el-table-column v-else label="维修人员" prop="repairUserName" min-width="120">
            <template #default="scope">{{ scope.row.repairUserName || '待分配' }}</template>
          </el-table-column>
          <el-table-column label="状态" width="110">
            <template #default="scope">
              <el-tag :type="statusMeta(scope.row.status).type">
                {{ statusMeta(scope.row.status).label }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="最后更新" min-width="165">
            <template #default="scope">{{ scope.row.updateTime || scope.row.createTime || '-' }}</template>
          </el-table-column>
        </el-table>

        <el-empty
          v-if="!orderLoading && activeOrders.length === 0"
          description="当前没有进行中的报修工单"
          :image-size="90"
        />
      </section>
    </template>

    <section class="notice-board">
      <div class="section-head notice-title-row">
        <div>
          <h2>通知公告</h2>
          <p>及时了解物业服务通知、维修安排和社区公告</p>
        </div>
        <el-button link type="primary" :icon="Refresh" @click="loadNotices">刷新公告</el-button>
      </div>

      <div class="notice-shell" v-loading="noticeLoading">
        <div class="notice-list">
          <button
            v-for="item in noticeList"
            :key="item.noticeId"
            type="button"
            class="notice-list-item"
            :class="{ active: activeNotice && activeNotice.noticeId === item.noticeId, read: item.isRead }"
            @click="selectNotice(item)"
          >
            <div class="notice-item-head">
              <el-tag size="small" :type="item.noticeType === '1' ? 'warning' : 'success'">
                {{ item.noticeType === '1' ? '通知' : '公告' }}
              </el-tag>
              <span v-if="!item.isRead" class="unread-dot"></span>
            </div>
            <h3>{{ item.noticeTitle }}</h3>
            <p>{{ plainContent(item.noticeContent) || '点击查看公告详情' }}</p>
            <time>{{ item.createTime || '-' }}</time>
          </button>
          <div v-if="!noticeLoading && noticeList.length === 0" class="notice-empty">
            <el-icon><Message /></el-icon>
            <span>暂无通知公告</span>
          </div>
        </div>

        <div class="notice-detail" v-loading="detailLoading">
          <template v-if="activeNotice">
            <div class="notice-detail-head">
              <el-tag :type="activeNotice.noticeType === '1' ? 'warning' : 'success'">
                {{ activeNotice.noticeType === '1' ? '通知' : '公告' }}
              </el-tag>
              <span>{{ activeNotice.createTime || '-' }}</span>
            </div>
            <h3>{{ activeNotice.noticeTitle }}</h3>
            <div class="notice-content" v-html="activeNotice.noticeContent || '<p>暂无公告内容</p>'"></div>
            <el-button type="primary" size="small" plain :icon="Document" @click="openNoticeDetail(activeNotice)">
              打开详情
            </el-button>
          </template>
          <div v-else class="notice-empty detail-empty">
            <el-icon><Document /></el-icon>
            <span>选择左侧公告查看详情</span>
          </div>
        </div>
      </div>
    </section>

    <notice-detail-view ref="noticeViewRef" />
  </div>
</template>

<script>
import PropertyDashboard from '@/views/property/dashboard/index.vue'
import NoticeDetailView from '@/layout/components/HeaderNotice/DetailView'
import useUserStore from '@/store/modules/user'
import { listOrder } from '@/api/property/order'
import { getNotice, listNoticeTop, markNoticeRead } from '@/api/system/notice'
import { Document, Message, Refresh, Tickets } from '@element-plus/icons-vue'

const ACTIVE_STATUSES = new Set(['0', '1', '2', '3', '4', '8'])
const STATUS_META = {
  0: { label: '待受理', type: 'primary' },
  1: { label: '已受理', type: 'info' },
  2: { label: '已分配', type: 'warning' },
  3: { label: '维修中', type: 'warning' },
  4: { label: '待确认', type: 'primary' },
  5: { label: '已完成', type: 'success' },
  6: { label: '已驳回', type: 'danger' },
  7: { label: '已取消', type: 'info' },
  8: { label: '返工中', type: 'danger' }
}

export default {
  name: 'Index',
  components: { PropertyDashboard, NoticeDetailView },
  setup() {
    return { Document, Message, Refresh, Tickets }
  },
  data() {
    return {
      orderLoading: false,
      activeOrders: [],
      noticeLoading: false,
      detailLoading: false,
      noticeList: [],
      activeNotice: null
    }
  },
  computed: {
    // 首页按角色呈现工作重点：管理人员看全局看板，业主和维修人员看自己的进行中工单。
    roles() {
      return useUserStore().roles || []
    },
    isManager() {
      return this.roles.some(role => ['admin', 'system_admin', 'property_manager'].includes(role))
    },
    isRepairWorker() {
      return this.roles.includes('repair_worker')
    },
    greetingTitle() {
      return this.isRepairWorker ? '我的维修工作台' : '我的报修服务台'
    },
    greetingDescription() {
      return this.isRepairWorker
        ? '集中查看当前分配给您的维修任务，及时跟进接单、处理和完工反馈。'
        : '集中查看您正在办理的报修工单，随时掌握受理、分配和维修进度。'
    }
  },
  created() {
    // 非管理角色无需加载全局统计，减少无关请求，同时避免在界面上暴露整体经营数据。
    if (!this.isManager) {
      this.loadActiveOrders()
    }
    this.loadNotices()
  },
  methods: {
    loadActiveOrders() {
      this.orderLoading = true
      listOrder({ pageNum: 1, pageSize: 100 }).then(response => {
        // 后端已按登录身份隔离数据；前端仅进一步筛选进行中状态并按最近变化排序。
        this.activeOrders = (response.rows || [])
          .filter(order => ACTIVE_STATUSES.has(String(order.status)))
          .sort((left, right) => String(right.updateTime || right.createTime || '').localeCompare(String(left.updateTime || left.createTime || '')))
      }).finally(() => {
        this.orderLoading = false
      })
    },
    statusMeta(status) {
      return STATUS_META[String(status)] || { label: String(status || '未知'), type: 'info' }
    },
    goToOrders() {
      this.$router.push('/property/order')
    },
    loadNotices() {
      this.noticeLoading = true
      listNoticeTop().then(response => {
        this.noticeList = response.data || []
        // 公告只有在用户主动点击查看时才标记已读。
        this.activeNotice = null
      }).finally(() => {
        this.noticeLoading = false
      })
    },
    selectNotice(item) {
      // 先本地更新红点以获得即时反馈，再异步持久化当前用户的已读记录。
      const selectedNotice = { ...item, isRead: true }
      this.activeNotice = selectedNotice
      this.loadNoticeDetail(selectedNotice)
      if (!item.isRead) {
        markNoticeRead(item.noticeId).catch(() => {})
        const index = this.noticeList.findIndex(notice => notice.noticeId === item.noticeId)
        if (index !== -1) {
          this.noticeList[index] = selectedNotice
        }
      }
    },
    openNoticeDetail(item) {
      this.selectNotice(item)
      this.$refs.noticeViewRef.open(item)
    },
    loadNoticeDetail(item) {
      if (!item || item.noticeContent != null) {
        return
      }
      this.detailLoading = true
      getNotice(item.noticeId).then(response => {
        const detail = response.data || item
        this.activeNotice = detail
        const index = this.noticeList.findIndex(notice => notice.noticeId === item.noticeId)
        if (index !== -1) {
          this.noticeList[index] = { ...this.noticeList[index], ...detail, isRead: true }
        }
      }).finally(() => {
        this.detailLoading = false
      })
    },
    plainContent(content) {
      if (!content) {
        return ''
      }
      return String(content)
        .replace(/<[^>]+>/g, '')
        .replace(/&nbsp;/g, ' ')
        .trim()
        .slice(0, 62)
    }
  }
}
</script>

<style scoped lang="scss">
.role-home {
  min-height: calc(100vh - 90px);
  color: #1f2937;
  background: #f4f8f7;
}

:deep(.embedded-dashboard) {
  min-height: auto;
  padding: 0;
  background: transparent;
}

.role-hero {
  min-height: 188px;
  padding: 30px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 28px;
  overflow: hidden;
  border-radius: 12px;
  color: #fff;
  box-shadow: 0 16px 38px rgba(15, 118, 110, .18);

  &.owner {
    background: linear-gradient(135deg, #0f766e, #2563eb);
  }

  &.worker {
    background: linear-gradient(135deg, #155e75, #0f766e 62%, #16a34a);
  }

  .eyebrow {
    margin: 0 0 8px;
    font-size: 12px;
    letter-spacing: .12em;
    opacity: .72;
  }

  h1 {
    margin: 0;
    font-size: 32px;
  }

  p:last-child {
    max-width: 660px;
    margin: 12px 0 0;
    color: rgba(255, 255, 255, .86);
    line-height: 1.8;
  }
}

.hero-stat {
  width: 172px;
  min-height: 128px;
  flex: 0 0 172px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  border: 1px solid rgba(255, 255, 255, .26);
  border-radius: 12px;
  background: rgba(255, 255, 255, .14);
  backdrop-filter: blur(8px);

  span,
  em {
    font-size: 13px;
    font-style: normal;
    opacity: .8;
  }

  strong {
    margin: 6px 0;
    font-size: 42px;
    line-height: 1;
  }
}

.order-panel,
.notice-board {
  margin-top: 16px;
  padding: 22px;
  border: 1px solid #e4efeb;
  border-radius: 10px;
  background: #fff;
  box-shadow: 0 8px 24px rgba(15, 23, 42, .05);
}

.section-head {
  margin-bottom: 18px;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;

  h2 {
    margin: 0;
    color: #102a27;
    font-size: 20px;
  }

  p {
    margin: 7px 0 0;
    color: #64748b;
  }
}

.head-actions {
  display: flex;
  gap: 8px;
}

.active-order-table {
  width: 100%;
}

.notice-shell {
  min-height: 320px;
  display: grid;
  grid-template-columns: minmax(280px, 360px) minmax(0, 1fr);
  gap: 18px;
}

.notice-list {
  overflow: hidden;
  border: 1px solid #e5eee9;
  border-radius: 8px;
  background: #f8fbfa;
}

.notice-list-item {
  width: 100%;
  padding: 16px;
  display: block;
  border: 0;
  border-bottom: 1px solid #e5eee9;
  color: inherit;
  background: transparent;
  text-align: left;
  cursor: pointer;
  transition: background .2s;

  &:last-child {
    border-bottom: 0;
  }

  &:hover,
  &.active {
    background: #ecf8f5;
  }

  &.active {
    box-shadow: inset 4px 0 0 #178f7a;
  }

  &.read h3,
  &.read p,
  &.read time {
    opacity: .62;
  }

  h3 {
    margin: 10px 0 8px;
    color: #1f2937;
    font-size: 15px;
    line-height: 1.45;
  }

  p {
    height: 42px;
    margin: 0 0 8px;
    overflow: hidden;
    color: #64748b;
    line-height: 1.5;
  }

  time {
    color: #94a3b8;
    font-size: 12px;
  }
}

.notice-item-head,
.notice-detail-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.unread-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #ef4444;
}

.notice-detail {
  padding: 22px;
  border: 1px solid #e5eee9;
  border-radius: 8px;
  background: linear-gradient(180deg, #fff, #f8fbfa);

  h3 {
    margin: 18px 0 12px;
    color: #102a27;
    font-size: 24px;
    line-height: 1.4;
  }
}

.notice-detail-head {
  color: #94a3b8;
}

.notice-content {
  min-height: 150px;
  margin-bottom: 18px;
  color: #374151;
  line-height: 1.8;
}

.notice-empty {
  min-height: 180px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #94a3b8;

  .el-icon {
    font-size: 34px;
  }
}

.detail-empty {
  height: 100%;
}

@media (max-width: 900px) {
  .notice-shell {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .role-hero,
  .section-head {
    align-items: stretch;
    flex-direction: column;
  }

  .role-hero {
    padding: 22px;
  }

  .hero-stat {
    width: 100%;
    min-height: 100px;
    flex-basis: auto;
  }

  .head-actions {
    flex-wrap: wrap;
  }
}
</style>
