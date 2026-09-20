<template>
  <div class="app-container">
    <!-- 房屋页面维护楼栋、房号与业主登录账号之间的绑定关系。 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="68px">
      <el-form-item label="房号"><el-input v-model="queryParams.roomNo" clearable @keyup.enter="handleQuery"/></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button><el-button type="primary" icon="Plus" @click="handleAdd" v-hasPermi="['property:room:add']">新增</el-button></el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="list">
      <el-table-column label="编号" prop="roomId" width="80"/><el-table-column label="楼栋" prop="buildingName"/><el-table-column label="单元" prop="unitNo"/><el-table-column label="房号" prop="roomNo"/><el-table-column label="业主" prop="ownerName"/><el-table-column label="电话" prop="ownerPhone"/>
      <el-table-column label="操作" width="160"><template #default="s"><el-button link type="primary" @click="handleUpdate(s.row)" v-hasPermi="['property:room:edit']">修改</el-button><el-button link type="primary" @click="handleDelete(s.row)" v-hasPermi="['property:room:remove']">删除</el-button></template></el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList"/>
    <el-dialog :title="title" v-model="open" width="520px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="楼栋" prop="buildingId">
          <el-select v-model="form.buildingId" placeholder="请选择楼栋" filterable style="width: 100%">
            <el-option v-for="b in buildingOptions" :key="b.buildingId" :label="b.buildingName" :value="b.buildingId" />
          </el-select>
        </el-form-item>
        <el-form-item label="单元" prop="unitNo"><el-input v-model="form.unitNo" placeholder="例如：1单元"/></el-form-item>
        <el-form-item label="房号" prop="roomNo"><el-input v-model="form.roomNo" placeholder="例如：101"/></el-form-item>
        <el-form-item label="业主姓名" prop="ownerName"><el-input v-model="form.ownerName" maxlength="20" placeholder="请输入业主姓名"/></el-form-item>
        <el-form-item label="登录账号" prop="loginAccount"><el-input v-model="form.loginAccount" maxlength="20" placeholder="请输入业主登录账号"/></el-form-item>
        <el-form-item label="电话" prop="ownerPhone"><el-input v-model="form.ownerPhone" placeholder="请输入联系电话"/></el-form-item>
        <el-form-item label="状态"><el-radio-group v-model="form.status"><el-radio value="0">正常</el-radio><el-radio value="1">停用</el-radio></el-radio-group></el-form-item>
      </el-form>
      <template #footer><el-button type="primary" @click="submitForm">确定</el-button><el-button @click="open=false">取消</el-button></template>
    </el-dialog>
  </div>
</template>
<script>
import { listRoom, getRoom, addRoom, updateRoom, delRoom } from '@/api/property/room'
import { listBuilding } from '@/api/property/building'

// 页面先校验输入格式，后端再验证房屋唯一性和业主账号一致性。
export default {
  name: 'PropertyRoom',
  data() {
    return {
      loading: true,
      list: [],
      total: 0,
      open: false,
      title: '',
      queryParams: { pageNum: 1, pageSize: 10 },
      form: {},
      buildingOptions: [],
      rules: {
        buildingId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
        unitNo: [{ max: 30, message: '单元长度不能超过30个字符', trigger: 'blur' }],
        roomNo: [{ required: true, message: '请填写房号', trigger: 'blur' }, { max: 30, message: '房号长度不能超过30个字符', trigger: 'blur' }],
        ownerName: [{ required: true, message: '请填写业主姓名', trigger: 'blur' }, { min: 2, max: 20, message: '业主姓名长度应为2至20个字符', trigger: 'blur' }],
        loginAccount: [{ required: true, message: '请填写登录账号', trigger: 'blur' }, { min: 2, max: 20, message: '登录账号长度应为2至20个字符', trigger: 'blur' }],
        ownerPhone: [{ required: true, message: '请填写手机号码', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的11位手机号码', trigger: 'blur' }]
      }
    }
  },
  created() { this.getList(); this.loadBuildings() },
  methods: {
    getList() { this.loading = true; listRoom(this.queryParams).then(r => { this.list = r.rows; this.total = r.total; this.loading = false }) },
    // 新增房屋只允许选择正常楼栋，避免数据关联到已停用楼栋。
    loadBuildings() { listBuilding({ status: '0' }).then(r => { this.buildingOptions = r.rows || [] }) },
    reset() { this.form = { status: '0', loginAccount: '' } },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.resetForm('queryForm'); this.handleQuery() },
    handleAdd() { this.reset(); this.title = '新增房屋'; this.open = true },
    handleUpdate(row) { getRoom(row.roomId).then(r => { this.form = r.data; this.title = '修改房屋'; this.open = true }) },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const api = this.form.roomId ? updateRoom : addRoom
        api(this.form).then(() => { this.$modal.msgSuccess('保存成功'); this.open = false; this.getList() })
      })
    },
    handleDelete(row) { this.$modal.confirm('确认删除该房屋吗？').then(() => delRoom(row.roomId)).then(() => { this.$modal.msgSuccess('删除成功'); this.getList() }) }
  }
}
</script>
