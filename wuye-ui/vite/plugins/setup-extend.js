/** 扩展 Vue setup 语法，允许页面直接声明稳定的组件名称。 */
import setupExtend from 'unplugin-vue-setup-extend-plus/vite'

export default function createSetupExtend() {
  return setupExtend({})
}
