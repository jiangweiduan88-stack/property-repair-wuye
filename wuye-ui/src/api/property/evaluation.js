import request from '@/utils/request'

// 评价请求统一经过 request 拦截器处理令牌、错误提示和响应格式。
export function listEvaluation(query) { return request({ url: '/property/evaluation/list', method: 'get', params: query }) }
export function getEvaluation(id) { return request({ url: '/property/evaluation/' + id, method: 'get' }) }
export function addEvaluation(data) { return request({ url: '/property/evaluation', method: 'post', data }) }
export function updateEvaluation(data) { return request({ url: '/property/evaluation', method: 'put', data }) }
export function delEvaluation(id) { return request({ url: '/property/evaluation/' + id, method: 'delete' }) }
