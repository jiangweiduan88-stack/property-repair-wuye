<template>
  <div class="app-container">
    <!-- 服务评价页面只展示后端按当前账号过滤后的评价数据。 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="68px">
      <el-form-item label="工单号"><el-input v-model="queryParams.orderNo" clearable @keyup.enter="handleQuery"/></el-form-item>
      <el-form-item><el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button><el-button icon="Refresh" @click="resetQuery">重置</el-button><el-button v-hasPermi="['property:evaluation:add']" type="primary" icon="Plus" @click="handleAdd">新增</el-button></el-form-item>
    </el-form>
    <el-table v-loading="loading" :data="list">
      <el-table-column label="编号" prop="evaluationId" width="80"/><el-table-column label="工单号" prop="orderNo"/><el-table-column label="评分" prop="score" width="180"><template #default="s"><el-rate v-model="s.row.score" disabled/></template></el-table-column><el-table-column label="评价内容" prop="content"/>
      <el-table-column label="操作" width="160"><template #default="s"><el-button v-hasPermi="['property:evaluation:edit']" link type="primary" @click="handleUpdate(s.row)">修改</el-button><el-button v-hasPermi="['property:evaluation:remove']" link type="primary" @click="handleDelete(s.row)">删除</el-button></template></el-table-column>
    </el-table>
    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList"/>
    <el-dialog :title="title" v-model="open" width="520px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px"><el-form-item label="工单ID" prop="orderId"><el-input-number v-model="form.orderId" :min="1" :disabled="Boolean(form.evaluationId)"/></el-form-item><el-form-item label="评分" prop="score"><el-rate v-model="form.score"/></el-form-item><el-form-item label="评价内容" prop="content"><el-input v-model="form.content" type="textarea" maxlength="500" show-word-limit/></el-form-item></el-form>
      <template #footer><el-button type="primary" @click="submitForm">确定</el-button><el-button @click="open=false">取消</el-button></template>
    </el-dialog>
  </div>
</template>
<script>
import { listEvaluation, getEvaluation, addEvaluation, updateEvaluation, delEvaluation } from '@/api/property/evaluation'
// 前端负责输入体验，评价归属、工单状态和重复评价由后端再次校验。
export default { name: 'PropertyEvaluation', data() { return { loading: true, list: [], total: 0, open: false, title: '', queryParams: { pageNum: 1, pageSize: 10 }, form: {}, rules: { orderId: [{ required: true, message: '请选择需要评价的工单', trigger: 'change' }], score: [{ required: true, message: '请选择服务评分', trigger: 'change' }], content: [{ max: 500, message: '评价内容长度不能超过500个字符', trigger: 'blur' }] } } }, created() { this.getList() }, methods: { getList() { this.loading = true; listEvaluation(this.queryParams).then(r => { this.list = r.rows; this.total = r.total; this.loading = false }) }, reset() { this.form = { score: 5 } }, handleQuery() { this.queryParams.pageNum = 1; this.getList() }, resetQuery() { this.resetForm('queryForm'); this.handleQuery() }, handleAdd() { this.reset(); this.title = '新增评价'; this.open = true }, handleUpdate(row) { getEvaluation(row.evaluationId).then(r => { this.form = r.data; this.title = '修改评价'; this.open = true }) }, submitForm() { this.$refs.form.validate(valid => { if (!valid) return; const api = this.form.evaluationId ? updateEvaluation : addEvaluation; api(this.form).then(() => { this.$modal.msgSuccess('保存成功'); this.open = false; this.getList() }) }) }, handleDelete(row) { this.$modal.confirm('确认删除该评价吗？').then(() => delEvaluation(row.evaluationId)).then(() => { this.$modal.msgSuccess('删除成功'); this.getList() }) } } }
</script>
