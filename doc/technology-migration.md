# 物业报修系统技术栈迁移说明

## 1. 迁移目标

本次迁移在不改变系统业务目标、菜单范围、页面功能和工单流程的前提下，将技术实现统一到开题报告要求：

- 前端：Vue 3、Element Plus、Vite、Pinia。
- 后端：Spring Boot 4、MyBatis-Plus 3.5.17。
- 数据库与缓存：MySQL 8.4、Redis。
- 运行环境：JDK 17、Node.js。

物业报修、楼宇管理、房屋管理、报修分类、工单评价、数据看板以及角色权限等原有功能均予以保留。

## 2. 前端迁移内容

- 将 Vue 2 和 Element UI 页面迁移为 Vue 3 与 Element Plus 写法。
- 使用 Vite 替换 Vue CLI 构建方式，开发服务端口保持为 `80`。
- 使用 Pinia 管理登录用户、权限、字典和应用状态。
- 保留工单状态流转、角色操作入口、详情、操作记录和图片预览功能。
- 图片查看支持切换、计数、放大、缩小和旋转，列表与详情采用统一交互。
- 保留密码字符策略、公告已读状态和系统标题等项目定制内容。

## 3. 后端迁移内容

- 引入 MyBatis-Plus Spring Boot 4 Starter。
- 物业模块实体增加表名、主键和非数据库字段标识。
- Mapper 继承 `BaseMapper`，Service 继承 `IService`，实现类继承 `ServiceImpl`。
- 保留原有 XML 自定义 SQL，避免改变复杂查询、权限过滤和业务状态流转逻辑。
- 使用 MyBatis-Plus 的会话工厂加载现有 Mapper XML，并启用下划线转驼峰规则。
- 后端以可执行 JAR 方式运行，避免依赖旧的外部运行类库目录。

## 4. 业务流程保持不变

工单主流程仍为：业主提交、物业受理或驳回、物业分配、维修人员开始维修、维修人员完成、业主确认或发起返工。系统继续根据当前用户角色和工单状态展示可执行操作，并记录新增、修改、受理、驳回、分配、开始维修、完成、确认、返工和取消等操作节点。

## 5. 构建方式

后端在项目根目录执行：

```powershell
.\.tools\apache-maven-3.9.16\bin\mvn.cmd clean package -DskipTests
```

前端在 `wuye-ui` 目录执行：

```powershell
npm ci
npm run build:prod
```

## 6. 本地启动

双击项目最外层的 `一键启动.cmd`，可依次启动 MySQL、Redis、后端和前端。脚本会等待每个服务真正就绪，全部启动成功后自动打开系统页面。也可以直接运行内层的 `bin\start-all.cmd`。默认地址如下：

- 系统页面：`http://localhost/property/order`
- 前端服务：`http://localhost:80`
- 后端服务：`http://localhost:8080`
- MySQL：`localhost:3333`
- Redis：`localhost:6379`

也可以分别运行 `start-local-mysql.cmd`、`start-local-redis.cmd`、`start-backend.cmd` 和 `start-frontend.cmd`。

MySQL 启动脚本优先使用 `.mysql\Data-runtime`。该目录不存在时，会自动回退到 `.mysql\Data`。

## 7. 验证结果

- Maven 七个模块完整打包通过。
- 前端生产构建通过，共完成 2521 个模块转换。
- 登录、用户信息、动态菜单和验证码接口通过。
- 工单列表、详情、操作记录、维修人员、数据看板和维修状态字典接口通过。
- 楼宇、房屋、报修分类和评价列表接口通过。
- 前端通过 `/dev-api` 代理访问后端正常。

## 8. 回退与维护

迁移前的 Vue 2 前端已备份到：

`.migration\wuye-ui-vue2-before-migration.zip`

后续开发应继续使用 Vue 3 和 Element Plus 组件语法；物业模块新增通用单表操作时优先使用 MyBatis-Plus，涉及联合查询、角色范围或复杂统计时可继续使用 Mapper XML，以保持 SQL 清晰可控。
