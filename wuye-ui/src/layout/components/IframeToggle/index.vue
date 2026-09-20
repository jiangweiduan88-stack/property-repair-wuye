<!-- 缓存并切换已打开的 iframe 标签页，避免标签切换时重复加载外部页面。 -->
<template>
  <inner-link
    v-for="(item, index) in tagsViewStore.iframeViews"
    :key="item.path"
    :iframeId="'iframe' + index"
    v-show="route.path === item.path"
    :src="iframeUrl(item.meta.link, item.query)"
  ></inner-link>
</template>

<script setup>
// 根据标签页缓存切换多个 iframe 的显示状态，保留外部页面上下文并避免重复加载。
import InnerLink from "../InnerLink/index"
import useTagsViewStore from "@/store/modules/tagsView"

const route = useRoute()
const tagsViewStore = useTagsViewStore()

function iframeUrl(url, query) {
  if (Object.keys(query).length > 0) {
    let params = Object.keys(query).map((key) => key + "=" + query[key]).join("&")
    return url + "?" + params
  }
  return url
}
</script>
