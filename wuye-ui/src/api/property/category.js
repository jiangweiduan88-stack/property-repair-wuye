import request from '@/utils/request'

// 分类接口同时服务于后台维护和新建工单的分类选项加载。
export function listCategory(query) { return request({ url: '/property/category/list', method: 'get', params: query }) }
export function categoryOptions() { return request({ url: '/property/category/options', method: 'get' }) }
export function getCategory(id) { return request({ url: '/property/category/' + id, method: 'get' }) }
export function addCategory(data) { return request({ url: '/property/category', method: 'post', data }) }
export function updateCategory(data) { return request({ url: '/property/category', method: 'put', data }) }
export function delCategory(id) { return request({ url: '/property/category/' + id, method: 'delete' }) }
