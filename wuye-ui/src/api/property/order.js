import request from '@/utils/request'

// 工单 API 按业务动作拆分，便于页面调用与后端状态流转接口一一对应。
export function listOrder(query) { return request({ url: '/property/order/list', method: 'get', params: query }) }
export function getOrder(id) { return request({ url: '/property/order/' + id, method: 'get' }) }
export function addOrder(data) { return request({ url: '/property/order', method: 'post', data }) }
export function updateOrder(data) { return request({ url: '/property/order', method: 'put', data }) }
export function delOrder(id) { return request({ url: '/property/order/' + id, method: 'delete' }) }
// 下列动作分别驱动受理、驳回、分配、维修、确认、取消和返工状态变化。
export function acceptOrder(id) { return request({ url: `/property/order/${id}/accept`, method: 'put' }) }
export function rejectOrder(id, reason) { return request({ url: `/property/order/${id}/reject`, method: 'put', data: { reason } }) }
export function assignOrder(id, data) { return request({ url: `/property/order/${id}/assign`, method: 'put', data }) }
export function repairUsers() { return request({ url: '/property/order/repair-users', method: 'get' }) }
export function startOrder(id) { return request({ url: `/property/order/${id}/start`, method: 'put' }) }
export function finishOrder(id, data) { return request({ url: `/property/order/${id}/finish`, method: 'put', data }) }
export function confirmOrder(id, data) { return request({ url: `/property/order/${id}/confirm`, method: 'put', data }) }
export function cancelOrder(id) { return request({ url: `/property/order/${id}/cancel`, method: 'put' }) }
export function reworkOrder(id, reason) { return request({ url: `/property/order/${id}/rework`, method: 'put', data: { reason } }) }
export function orderLogs(id) { return request({ url: `/property/order/${id}/logs`, method: 'get' }) }
// 看板接口通过 period 参数统一切换当月和滚动天数统计口径。
export function dashboard(period = 'month') { return request({ url: '/property/order/dashboard', method: 'get', params: { period } }) }
