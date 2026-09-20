/** 将 Element Plus 图标批量注册为全局组件，供菜单和业务页面直接使用。 */
import * as components from '@element-plus/icons-vue'

export default {
  install: (app) => {
    for (const key in components) {
      const componentConfig = components[key]
      app.component(componentConfig.name, componentConfig)
    }
  }
}
