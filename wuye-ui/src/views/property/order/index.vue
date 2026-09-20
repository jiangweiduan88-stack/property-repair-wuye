<template>
  <div class="app-container">
    <!-- 查询条件只负责筛选，实际数据范围由后端按照当前登录角色强制控制。 -->
    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" label-width="68px">
      <el-form-item label="工单号">
        <el-input v-model="queryParams.orderNo" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="标题">
        <el-input v-model="queryParams.title" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" clearable placeholder="请选择">
          <el-option
            v-for="d in dict.type.prop_repair_status"
            :key="d.value"
            :label="d.label"
            :value="d.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
        <el-button type="primary" icon="Plus" @click="handleAdd" v-hasPermi="['property:order:add']">
          新增报修
        </el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮根据工单状态和当前角色动态显示，帮助用户按正确顺序完成流程。 -->
    <el-table v-loading="loading" :data="list">
      <el-table-column label="工单号" prop="orderNo" width="190" />
      <el-table-column label="标题" prop="title" min-width="160" />
      <el-table-column label="分类" prop="categoryName" />
      <el-table-column label="房屋" prop="roomText" min-width="150" />
      <el-table-column label="业主" prop="ownerName" />
      <el-table-column label="现场图片" prop="images" width="100">
        <template #default="scope">
          <div
            v-if="firstImage(scope.row.images)"
            class="table-image-trigger"
            :class="{ 'is-broken': isImageBroken(firstImage(scope.row.images)) }"
            @click.stop="openTableImagePreview(scope.row.images)"
          >
            <img
              v-if="!isImageBroken(firstImage(scope.row.images))"
              :src="normalizeImageUrl(firstImage(scope.row.images))"
              alt=""
              class="table-image-thumb"
              @error="markImageBroken(firstImage(scope.row.images))"
            >
            <span v-else class="broken-image-placeholder" aria-label="图片已损毁">
              <svg viewBox="0 0 48 48" aria-hidden="true">
                <rect x="4.5" y="7.5" width="39" height="33" rx="5" />
                <circle cx="15" cy="17" r="3" />
                <path d="M9 34l9-10 7 8 5-5 9 8" />
                <path class="broken-image-crack" d="M29 4l-5 14 7-3-5 16" />
              </svg>
            </span>
          </div>
          <span v-else class="text-muted">无</span>
        </template>
      </el-table-column>
      <el-table-column label="维修人员" prop="repairUserName" />
      <el-table-column label="状态" prop="status" width="100">
        <template #default="scope">
          <dict-tag :options="dict.type.prop_repair_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="470" fixed="right">
        <template #default="scope">
          <el-button v-if="normalizeOrderStatus(scope.row) === '0' && canEditOrder(scope.row)" link type="primary" @click="handleUpdate(scope.row)">
            修改
          </el-button>
          <el-button v-if="normalizeOrderStatus(scope.row) === '6' && canEditOrder(scope.row)" link type="primary" @click="handleUpdate(scope.row)">
            编辑
          </el-button>
          <el-button v-if="canAcceptOrder(scope.row)" link type="primary" @click="simpleAction(scope.row, 'accept')">
            受理
          </el-button>
          <el-button v-if="canRejectOrder(scope.row)" link type="primary" @click="reasonAction(scope.row, 'reject')">
            驳回
          </el-button>
          <el-button
            v-if="canAssignOrder(scope.row)"
            link
            type="primary"
            @click="assignAction(scope.row)"
          >
            分配
          </el-button>
          <el-button v-if="canStartOrder(scope.row)" link type="primary" @click="simpleAction(scope.row, 'start')">
            接单
          </el-button>
          <el-button v-if="canFinishOrder(scope.row)" link type="primary" @click="finishAction(scope.row)">
            完成
          </el-button>
          <el-button v-if="canConfirmOrder(scope.row)" link type="primary" @click="confirmAction(scope.row)">
            确认
          </el-button>
          <el-button v-if="canReworkOrder(scope.row)" link type="primary" @click="reasonAction(scope.row, 'rework')">
            返工
          </el-button>
          <el-button v-if="canCancelOrder(scope.row)" link type="primary" @click="simpleAction(scope.row, 'cancel')">
            取消
          </el-button>
          <el-button link type="primary" icon="View" @click="showDetail(scope.row)" v-hasPermi="['property:order:query']">
            详情
          </el-button>
          <el-button link type="primary" icon="Document" @click="showLogs(scope.row)">记录</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" v-model="open" width="680px" append-to-body>
      <el-form ref="form" :model="form" :rules="formRules" label-width="90px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请简单描述报修问题" />
        </el-form-item>
        <el-form-item label="房屋" prop="roomId">
          <el-select v-model="form.roomId" placeholder="请选择房屋" filterable style="width: 100%">
            <el-option v-for="room in roomOptions" :key="room.roomId" :label="formatRoom(room)" :value="room.roomId" />
          </el-select>
        </el-form-item>
        <el-form-item label="报修分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择报修分类" filterable style="width: 100%">
            <el-option
              v-for="category in categoryOptions"
              :key="category.categoryId"
              :label="category.categoryName"
              :value="category.categoryId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="报修内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请填写具体位置、故障现象等信息" />
        </el-form-item>
        <el-form-item label="现场图片">
          <image-upload v-model="form.images" :limit="3" :file-size="5" />
          <div class="form-tip">可上传现场照片，维修人员可在工单列表中直接预览。</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="open = false">取消</el-button>
      </template>
    </el-dialog>

    <el-image-viewer
      v-if="previewOpen"
      :url-list="previewImages"
      :initial-index="previewIndex"
      :z-index="previewViewerZIndex"
      :hide-on-click-modal="false"
      teleported
      @switch="handlePreviewSwitch"
      @close="closeImagePreview"
    />

    <el-dialog title="工单详情" v-model="detailOpen" width="760px" append-to-body>
      <div class="detail-grid">
        <div class="detail-item">
          <span class="detail-label">工单号</span>
          <span class="detail-value">{{ formatValue(detailForm.orderNo) }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">状态</span>
          <span class="detail-value">
            <dict-tag :options="dict.type.prop_repair_status" :value="detailForm.status" />
          </span>
        </div>
        <div class="detail-item">
          <span class="detail-label">标题</span>
          <span class="detail-value">{{ formatValue(detailForm.title) }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">报修分类</span>
          <span class="detail-value">{{ formatValue(detailForm.categoryName) }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">房屋</span>
          <span class="detail-value">{{ formatValue(detailForm.roomText) }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">业主</span>
          <span class="detail-value">{{ formatValue(detailForm.ownerName) }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">联系电话</span>
          <span class="detail-value">{{ formatValue(detailForm.ownerPhone) }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">维修人员</span>
          <span class="detail-value">{{ formatValue(detailForm.repairUserName) }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">提交时间</span>
          <span class="detail-value">{{ formatValue(detailForm.createTime) }}</span>
        </div>
        <div class="detail-item">
          <span class="detail-label">最后更新</span>
          <span class="detail-value">{{ formatValue(detailForm.updateTime) }}</span>
        </div>
        <div class="detail-item detail-item-full">
          <span class="detail-label">报修内容</span>
          <div class="detail-block">{{ formatValue(detailForm.content) }}</div>
        </div>
        <div class="detail-item detail-item-full">
          <span class="detail-label">现场图片</span>
          <div v-if="detailImageList.length" class="detail-image-list">
            <div
              v-for="(image, index) in detailImageList"
              :key="`${detailForm.orderId || 'detail'}-${index}`"
              class="detail-image-trigger"
              @click.stop="openImagePreview(detailImageList, index)"
            >
              <img :src="normalizeImageUrl(image)" alt="鐜板満鍥剧墖" class="detail-image-thumb">
            </div>
          </div>
          <div v-else class="detail-block text-muted">暂无图片</div>
        </div>
        <div class="detail-item detail-item-full" v-if="detailForm.finishResult">
          <span class="detail-label">维修结果</span>
          <div class="detail-block">{{ detailForm.finishResult }}</div>
        </div>
        <div class="detail-item detail-item-full" v-if="detailForm.finishImages">
          <span class="detail-label">完工图片</span>
          <div class="detail-image-list">
            <div
              v-for="(image, index) in splitImageList(detailForm.finishImages)"
              :key="`${detailForm.orderId || 'finish'}-${index}`"
              class="detail-image-trigger"
              @click.stop="openImagePreview(detailForm.finishImages, index)"
            >
              <img :src="normalizeImageUrl(image)" alt="瀹屽伐鍥剧墖" class="detail-image-thumb">
            </div>
          </div>
        </div>
        <div class="detail-item detail-item-full" v-if="shouldShowRejectReason(detailForm.status)">
          <span class="detail-label">驳回或返工原因</span>
          <div class="detail-block">{{ formatValue(detailForm.rejectReason) }}</div>
        </div>
        <div class="detail-item detail-item-full" v-if="detailForm.remark">
          <span class="detail-label">备注</span>
          <div class="detail-block">{{ detailForm.remark }}</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="detailOpen = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog title="分配工单" v-model="assignOpen" width="520px" append-to-body>
      <el-form :model="assignForm" label-width="90px">
        <el-form-item label="维修人员">
          <el-select v-model="assignForm.repairUserId" placeholder="请选择维修人员" filterable style="width: 100%">
            <el-option
              v-for="user in repairUserOptions"
              :key="user.userId"
              :label="formatRepairUser(user)"
              :value="user.userId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitAssign">确定</el-button>
        <el-button @click="assignOpen = false">取消</el-button>
      </template>
    </el-dialog>

    <el-dialog title="确认完成并评价" v-model="confirmOpen" width="560px" append-to-body>
      <el-form :model="confirmForm" label-width="90px">
        <el-form-item label="服务评分">
          <el-rate
            v-model="confirmForm.score"
            :max="5"
            :texts="['非常不满意', '不满意', '一般', '满意', '非常满意']"
            show-text
          />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="confirmForm.content" type="textarea" :rows="4" placeholder="可以填写本次维修服务体验" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitConfirm">确定</el-button>
        <el-button @click="confirmOpen = false">取消</el-button>
      </template>
    </el-dialog>

    <!-- 返工使用独立表单，以支持多行原因、字数统计和明确的业务标题。 -->
    <el-dialog title="维修返工" v-model="reworkOpen" width="560px" append-to-body>
      <el-form ref="reworkForm" :model="reworkForm" :rules="reworkRules" label-width="90px">
        <el-form-item label="返工原因" prop="reason">
          <el-input
            v-model="reworkForm.reason"
            type="textarea"
            :rows="6"
            maxlength="300"
            show-word-limit
            placeholder="请输入返工原因，300字以内"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitRework">确定</el-button>
        <el-button @click="reworkOpen = false">取消</el-button>
      </template>
    </el-dialog>

    <el-dialog title="工单操作记录" v-model="logsOpen" width="700px" append-to-body>
      <el-timeline v-if="logs.length > 0">
        <el-timeline-item
          v-for="item in logs"
          :key="item.logId"
          :timestamp="item.createTime"
          placement="top"
          :type="logType(item.action)"
        >
          <el-card shadow="hover">
            <div class="log-card-header">
              <el-tag :type="logTagType(item.action)" size="small">{{ formatLogLabel(item) }}</el-tag>
              <span v-if="item.operatorName" class="log-operator">操作人：{{ item.operatorName }}</span>
            </div>
            <div v-if="formatLogContent(item)" class="log-content">{{ formatLogContent(item) }}</div>
          </el-card>
        </el-timeline-item>
      </el-timeline>
      <div v-else class="no-logs">
        <el-icon><InfoFilled /></el-icon> 暂无操作记录
      </div>
      <template #footer>
        <el-button @click="logsOpen = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ElImageViewer } from 'element-plus'
import { isExternal } from '@/utils/validate'
import useUserStore from '@/store/modules/user'
import { getDicts } from '@/api/system/dict/data'
import {
  listOrder,
  addOrder,
  updateOrder,
  acceptOrder,
  rejectOrder,
  assignOrder,
  repairUsers,
  startOrder,
  finishOrder,
  confirmOrder,
  cancelOrder,
  reworkOrder,
  orderLogs
} from '@/api/property/order'
import { categoryOptions } from '@/api/property/category'
import { roomOptions } from '@/api/property/room'

// 页面负责工单状态操作、详情、图片预览和操作时间线展示。
// 按钮权限用于改善操作体验，最终权限与状态合法性仍由后端验证。
export default {
  name: 'PropertyOrder',
  components: { ElImageViewer },
  data() {
    return {
      loading: true,
      list: [],
      total: 0,
      open: false,
      previewOpen: false,
      detailOpen: false,
      assignOpen: false,
      confirmOpen: false,
      reworkOpen: false,
      logsOpen: false,
      title: '',
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNo: '',
        title: '',
        status: ''
      },
      form: {},
      formRules: {
        title: [
          { required: true, message: '请填写报修标题', trigger: 'blur' },
          { max: 120, message: '报修标题长度不能超过120个字符', trigger: 'blur' }
        ],
        roomId: [{ required: true, message: '请选择报修房屋', trigger: 'change' }],
        categoryId: [{ required: true, message: '请选择报修分类', trigger: 'change' }],
        content: [
          { required: true, message: '请填写报修内容', trigger: 'blur' },
          { max: 1000, message: '报修内容长度不能超过1000个字符', trigger: 'blur' }
        ]
      },
      reworkForm: { reason: '' },
      reworkRules: {
        reason: [
          { required: true, message: '请填写返工原因', trigger: 'blur' },
          { max: 300, message: '返工原因长度不能超过300个字符', trigger: 'blur' }
        ]
      },
      detailForm: {},
      previewImages: [],
      previewIndex: 0,
      previewRequestId: 0,
      previewViewerZIndex: 3000,
      previewCounterZIndex: 3002,
      dict: { type: { prop_repair_status: [] } },
      assignForm: {},
      confirmForm: {},
      logs: [],
      currentAssignOrder: null,
      currentConfirmOrder: null,
      currentReworkOrder: null,
      categoryOptions: [],
      roomOptions: [],
      repairUserOptions: [],
      brokenImageUrls: {}
    }
  },
  computed: {
    currentUserId() {
      return Number(useUserStore().id || 0)
    },
    currentRoles() {
      return useUserStore().roles || []
    },
    canManageOrder() {
      // 管理角色处理全量工单，普通角色只能操作与自己相关的工单。
      return this.currentRoles.includes('admin') || this.currentRoles.includes('system_admin') || this.currentRoles.includes('property_manager')
    },
    isRepairWorkerRole() {
      return this.currentRoles.includes('repair_worker')
    },
    isOwnerRole() {
      return this.currentRoles.includes('property_owner')
    },
    detailImageList() {
      return this.splitImageList(this.detailForm.images)
    }
  },
  created() {
    this.loadStatusDict()
    this.getList()
    this.getOptions()
  },
  beforeUnmount() {
    this.removePreviewCounter()
  },
  methods: {
    loadStatusDict() {
      getDicts('prop_repair_status').then(response => {
        this.dict.type.prop_repair_status = (response.data || []).map(item => ({
          label: item.dictLabel ?? item.label,
          value: String(item.dictValue ?? item.value ?? ''),
          elTagType: item.listClass ?? item.elTagType ?? '',
          elTagClass: item.cssClass ?? item.elTagClass ?? ''
        }))
      })
    },
    normalizeOrderStatus(row) {
      return String((row && row.status) || '')
    },
    isCurrentOwner(row) {
      return !!row && Number(row.ownerId || 0) === this.currentUserId
    },
    isCurrentRepairUser(row) {
      return !!row && Number(row.repairUserId || 0) === this.currentUserId
    },
    canEditOrder(row) {
      return ['0', '6'].includes(this.normalizeOrderStatus(row)) && (this.isCurrentOwner(row) || this.canManageOrder)
    },
    canAcceptOrder(row) {
      return this.normalizeOrderStatus(row) === '0' && this.canManageOrder
    },
    canRejectOrder(row) {
      return this.normalizeOrderStatus(row) === '0' && this.canManageOrder
    },
    canAssignOrder(row) {
      return ['1', '8'].includes(this.normalizeOrderStatus(row)) && this.canManageOrder
    },
    canStartOrder(row) {
      return this.normalizeOrderStatus(row) === '2' && (this.isCurrentRepairUser(row) || this.canManageOrder || this.isRepairWorkerRole)
    },
    canFinishOrder(row) {
      return this.normalizeOrderStatus(row) === '3' && (this.isCurrentRepairUser(row) || this.canManageOrder || this.isRepairWorkerRole)
    },
    canConfirmOrder(row) {
      return this.normalizeOrderStatus(row) === '4' && (this.isCurrentOwner(row) || this.isOwnerRole)
    },
    canReworkOrder(row) {
      return this.normalizeOrderStatus(row) === '4' && (this.isCurrentOwner(row) || this.isOwnerRole || this.canManageOrder)
    },
    canCancelOrder(row) {
      return this.normalizeOrderStatus(row) === '0' && (this.isCurrentOwner(row) || this.canManageOrder)
    },
    getList() {
      this.loading = true
      listOrder(this.queryParams).then(response => {
        this.list = response.rows
        this.total = response.total
      }).finally(() => {
        this.loading = false
      })
    },
    getOptions() {
      categoryOptions().then(response => {
        this.categoryOptions = response.data || []
      })
      roomOptions().then(response => {
        this.roomOptions = response.data || []
      })
      if (this.$auth.hasPermi('property:order:assign')) {
        this.loadRepairUsers()
      }
    },
    loadRepairUsers() {
      return repairUsers().then(response => {
        this.repairUserOptions = response.data || []
      })
    },
    formatRoom(room) {
      const parts = [room.buildingName, room.unitNo, room.roomNo].filter(Boolean)
      return parts.join(' ') || `房屋${room.roomId}`
    },
    formatRepairUser(user) {
      const name = user.nickName || user.userName || ''
      const phone = user.phonenumber ? `（${user.phonenumber}）` : ''
      return `${name}${phone}`
    },
    formatValue(value) {
      return value || '-'
    },
    splitImageList(images) {
      if (!images) {
        return []
      }
      if (Array.isArray(images)) {
        return images.map(item => (item || '').trim()).filter(Boolean)
      }
      return String(images).split(',').map(item => item.trim()).filter(Boolean)
    },
    firstImage(images) {
      return this.splitImageList(images)[0] || ''
    },
    isImageBroken(image) {
      return !!this.brokenImageUrls[this.normalizeImageUrl(image)]
    },
    markImageBroken(image) {
      const imageUrl = this.normalizeImageUrl(image)
      if (imageUrl) {
        this.brokenImageUrls[imageUrl] = true
      }
    },
    openTableImagePreview(images) {
      if (!this.isImageBroken(this.firstImage(images))) {
        this.openImagePreview(images)
      }
    },
    normalizeImageUrl(image) {
      if (!image) {
        return ''
      }
      return isExternal(image) ? image : import.meta.env.VITE_APP_BASE_API + image
    },
    async openImagePreview(images, initialIndex = 0) {
      // 请求编号用于丢弃较早的异步结果，避免连续点击导致预览图片错位。
      const requestId = ++this.previewRequestId
      const sourceImages = this.splitImageList(images).map(item => this.normalizeImageUrl(item))
      const selectedImage = sourceImages[Math.min(Math.max(initialIndex, 0), Math.max(sourceImages.length - 1, 0))]
      const loadResults = await Promise.all(sourceImages.map(image => this.loadPreviewImage(image)))
      if (requestId !== this.previewRequestId) {
        return
      }
      this.previewImages = loadResults.filter(Boolean)
      if (!this.previewImages.length) {
        this.$modal.msgWarning('图片文件不存在或暂时无法访问')
        return
      }
      this.previewIndex = Math.max(this.previewImages.indexOf(selectedImage), 0)
      this.previewViewerZIndex = this.getPreviewViewerZIndex()
      this.previewCounterZIndex = this.previewViewerZIndex + 2
      this.previewOpen = this.previewImages.length > 0
      if (this.previewOpen) {
        this.$nextTick(() => {
          this.renderPreviewCounter()
        })
      }
    },
    getPreviewViewerZIndex() {
      // 动态高于当前弹窗遮罩，避免详情内打开图片时查看器被弹窗覆盖。
      const overlayZIndexes = Array.from(document.body.querySelectorAll('.el-overlay, .el-popper'))
        .map(element => Number.parseInt(window.getComputedStyle(element).zIndex, 10))
        .filter(Number.isFinite)
      return Math.max(3000, ...overlayZIndexes) + 10
    },
    loadPreviewImage(imageUrl) {
      return new Promise(resolve => {
        const image = new Image()
        let settled = false
        const finish = result => {
          if (settled) {
            return
          }
          settled = true
          clearTimeout(timer)
          resolve(result)
        }
        const timer = setTimeout(() => finish(null), 8000)
        image.onload = () => finish(imageUrl)
        image.onerror = () => finish(null)
        image.src = imageUrl
      })
    },
    handlePreviewSwitch(index) {
      this.previewIndex = index
      this.renderPreviewCounter()
    },
    closeImagePreview() {
      this.previewRequestId += 1
      this.previewOpen = false
      this.removePreviewCounter()
      this.previewImages = []
      this.previewIndex = 0
    },
    renderPreviewCounter() {
      // 计数器挂载到 body，避免查看器重复打开时被组件内部销毁或裁剪。
      if (!this.previewOpen || !this.previewImages.length) {
        this.removePreviewCounter()
        return
      }
      if (!this.previewCounterEl) {
        this.previewCounterEl = document.createElement('div')
      }
      this.previewCounterEl.textContent = `${this.previewIndex + 1}/${this.previewImages.length}`
      this.previewCounterEl.style.cssText = [
        'position: fixed',
        'left: 50%',
        'bottom: 96px',
        'z-index: ' + this.previewCounterZIndex,
        'transform: translateX(-50%)',
        'min-width: 72px',
        'padding: 8px 14px',
        'border-radius: 999px',
        'background: rgba(15, 23, 42, 0.78)',
        'text-align: center',
        'color: #fff',
        'font-size: 14px',
        'font-weight: 600',
        'line-height: 1',
        'pointer-events: none'
      ].join(';')
      if (!document.body.contains(this.previewCounterEl)) {
        document.body.appendChild(this.previewCounterEl)
      }
    },
    removePreviewCounter() {
      if (this.previewCounterEl && this.previewCounterEl.parentNode) {
        this.previewCounterEl.parentNode.removeChild(this.previewCounterEl)
      }
    },
    shouldShowRejectReason(status) {
      return String(status) === '6' || String(status) === '8'
    },
    reset() {
      this.form = {
        orderId: null,
        title: '',
        roomId: null,
        categoryId: null,
        content: '',
        images: ''
      }
    },
    resetDetail() {
      this.detailForm = {}
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      // 重建查询对象，确保输入框和选择框中的残留条件都被清空。
      this.resetForm('queryForm')
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        orderNo: '',
        title: '',
        status: ''
      }
      this.handleQuery()
    },
    handleAdd() {
      this.reset()
      this.title = '新增报修'
      this.open = true
    },
    showDetail(row) {
      this.resetDetail()
      this.detailForm = { ...row }
      this.detailOpen = true
    },
    handleUpdate(row) {
      this.form = { ...row }
        this.title = row.status === '6' ? '编辑报修（驳回重提）' : '修改报修'
        this.open = true
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const api = this.form.orderId ? updateOrder : addOrder
        api(this.form).then(() => {
          this.$modal.msgSuccess('保存成功')
          this.open = false
          this.getList()
        })
      })
    },
    simpleAction(row, type) {
      const map = {
        accept: acceptOrder,
        start: startOrder,
        cancel: cancelOrder
      }
      map[type](row.orderId).then(() => {
        this.$modal.msgSuccess('操作成功')
        this.getList()
      })
    },
    reasonAction(row, type) {
      // 返工需要较完整的说明，因此使用独立对话框；驳回保留简短原因输入。
      if (type === 'rework') {
        this.reworkForm = { reason: '' }
        this.currentReworkOrder = row
        this.reworkOpen = true
        this.$nextTick(() => this.$refs.reworkForm?.clearValidate())
        return
      }
      this.$modal.prompt('请输入原因', '提示', {
        inputPattern: /\S+/,
        inputErrorMessage: '原因不能为空'
      }).then(({ value }) => {
        const api = type === 'reject' ? rejectOrder : reworkOrder
        return api(row.orderId, value)
      }).then(() => {
        this.$modal.msgSuccess('操作成功')
        this.getList()
      }).catch(() => {})
    },
    submitRework() {
      this.$refs.reworkForm.validate(valid => {
        if (!valid) return
        reworkOrder(this.currentReworkOrder.orderId, this.reworkForm.reason).then(() => {
          this.$modal.msgSuccess('返工申请已提交')
          this.reworkOpen = false
          this.getList()
        })
      })
    },
    assignAction(row) {
      this.currentAssignOrder = row
      this.assignForm = { repairUserId: row.repairUserId || null }
      this.assignOpen = true
      if (!this.repairUserOptions.length) {
        this.loadRepairUsers()
      }
    },
    submitAssign() {
      if (!this.assignForm.repairUserId) {
        this.$modal.msgWarning('请选择维修人员')
        return
      }
      const user = this.repairUserOptions.find(item => item.userId === this.assignForm.repairUserId)
      assignOrder(this.currentAssignOrder.orderId, {
        repairUserId: this.assignForm.repairUserId,
        repairUserName: user ? (user.nickName || user.userName) : ''
      }).then(() => {
        this.$modal.msgSuccess('分配成功')
        this.assignOpen = false
        this.getList()
      })
    },
    confirmAction(row) {
      this.currentConfirmOrder = row
      this.confirmForm = { score: 5, content: '' }
      this.confirmOpen = true
    },
    submitConfirm() {
      if (!this.confirmForm.score) {
        this.$modal.msgWarning('请选择服务评分')
        return
      }
      confirmOrder(this.currentConfirmOrder.orderId, this.confirmForm).then(() => {
        this.$modal.msgSuccess('确认完成，评价已生成')
        this.confirmOpen = false
        this.getList()
      })
    },
    finishAction(row) {
      this.$modal.prompt('请输入维修结果', '提示', {
        inputPattern: /\S+/,
        inputErrorMessage: '维修结果不能为空'
      }).then(({ value }) => {
        return finishOrder(row.orderId, { finishResult: value })
      }).then(() => {
        this.$modal.msgSuccess('操作成功')
        this.getList()
      }).catch(() => {})
    },
    showLogs(row) {
      orderLogs(row.orderId).then(response => {
        this.logs = response.data || []
        this.logsOpen = true
      })
    },
    formatLogLabel(item) {
      const map = {
        submit: '已提交',
        resubmit: '重新提交',
        update: '已修改',
        accept: '已受理',
        reject: '已驳回',
        assign: '已分配',
        start: '维修中',
        finish: '待确认',
        confirm: '已完成',
        cancel: '已取消',
        rework: '返工中'
      }
      return map[item.action] || item.actionLabel || '状态变更'
    },
    formatLogContent(item) {
      // 将历史英文日志前缀转换为中文，保证新旧操作记录展示口径一致。
      const defaultMap = {
        submit: '提交了工单',
        resubmit: '驳回后重新提交了工单',
        update: '修改了工单信息',
        accept: '受理了工单',
        assign: '已完成分配',
        start: '开始处理工单',
        finish: '提交了维修结果',
        confirm: '已确认工单完成',
        cancel: '取消了工单',
        reject: '驳回了工单',
        rework: '发起了返工'
      }
      let content = item.content || ''
      if (!content) {
        return defaultMap[item.action] || ''
      }
      return content
        .replace(/^Reason:\s*/, '原因：')
        .replace(/^Assigned to:\s*/, '分配给：')
        .replace(/^Result:\s*/, '维修结果：')
        .replace(/^Score:\s*/, '评分：')
        .replace(/,\s*Comment:\s*/g, '，评价：')
        .replace(/^Resubmitted after rejection$/g, '驳回后重新提交工单')
    },
    logType(action) {
      const map = {
        submit: 'primary',
        resubmit: 'primary',
        update: 'warning',
        accept: 'success',
        reject: 'danger',
        assign: 'warning',
        start: 'info',
        finish: 'success',
        confirm: 'success',
        cancel: 'info',
        rework: 'danger'
      }
      return map[action] || 'info'
    },
    logTagType(action) {
      const map = {
        submit: '',
        resubmit: 'warning',
        update: 'warning',
        accept: 'success',
        reject: 'danger',
        assign: '',
        start: '',
        finish: 'success',
        confirm: '',
        cancel: 'info',
        rework: 'danger'
      }
      return map[action] || ''
    }
  }
}
</script>

<style scoped>
.text-muted {
  color: #94a3b8;
}

.table-image-trigger {
  position: relative;
  display: inline-flex;
  width: 48px;
  height: 48px;
  cursor: pointer;
}

.table-image-trigger.is-broken {
  cursor: default;
}

.table-image-thumb {
  width: 48px;
  height: 48px;
  object-fit: cover;
  border-radius: 6px;
  box-shadow: 0 6px 16px rgba(15, 23, 42, 0.16);
}

.broken-image-placeholder {
  width: 48px;
  height: 48px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  color: #94a3b8;
  background: #f1f5f9;
  box-shadow: inset 0 0 0 1px #dbe3ec;
}

.broken-image-placeholder svg {
  width: 38px;
  height: 38px;
  fill: none;
  stroke: currentColor;
  stroke-width: 2;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.broken-image-placeholder .broken-image-crack {
  color: #ef6a6a;
  stroke: currentColor;
  stroke-width: 2.5;
}

.form-tip {
  margin-top: 6px;
  color: #94a3b8;
  font-size: 12px;
  line-height: 1.5;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px 18px;
}

.detail-item {
  padding: 14px 16px;
  border-radius: 10px;
  background: #f8fafc;
}

.detail-item-full {
  grid-column: 1 / -1;
}

.detail-label {
  display: block;
  margin-bottom: 8px;
  color: #64748b;
  font-size: 12px;
  line-height: 1;
}

.detail-value {
  color: #0f172a;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-all;
}

.detail-block {
  color: #0f172a;
  font-size: 14px;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}

.detail-image-list {
  display: grid;
  grid-template-columns: repeat(6, 72px);
  gap: 12px;
}

.detail-image-trigger {
  display: inline-flex;
  width: 72px;
  height: 72px;
  cursor: pointer;
}

.detail-image-thumb {
  width: 72px;
  height: 72px;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 8px 18px rgba(15, 23, 42, 0.16);
}

.log-card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 6px;
}

.log-operator {
  color: #909399;
  font-size: 12px;
}

.log-content {
  color: #606266;
  font-size: 13px;
  line-height: 1.6;
  white-space: pre-wrap;
}

.no-logs {
  text-align: center;
  padding: 40px 0;
  color: #909399;
  font-size: 14px;
}

.no-logs i {
  margin-right: 6px;
}

@media (max-width: 900px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }

  .detail-image-list {
    grid-template-columns: repeat(3, 72px);
  }
}
</style>
