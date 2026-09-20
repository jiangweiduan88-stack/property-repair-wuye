<!-- 提供项目文档快捷入口，帮助使用者快速查看系统说明。 -->
<template>
  <div>
    <svg-icon icon-class="question" @click="goto" />
  </div>
</template>

<script setup>
// 提供项目文档入口组件，将跳转行为集中封装以便导航栏和帮助页面复用。
const url = ref('/')

function goto() {
  window.open(url.value)
}
</script>
