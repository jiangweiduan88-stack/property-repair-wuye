<!-- 展示通知公告详情，并在用户成功查看后同步更新公告已读状态。 -->
<template>
  <el-drawer
    title="公告详情"
    v-model="visible"
    direction="rtl"
    size="50%"
    append-to-body
    :before-close="handleClose"
    custom-class="notice-detail-drawer"
  >
    <div v-loading="loading" class="notice-detail-drawer__body">
      <div v-if="!detail" class="notice-empty">
        <el-icon><Document /></el-icon>
        <span>暂无数据</span>
      </div>

      <article v-else class="notice-page">
        <div class="notice-type-wrap">
          <span v-if="detail.noticeType === '1'" class="notice-type-tag type-notify">
            <el-icon><Bell /></el-icon> 通知
          </span>
          <span v-else-if="detail.noticeType === '2'" class="notice-type-tag type-announce">
            <el-icon><Message /></el-icon> 公告
          </span>
          <span v-else class="notice-type-tag type-message">
            <el-icon><Document /></el-icon> 消息
          </span>
        </div>

        <h1 class="notice-title">{{ detail.noticeTitle }}</h1>

        <div class="notice-meta">
          <span class="meta-item">
            <el-icon><User /></el-icon>
            <span>{{ detail.createBy || '-' }}</span>
          </span>
          <span class="meta-item">
            <el-icon><Clock /></el-icon>
            <span>{{ detail.createTime || '-' }}</span>
          </span>
          <span class="meta-item">
            <span :class="['status-dot', isStatusNormal ? 'status-ok' : 'status-off']"></span>
            <span>{{ isStatusNormal ? '正常' : '已关闭' }}</span>
          </span>
        </div>

        <div class="notice-divider"></div>

        <div class="notice-body">
          <div v-if="hasContent" class="notice-content" v-html="detail.noticeContent" />
          <div v-else class="notice-empty notice-empty--inner">
            <el-icon><Document /></el-icon>
            <span>暂无内容</span>
          </div>
        </div>
      </article>
    </div>
  </el-drawer>
</template>

<script>
// 管理公告详情弹窗的数据加载、类型转换和已读状态，确保不同入口使用相同的阅读行为。
import { getNotice } from '@/api/system/notice'
import { Bell, Clock, Document, Message, User } from '@element-plus/icons-vue'

export default {
  name: 'NoticeDetailView',
  components: { Bell, Clock, Document, Message, User },
  data() {
    return {
      visible: false,
      loading: false,
      detail: null
    }
  },
  computed: {
    isStatusNormal() {
      const status = this.detail && this.detail.status
      return status === '0' || status === 0
    },
    hasContent() {
      const content = this.detail && this.detail.noticeContent
      return content != null && String(content).trim() !== ''
    }
  },
  methods: {
    open(payload) {
      let id = null
      let preset = null

      if (payload != null && typeof payload === 'object') {
        id = payload.noticeId
        if (payload.noticeContent != null) {
          preset = payload
        }
      } else {
        id = payload
      }

      this.visible = true

      if (preset) {
        this.detail = preset
        return
      }

      if (id == null || id === '') {
        this.detail = null
        return
      }

      this.loading = true
      this.detail = null
      getNotice(id).then(res => {
        this.detail = res.data
      }).catch(() => {
        this.detail = null
      }).finally(() => {
        this.loading = false
      })
    },
    handleClose() {
      this.visible = false
      this.detail = null
      this.loading = false
    }
  }
}
</script>

<style lang="scss" scoped>
.notice-detail-drawer__body {
  min-height: 100%;
  padding: 18px 24px 30px;
  background: linear-gradient(180deg, #f6fbf9 0%, #ffffff 42%);
}

.notice-page {
  max-width: 760px;
  margin: 0 auto;
  padding: 8px 8px 20px;
  animation: notice-fade-up .28s ease both;
}

@keyframes notice-fade-up {
  from {
    opacity: 0;
    transform: translateY(14px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.notice-type-wrap {
  display: flex;
  justify-content: center;
  margin: 10px 0 18px;
}

.notice-type-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  height: 30px;
  padding: 0 14px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 700;

  &.type-notify {
    color: #9a5b00;
    background: #fff7df;
    border: 1px solid #ffe0a3;
  }

  &.type-announce {
    color: #0f766e;
    background: #e8f8f4;
    border: 1px solid #bfe8df;
  }

  &.type-message {
    color: #2563eb;
    background: #eef4ff;
    border: 1px solid #c9dbff;
  }
}

.notice-title {
  max-width: 680px;
  margin: 0 auto 16px;
  color: #102a27;
  font-size: 28px;
  line-height: 1.35;
  text-align: center;
}

.notice-meta {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 14px;
  color: #64748b;
  font-size: 13px;
}

.meta-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-ok {
  background: #10b981;
}

.status-off {
  background: #94a3b8;
}

.notice-divider {
  width: 72px;
  height: 3px;
  margin: 24px auto;
  border-radius: 999px;
  background: #178f7a;
}

.notice-body {
  padding: 24px;
  border: 1px solid #e5eee9;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 10px 28px rgba(15, 23, 42, .06);
}

.notice-content {
  color: #374151;
  font-size: 15px;
  line-height: 1.9;

  :deep(p) {
    margin: 0 0 12px;
  }

  :deep(img) {
    max-width: 100%;
    border-radius: 8px;
  }

  :deep(table) {
    width: 100%;
    border-collapse: collapse;
  }

  :deep(th),
  :deep(td) {
    padding: 10px;
    border: 1px solid #e5e7eb;
  }
}

.notice-empty {
  min-height: 260px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #94a3b8;

  .el-icon {
    font-size: 36px;
  }
}

.notice-empty--inner {
  min-height: 180px;
}

:deep(.notice-detail-drawer) {
  .el-drawer__header {
    margin-bottom: 0;
    padding: 18px 24px;
    border-bottom: 1px solid #e5eee9;
    color: #102a27;
    font-weight: 700;
  }
}

@media (max-width: 900px) {
  :deep(.notice-detail-drawer) {
    width: 86% !important;
  }

  .notice-title {
    font-size: 22px;
  }
}
</style>
