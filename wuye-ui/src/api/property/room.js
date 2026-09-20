import request from '@/utils/request'

// options 接口由后端按当前用户收窄房屋范围，可安全复用于报修表单。
export function listRoom(query) { return request({ url: '/property/room/list', method: 'get', params: query }) }
export function roomOptions() { return request({ url: '/property/room/options', method: 'get' }) }
export function getRoom(id) { return request({ url: '/property/room/' + id, method: 'get' }) }
export function addRoom(data) { return request({ url: '/property/room', method: 'post', data }) }
export function updateRoom(data) { return request({ url: '/property/room', method: 'put', data }) }
export function delRoom(id) { return request({ url: '/property/room/' + id, method: 'delete' }) }
