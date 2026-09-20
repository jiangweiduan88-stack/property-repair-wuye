<!-- 提供源码仓库快捷入口，点击图标后在新窗口打开项目仓库。 -->
<template>
  <div>
    <svg-icon icon-class="github" @click="goto" />
  </div>
</template>

<script setup>
// 提供代码仓库入口组件，统一外部链接打开方式并降低页面对仓库地址的直接依赖。
const url = ref('/')

function goto() {
  window.open(url.value)
}
</script>
