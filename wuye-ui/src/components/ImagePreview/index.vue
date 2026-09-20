<!-- 提供业务图片缩略图与放大预览能力，并处理多图地址和加载失败场景。 -->
<template>
  <el-image
    :src="`${realSrc}`"
    fit="cover"
    :style="`width:${realWidth};height:${realHeight};`"
    :preview-src-list="realSrcList"
    preview-teleported
  >
    <template #error>
      <div class="image-slot">
        <el-icon><picture-filled /></el-icon>
      </div>
    </template>
  </el-image>
</template>

<script setup>
// 统一图片缩略图、加载失败和大图预览行为，使业务页面只需传入图片地址即可安全展示。
import { isExternal } from "@/utils/validate"

const props = defineProps({
  src: {
    type: String,
    default: ""
  },
  width: {
    type: [Number, String],
    default: ""
  },
  height: {
    type: [Number, String],
    default: ""
  }
})

const realSrc = computed(() => {
  if (!props.src) {
    return
  }
  let real_src = props.src.split(",")[0]
  if (isExternal(real_src)) {
    return real_src
  }
  return import.meta.env.VITE_APP_BASE_API + real_src
})

const realSrcList = computed(() => {
  if (!props.src) {
    return
  }
  let real_src_list = props.src.split(",")
  let srcList = []
  real_src_list.forEach(item => {
    if (isExternal(item)) {
      return srcList.push(item)
    }
    return srcList.push(import.meta.env.VITE_APP_BASE_API + item)
  })
  return srcList
})

const realWidth = computed(() =>
  typeof props.width == "string" ? props.width : `${props.width}px`
)

const realHeight = computed(() =>
  typeof props.height == "string" ? props.height : `${props.height}px`
)
</script>

<style lang="scss" scoped>
.el-image {
  border-radius: 5px;
  background-color: #ebeef5;
  box-shadow: 0 0 5px 1px #ccc;
  :deep(.el-image__inner) {
    transition: all 0.3s;
    cursor: pointer;
    &:hover {
      transform: scale(1.2);
    }
  }
  :deep(.image-slot) {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    color: #909399;
    font-size: 30px;
  }
}
</style>
