# 社区物业报修系统

社区物业报修系统用于连接业主、物业管理人员和维修人员，覆盖房屋维护、报修受理、维修分配、维修处理、业主确认、返工及服务评价等业务流程。

## 技术架构

- 后端：Spring Boot、Spring Security、MyBatis、MySQL、Redis
- 前端：Vue 3、Vite、Element Plus、ECharts
- 构建：Maven、npm

## 项目模块

- `wuye-admin`：后端启动模块和接口控制器
- `wuye-common`：公共模型、注解及工具类
- `wuye-framework`：安全认证、配置和基础框架
- `wuye-system`：系统管理与物业报修业务实现
- `wuye-quartz`：定时任务模块
- `wuye-generator`：代码生成模块
- `wuye-ui`：前端管理页面

## 本地运行

可执行 `bin/start-all.cmd` 一键启动本地依赖、后端和前端，也可以分别启动：

```text
bin/start-backend.cmd
bin/start-frontend.cmd
```

后端默认端口为 `8080`，前端默认端口为 `80`。

## 构建命令

```text
mvn -pl wuye-admin -am -DskipTests package
cd wuye-ui
npm run build:prod
```

项目继续遵循根目录 `LICENSE` 中保留的开源许可和原作者版权声明。
