import request from './request'

export function list(token, payload) {
    return request(token, payload.url + '/list?index=' + payload.pageable.index + '&size=' + payload.pageable.size + '&sort=' + payload.pageable.sort, payload.filters);
}

export function mainGrid(token, url) {
    return request(token, url + '/grid');
}

export function remove(token, url, id) {
    return request(token, url + '/remove?id=' + id);
}

export function batchRemove(token, url, ids) {
    return request(token, url + '/batch/remove', ids);
}

export function batchSave(token, url, data) {
    return request(token, url + '/batch/save', data);
}