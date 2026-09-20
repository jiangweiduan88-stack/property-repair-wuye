<!-- 切换 Element Plus 组件尺寸并刷新页面，使界面密度偏好即时生效。 -->
<template>
  <div>
    <el-dropdown trigger="click" @command="handleSetSize">
      <div class="size-icon--style">
        <svg-icon class-name="size-icon" icon-class="size" />
      </div>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item v-for="item of sizeOptions" :key="item.value" :disabled="size === item.value" :command="item.value">
            {{ item.label }}
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup>
// 切换 Element Plus 全局组件尺寸并持久化用户偏好，保证不同页面的控件密度一致。
import useAppStore from "@/store/modules/app"

const appStore = useAppStore()
const size = computed(() => appStore.size)
const { proxy } = getCurrentInstance()
const sizeOptions = ref([
  { label: "较大", value: "large" },
  { label: "默认", value: "default" },
  { label: "稍小", value: "small" },
])

function handleSetSize(size) {
  proxy.$modal.loading("正在设置布局大小，请稍候...")
  appStore.setSize(size)
  setTimeout("window.location.reload()", 1000)
}
</script>

<style lang='scss' scoped>
.size-icon--style {
  font-size: 18px;
  line-height: 50px;
  padding-right: 7px;
}
</style>
