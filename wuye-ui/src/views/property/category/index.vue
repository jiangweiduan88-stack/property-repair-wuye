<template>
  <div class="app-container">
    <!-- 报修分类页面统一维护分类名称、排序和启停状态。 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="68px">
      <el-form-item label="分类名称"><el-input v-model="queryParams.categoryName" clearable @keyup.enter="handleQuery"/></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button><el-button v-hasPermi="['property:category:add']" type="primary" icon="Plus" @click="handleAdd">新增</el-button></el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="list">
      <el-table-column label="编号" prop="categoryId" width="80"/>
      <el-table-column label="分类名称" prop="categoryName"/>
      <el-table-column label="排序" prop="orderNum" width="80"/>
      <el-table-column label="状态" prop="status" width="90">
        <template #default="scope">
          <el-tag :type="String(scope.row.status) === '0' ? 'success' : 'info'">
            {{ String(scope.row.status) === '0' ? '正常' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160"><template #default="s"><el-button v-hasPermi="['property:category:edit']" link type="primary" @click="handleUpdate(s.row)">修改</el-button><el-button v-hasPermi="['property:category:remove']" link type="primary" @click="handleDelete(s.row)">删除</el-button></template></el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList"/>
    <el-dialog :title="title" v-model="open" width="480px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px"><el-form-item label="分类名称" prop="categoryName"><el-input v-model="form.categoryName"/></el-form-item><el-form-item label="排序"><el-input-number v-model="form.orderNum" :min="0"/></el-form-item><el-form-item label="状态"><el-radio-group v-model="form.status"><el-radio value="0">正常</el-radio><el-radio value="1">停用</el-radio></el-radio-group></el-form-item></el-form>
      <template #footer><el-button type="primary" @click="submitForm">确定</el-button><el-button @click="open=false">取消</el-button></template>
    </el-dialog>
  </div>
</template>
<script>
import { listCategory, getCategory, addCategory, updateCategory, delCategory } from '@/api/property/category'
// 分类状态在页面转换为中文标签，新增和修改共用保存流程。
export default { name: 'PropertyCategory', data() { return { loading: true, list: [], total: 0, open: false, title: '', queryParams: { pageNum: 1, pageSize: 10 }, form: {}, rules: { categoryName: [{ required: true, message: '请填写分类名称', trigger: 'blur' }, { max: 80, message: '分类名称长度不能超过80个字符', trigger: 'blur' }] } } }, created() { this.getList() }, methods: { getList() { this.loading = true; listCategory(this.queryParams).then(r => { this.list = r.rows; this.total = r.total; this.loading = false }) }, reset() { this.form = { status: '0', orderNum: 0 } }, handleQuery() { this.queryParams.pageNum = 1; this.getList() }, resetQuery() { this.resetForm('queryForm'); this.handleQuery() }, handleAdd() { this.reset(); this.title = '新增分类'; this.open = true }, handleUpdate(row) { getCategory(row.categoryId).then(r => { this.form = r.data; this.title = '修改分类'; this.open = true }) }, submitForm() { this.$refs.form.validate(valid => { if (!valid) return; const api = this.form.categoryId ? updateCategory : addCategory; api(this.form).then(() => { this.$modal.msgSuccess('保存成功'); this.open = false; this.getList() }) }) }, handleDelete(row) { this.$modal.confirm('确认删除该分类吗？').then(() => delCategory(row.categoryId)).then(() => { this.$modal.msgSuccess('删除成功'); this.getList() }) } } }
</script>
