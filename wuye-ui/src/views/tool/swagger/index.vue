<!-- 在系统内嵌接口文档页面，便于开发和联调人员统一查阅后端接口。 -->
<template>
   <i-frame v-model:src="url"></i-frame>
</template>

<script setup>
// 将后端接口文档嵌入管理系统，方便开发和联调人员在统一入口查询接口定义。
import iFrame from '@/components/iFrame'

const url = ref(import.meta.env.VITE_APP_BASE_API + "/swagger-ui/index.html")
</script>
