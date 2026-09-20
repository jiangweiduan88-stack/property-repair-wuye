import request from '@/utils/request'

// 集中定义楼栋接口，页面无需重复处理基础地址、令牌和统一错误响应。
export function listBuilding(query) { return request({ url: '/property/building/list', method: 'get', params: query }) }
export function getBuilding(id) { return request({ url: '/property/building/' + id, method: 'get' }) }
export function addBuilding(data) { return request({ url: '/property/building', method: 'post', data }) }
export function updateBuilding(data) { return request({ url: '/property/building', method: 'put', data }) }
export function delBuilding(id) { return request({ url: '/property/building/' + id, method: 'delete' }) }
