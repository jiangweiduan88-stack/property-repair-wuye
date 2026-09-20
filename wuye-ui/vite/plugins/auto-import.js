/** 自动导入 Vue 与路由常用 API，减少页面中的重复 import 声明。 */
import autoImport from 'unplugin-auto-import/vite'

export default function createAutoImport() {
  return autoImport({
    imports: [
      'vue',
      'vue-router',
      'pinia'
    ],
    dts: false
  })
}
