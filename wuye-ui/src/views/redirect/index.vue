<!-- 承接路由刷新跳转，通过替换当前路由恢复目标页面及其查询参数。 -->
<template>
  <div></div>
</template>

<script setup>
// 通过中转路由重新挂载当前页面，用于标签页刷新时清除旧组件实例而不丢失查询参数。
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()
const { params, query } = route
const { path } = params

router.replace({ path: '/' + path, query })
</script>
