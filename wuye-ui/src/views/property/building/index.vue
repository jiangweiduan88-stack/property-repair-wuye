<template>
  <div class="app-container">
    <!-- 楼栋基础资料为房屋和工单位置关联提供上级数据。 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="68px">
      <el-form-item label="楼栋名称" prop="buildingName"><el-input v-model="queryParams.buildingName" clearable @keyup.enter="handleQuery"/></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
    </el-form>
    <el-row :gutter="10" class="mb8"><el-col :span="1.5"><el-button v-hasPermi="['property:building:add']" type="primary" plain icon="Plus" @click="handleAdd">新增</el-button></el-col></el-row>
    <el-table v-loading="loading" :data="list">
      <el-table-column label="编号" prop="buildingId" width="80"/>
      <el-table-column label="楼栋名称" prop="buildingName"/>
      <el-table-column label="地址" prop="address"/>
      <el-table-column label="楼层" prop="floors" width="80"/>
      <el-table-column label="状态" prop="status" width="90"><template #default="s"><el-tag :type="s.row.status === '0' ? 'success' : 'info'">{{ s.row.status === '0' ? '正常' : '停用' }}</el-tag></template></el-table-column>
      <el-table-column label="操作" width="160"><template #default="s"><el-button v-hasPermi="['property:building:edit']" link type="primary" @click="handleUpdate(s.row)">修改</el-button><el-button v-hasPermi="['property:building:remove']" link type="primary" @click="handleDelete(s.row)">删除</el-button></template></el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList"/>
    <el-dialog :title="title" v-model="open" width="520px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="楼栋名称" prop="buildingName"><el-input v-model="form.buildingName"/></el-form-item>
        <el-form-item label="地址"><el-input v-model="form.address"/></el-form-item>
        <el-form-item label="楼层" prop="floors"><el-input-number v-model="form.floors" :min="1"/></el-form-item>
        <el-form-item label="状态"><el-radio-group v-model="form.status"><el-radio value="0">正常</el-radio><el-radio value="1">停用</el-radio></el-radio-group></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea"/></el-form-item>
      </el-form>
      <template #footer><el-button type="primary" @click="submitForm">确定</el-button><el-button @click="open=false">取消</el-button></template>
    </el-dialog>
  </div>
</template>
<script>
import { listBuilding, getBuilding, addBuilding, updateBuilding, delBuilding } from '@/api/property/building'
// 新增和修改共用弹窗，通过 buildingId 判断调用哪一个保存接口。
export default { name: 'PropertyBuilding', data() { return { loading: true, list: [], total: 0, open: false, title: '', queryParams: { pageNum: 1, pageSize: 10 }, form: {}, rules: { buildingName: [{ required: true, message: '请填写楼栋名称', trigger: 'blur' }, { max: 80, message: '楼栋名称长度不能超过80个字符', trigger: 'blur' }], floors: [{ required: true, message: '请填写楼层数', trigger: 'change' }] } } }, created() { this.getList() }, methods: { getList() { this.loading = true; listBuilding(this.queryParams).then(r => { this.list = r.rows; this.total = r.total; this.loading = false }) }, reset() { this.form = { status: '0', floors: 1 } }, handleQuery() { this.queryParams.pageNum = 1; this.getList() }, resetQuery() { this.resetForm('queryForm'); this.handleQuery() }, handleAdd() { this.reset(); this.title = '新增楼栋'; this.open = true }, handleUpdate(row) { getBuilding(row.buildingId).then(r => { this.form = r.data; this.title = '修改楼栋'; this.open = true }) }, submitForm() { this.$refs.form.validate(valid => { if (!valid) return; const api = this.form.buildingId ? updateBuilding : addBuilding; api(this.form).then(() => { this.$modal.msgSuccess('保存成功'); this.open = false; this.getList() }) }) }, handleDelete(row) { this.$modal.confirm('确认删除该楼栋吗？').then(() => delBuilding(row.buildingId)).then(() => { this.$modal.msgSuccess('删除成功'); this.getList() }) } } }
</script>
