# Community Property Repair Module

This project implements a community property repair workflow for the thesis topic "Design and Implementation of a Community Property Repair System Based on Spring Boot".

## Business Roles

- Owner/resident: submit repair orders, cancel pending orders, confirm completion, request rework, evaluate service.
- Property manager: accept or reject repair orders, assign repair workers, track orders, view dashboard statistics.
- Repair worker: receive assigned tasks, start processing, submit completion result and images.
- System administrator: maintain users, roles, menus, buildings, rooms, categories and system logs.

## Demo Accounts

All demo accounts use the initial password `admin123`.

| Username | Nickname | Role | Main Responsibility |
| --- | --- | --- | --- |
| `owner01` | 业主张三 | 业主/住户 | Submit repair, view progress, confirm completion, evaluate service |
| `property01` | 物业管理员李明 | 物业管理员 | Audit repair, assign repair workers, track work orders, view statistics |
| `repair01` | 维修人员王师傅 | 维修人员 | Receive tasks, process repair, submit repair result |
| `sysadmin01` | 系统管理员赵强 | 系统管理员 | Manage users, roles, permissions, community data and notices |

## Main Modules

- Dashboard: total repairs, pending repairs, completed repairs, average score, status/category/worker statistics.
- Building management: maintain community building data.
- Room management: bind room, owner and building information.
- Repair category: maintain repair types such as plumbing, doors/windows and public facilities.
- Repair order: submit, accept, reject, assign, start, finish, confirm, cancel and rework.
- Evaluation: score and comment on completed repair orders.

## Repair Status

| Code | Status | Typical Operator | Description |
| --- | --- | --- | --- |
| 0 | Pending acceptance | Owner | Created after owner submits repair order |
| 1 | Accepted | Property manager | Valid repair order accepted |
| 2 | Assigned | Property manager | Assigned to repair worker |
| 3 | Processing | Repair worker | Repair worker has started |
| 4 | Waiting confirmation | Repair worker | Repair finished and waiting owner confirmation |
| 5 | Completed | Owner | Owner confirms completion |
| 6 | Rejected | Property manager | Invalid or out-of-scope repair |
| 7 | Canceled | Owner | Owner cancels before acceptance |
| 8 | Rework | Owner / worker | Owner rejects result and sends back for repair |

## Import Order

1. Import `sql/ry_20260417.sql`.
2. Import `sql/quartz.sql`.
3. Import `sql/property_repair.sql`.

## Verification

- Backend build: `mvn -pl wuye-admin -am -DskipTests package`
- Frontend install: `npm.cmd install` in `wuye-ui`
- Frontend build: `npm.cmd run build:prod` in `wuye-ui`

Both backend and frontend builds were verified after adding this module.
