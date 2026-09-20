# 项目整体目录结构

## 1. 项目概览

本项目是一个前后端分离的社区物业报修系统：

- 后端：Java 17 + Spring Boot 4 + MyBatis-Plus + Druid，使用 Maven 多模块工程管理。
- 前端：Vue 3 + Vite + Element Plus，位于 `wuye-ui`。
- 数据库：MySQL，当前数据库名为 `wy-vue`，默认端口为 `3333`。
- 缓存：Redis，默认端口为 `6379`。
- 运行方式：支持开发模式、构建后的 JAR 启动，以及 Windows 本地一键启动。

## 2. 顶层目录

```text
property-repair-wuye/
├── pom.xml                         # Maven 根工程和模块依赖管理
├── README.md                       # 项目简介、架构和运行说明
├── LICENSE                         # 开源许可
├── my.ini                          # MySQL 基础配置
├── start.bat                       # Windows 后端启动/停止/重启/状态菜单
├── start.sh                        # Linux/macOS 后端启动脚本
│
├── bin/                            # 本地运行、构建和服务管理脚本
├── wuye-admin/                     # 后端启动模块
├── wuye-common/                    # 公共基础代码
├── wuye-framework/                 # 框架、安全和基础设施
├── wuye-system/                    # 系统管理和物业业务模块
├── wuye-quartz/                    # 定时任务模块
├── wuye-generator/                 # 代码生成模块
├── wuye-ui/                        # Vue 3 前端
│
├── sql/                            # 初始化 SQL 和数据库迁移脚本
├── backup/                         # 本地数据库备份（已加入 Git 忽略）
├── doc/                            # 项目文档和使用手册
├── runtime/                        # 当前运行 JAR
├── logs/                           # 后端、前端和依赖服务日志
├── data/                           # 文件上传等运行时数据
├── .mysql/                         # 项目本地 MySQL 数据目录
├── .tools/                         # 项目附带的 Maven 等工具
├── scripts/                        # 辅助脚本
└── tools/                          # 其他开发辅助工具
```

`.git`、`target`、`node_modules`、`dist` 等目录属于版本控制或构建产生的目录，日常开发一般不需要直接修改。

## 3. 后端 Maven 模块

根目录 `pom.xml` 是聚合工程，当前包含六个业务/基础模块和一个应用启动模块。

| 模块 | 主要职责 |
| --- | --- |
| `wuye-admin` | Spring Boot 应用入口、Web 控制器、全局配置、资源文件和最终可运行 JAR。 |
| `wuye-common` | 注解、常量、通用领域对象、异常、过滤器、工具类、分页和文件处理。 |
| `wuye-framework` | 数据源、权限认证、拦截器、AOP、缓存、任务管理和框架级配置。 |
| `wuye-system` | 用户、角色、菜单、部门、字典、通知、物业楼栋/房间/报修等核心业务。 |
| `wuye-quartz` | Quartz 定时任务、任务调度和任务日志。 |
| `wuye-generator` | 根据数据库表生成后端、Mapper、Service 和前端代码。 |

### 3.1 后端典型结构

各模块通常遵循以下结构：

```text
<module>/
├── pom.xml
└── src/
    ├── main/java/com/wuye/
    │   └── <package>/
    │       ├── controller/       # HTTP 接口
    │       ├── service/          # 业务逻辑
    │       ├── mapper/           # 数据访问接口
    │       ├── domain/           # 实体和请求/响应对象
    │       ├── config/           # 模块配置
    │       ├── task/             # 定时任务（适用时）
    │       └── util/             # 模块工具类
    └── main/resources/
        ├── mapper/               # MyBatis XML 映射文件
        ├── application*.yml      # Spring 配置
        ├── mybatis/               # MyBatis 全局配置
        └── logback.xml            # 日志配置
```

### 3.2 启动模块 `wuye-admin`

```text
wuye-admin/
├── src/main/java/com/wuye/
│   ├── WuYeApplication.java             # Spring Boot 主入口
│   ├── WuYeServletInitializer.java      # WAR/Servlet 容器初始化
│   └── web/                             # Web 控制器和 Web 配置
└── src/main/resources/
    ├── application.yml                  # 通用应用配置
    ├── application-druid.yml            # MySQL/Druid 数据源配置
    ├── mybatis/                         # MyBatis 配置
    ├── i18n/                            # 国际化资源
    └── logback.xml                      # 日志输出配置
```

当前数据库连接配置位于 `wuye-admin/src/main/resources/application-druid.yml`，JDBC 地址为 `localhost:3333/wy-vue`。

## 4. 前端 `wuye-ui`

```text
wuye-ui/
├── package.json                  # 前端依赖和 npm scripts
├── vite.config.js                # Vite 构建配置
├── index.html                    # 前端入口页面
├── .env.development              # 开发环境变量
├── .env.staging                  # 测试/预发布环境变量
├── .env.production               # 生产环境变量
├── public/                       # 不经打包处理的静态资源
├── vite/                         # Vite 插件和构建扩展
├── bin/                          # 前端辅助脚本
└── src/
    ├── api/                      # Axios 接口封装
    ├── assets/                   # 图片、图标和样式资源
    ├── components/               # 可复用 Vue 组件
    ├── directive/                # 自定义指令和权限指令
    ├── layout/                   # 主布局、导航、侧边栏、标签页
    ├── plugins/                  # 全局插件、缓存、权限和弹窗封装
    ├── router/                   # Vue Router 路由
    ├── store/                    # Pinia 状态管理
    ├── utils/                    # 请求、权限、字典、校验等工具
    └── views/                    # 页面级组件
        ├── property/             # 物业首页、楼栋、房间、报修分类、工单
        ├── system/               # 用户、角色、菜单、部门、字典、通知
        ├── monitor/              # 在线用户、登录日志、操作日志
        ├── tool/                 # 代码生成、构建器、Swagger
        └── error/                # 401、404 等错误页面
```

常用前端命令：

```bash
npm run dev       # 开发模式
npm run build:prod
npm run preview
```

## 5. 物业业务模块详解

物业业务的后端主要位于 `wuye-admin` 和 `wuye-system`，前端位于 `wuye-ui/src/api/property` 与 `wuye-ui/src/views/property`。模块覆盖基础资料、房屋业主绑定、报修分类、报修工单、维修流转、服务评价和数据看板。

### 5.1 后端文件分层

```text
wuye-admin/src/main/java/com/wuye/web/controller/property/
├── PropBuildingController.java             # 楼栋接口
├── PropRoomController.java                 # 房屋和业主绑定接口
├── PropRepairCategoryController.java       # 报修分类接口
├── PropRepairOrderController.java          # 工单查询和状态流转接口
└── PropRepairEvaluationController.java     # 服务评价接口

wuye-system/src/main/java/com/wuye/system/
├── domain/
│   ├── PropBuilding.java                    # 楼栋实体
│   ├── PropRoom.java                        # 房屋实体
│   ├── PropRepairCategory.java              # 报修分类实体
│   ├── PropRepairOrder.java                 # 报修工单实体
│   ├── PropRepairOrderLog.java              # 工单操作记录实体
│   ├── PropRepairEvaluation.java            # 服务评价实体
│   └── PropRepairStats.java                 # 看板统计结果对象（非独立表）
├── mapper/                                  # Mapper 接口
├── service/                                 # Service 接口
└── service/impl/                            # 业务实现和状态校验

wuye-system/src/main/resources/mapper/property/
├── PropBuildingMapper.xml
├── PropRoomMapper.xml
├── PropRepairCategoryMapper.xml
├── PropRepairOrderMapper.xml
├── PropRepairOrderLogMapper.xml
└── PropRepairEvaluationMapper.xml
```

### 5.2 领域对象与数据库表

| Java 对象 | 数据表 | 主要字段/用途 |
| --- | --- | --- |
| `PropBuilding` | `prop_building` | 楼栋名称、地址、楼层数、启用状态。 |
| `PropRoom` | `prop_room` | 楼栋、单元、房号、业主账号、业主姓名和联系电话。 |
| `PropRepairCategory` | `prop_repair_category` | 报修分类名称、排序和启停状态。 |
| `PropRepairOrder` | `prop_repair_order` | 工单编号、业主、房屋、分类、问题描述、现场图片、维修人员和时间节点。 |
| `PropRepairOrderLog` | `prop_repair_order_log` | 受理、驳回、分配、开始维修、完成、确认、取消和返工等操作记录。 |
| `PropRepairEvaluation` | `prop_repair_evaluation` | 工单评价、评分、评价内容、业主和维修人员。 |
| `PropRepairStats` | — | 汇总工单总数、待处理数、完成数和平均评分，供看板使用。 |

### 5.3 后端接口页面对应关系

| 业务页面 | Controller | 基础路径 | 主要接口 |
| --- | --- | --- | --- |
| 楼栋管理 | `PropBuildingController` | `/property/building` | `GET /list`、`GET /{id}`、`POST /`、`PUT /`、`DELETE /{ids}` |
| 房屋管理 | `PropRoomController` | `/property/room` | `GET /list`、`GET /options`、`GET /{id}`、`POST /`、`PUT /`、`DELETE /{ids}` |
| 报修分类 | `PropRepairCategoryController` | `/property/category` | `GET /list`、`GET /options`、`GET /{id}`、`POST /`、`PUT /`、`DELETE /{ids}` |
| 报修工单 | `PropRepairOrderController` | `/property/order` | 查询、新增、编辑、删除、受理、驳回、分配、维修、确认、取消、返工、记录和看板统计 |
| 服务评价 | `PropRepairEvaluationController` | `/property/evaluation` | `GET /list`、`GET /{id}`、`POST /`、`PUT /`、`DELETE /{ids}` |

工单 Controller 的动作接口如下：

```text
GET  /property/order/list                 工单分页查询
GET  /property/order/{orderId}            工单详情
POST /property/order                      新增报修
PUT  /property/order                      编辑或保存工单
PUT  /property/order/{id}/accept          受理
PUT  /property/order/{id}/reject          驳回并填写原因
PUT  /property/order/{id}/assign          分配维修人员
GET  /property/order/repair-users         获取可分配的维修人员
PUT  /property/order/{id}/start           开始维修
PUT  /property/order/{id}/finish          提交维修结果和完工图片
PUT  /property/order/{id}/confirm         业主确认并提交评价
PUT  /property/order/{id}/cancel          取消报修
PUT  /property/order/{id}/rework          发起返工
GET  /property/order/{id}/logs            查看工单操作记录
GET  /property/order/dashboard            获取看板统计数据
```

### 5.4 前端页面与 API 文件

| 页面文件 | API 文件 | 页面职责 |
| --- | --- | --- |
| `views/property/dashboard/index.vue` | `api/property/order.js` | 展示工单总量、待处理量、完成量、平均评分、状态分布、分类分布和维修人员排行，使用 ECharts 绘图。 |
| `views/property/building/index.vue` | `api/property/building.js` | 楼栋分页查询、新增、编辑、删除和启停状态维护。 |
| `views/property/room/index.vue` | `api/property/room.js`、`api/property/building.js` | 房屋信息维护、选择所属楼栋、绑定业主资料，并提供报修表单使用的房屋选项。 |
| `views/property/category/index.vue` | `api/property/category.js` | 报修分类维护、排序和启停状态管理。 |
| `views/property/order/index.vue` | `api/property/order.js`、`api/property/category.js`、`api/property/room.js` | 新增报修、图片上传、工单列表、详情、状态动作、维修人员分配、操作记录和评价确认。 |
| `views/property/evaluation/index.vue` | `api/property/evaluation.js` | 评价列表、评分、评价内容维护。 |

前端 API 文件只负责封装请求地址和 HTTP 方法，统一通过 `@/utils/request` 处理令牌、基础地址、错误提示和响应格式。`order.js` 将工单动作拆分成 `acceptOrder`、`assignOrder`、`startOrder`、`finishOrder`、`confirmOrder`、`cancelOrder` 和 `reworkOrder`，与后端接口一一对应。

### 5.5 工单状态流转

`prop_repair_status` 字典和前端状态处理共同定义了以下流程：

```text
0 待受理 → 1 已受理 → 2 已分配 → 3 处理中 → 4 待确认 → 5 已完成
                  ├→ 6 已驳回 → 重新编辑后再次提交
                  ├→ 7 已取消
                  └→ 8 返工中 → 重新分配/维修
```

关键角色和权限边界：

- `property_owner`：提交本人房屋报修、查看本人工单、确认维修结果、评价或发起返工。
- `repair_worker`：查看分配给自己的工单、开始维修、提交维修结果和完工图片。
- `property_manager`：查看全量工单、受理、驳回、分配维修人员、处理异常状态。
- `system_admin` / `admin`：拥有系统级管理权限。

后端通过 `@PreAuthorize` 和服务层的当前用户校验双重控制访问范围；前端通过 `v-hasPermi`、角色判断和状态判断控制按钮显示，但前端判断不能替代后端权限校验。

### 5.6 工单页面的前后端协作

```text
报修页面 order/index.vue
  ├── categoryOptions() ──> /property/category/options
  ├── roomOptions() ──────> /property/room/options
  ├── addOrder() ─────────> PropRepairOrderController.add()
  └── 状态按钮 ───────────> order/{id}/{action}

PropRepairOrderController
  ├── 按角色收窄工单查询范围
  ├── 调用 IPropRepairOrderService 执行状态流转
  ├── 调用 IPropRepairOrderLogService 写入操作记录
  └── 通过 PropRepairOrderMapper 查询工单、统计和关联信息
```

### 5.7 菜单和权限来源

物业菜单及权限初始化数据位于 `sql/123.sql`，主要菜单路径为：

```text
property/building/index       property:building:list
property/room/index           property:room:list
property/category/index       property:category:list
property/order/index          property:order:list
property/evaluation/index     property:evaluation:list
```

新增、编辑、删除按钮以及工单状态动作均使用独立权限标识，例如 `property:order:assign`、`property:order:finish` 和 `property:order:confirm`。修改物业页面或接口时，需要同时检查页面路由、API 封装、Controller 权限、菜单数据和数据库 Mapper 是否保持一致。

## 6. 脚本和本地运行

`bin/` 目录集中管理 Windows 本地运行流程：

| 文件 | 作用 |
| --- | --- |
| `start-all.cmd` | 按 MySQL、Redis、后端、前端的顺序启动整套系统。 |
| `start-backend.cmd` | 启动后端 JAR，并写入 `logs/backend-service.log`。 |
| `start-frontend.cmd` | 启动 Vite 前端并监听 80 端口。 |
| `start-local-mysql.cmd` | 直接启动项目本地 MySQL（适合排查或未注册服务时使用）。 |
| `start-local-redis.cmd` | 启动本地 Redis。 |
| `install-property-repair-mysql-service.ps1` | 以管理员权限注册 `PropertyRepairMySQL` Windows 服务。 |
| `property-repair-mysql.ini` | Windows 服务使用的 MySQL 配置，指向 `.mysql/Data-runtime`。 |
| `package.bat` | 使用项目内 Maven 打包后端。 |
| `clean.bat` | 清理后端构建产物。 |

当前 `start-all.cmd` 使用 `PropertyRepairMySQL` Windows 服务，因此 MySQL 不依赖前台控制台窗口。

## 7. 数据库、文档和运行产物

```text
sql/
├── 123.sql                                      # 项目初始化 SQL
├── migration_*.sql                               # 数据库迁移脚本

backup/
├── wy-vue-full-*.sql                             # 当前 wy-vue 完整备份
└── ry-vue-before-rename-*.sql                    # 改名前的回滚备份

doc/
├── project-directory-structure.md                # 本文档
├── property-repair-module.md                     # 物业模块说明
├── technology-migration.md                       # 技术迁移说明
└── 物业报修系统环境使用手册.docx                # 环境使用手册
```

`runtime/wuye-admin-runtime.jar` 是当前后端运行包；`wuye-admin/target/` 和前端 `dist/` 是构建输出，不建议手工编辑。

## 8. 请求调用链

```text
浏览器
  ↓
wuye-ui（Vue Router / Pinia / Axios）
  ↓ HTTP API
wuye-admin（Spring Boot 启动层和 Web 控制器）
  ↓
wuye-framework（认证、权限、AOP、数据源）
  ↓
wuye-system / wuye-quartz / wuye-generator
  ↓
wuye-common（公共模型和工具）
  ↓
MySQL wy-vue + Redis
```
