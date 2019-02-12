import request from './request'

export function list(user, payload) {
    return request(user, payload.url + '/list?index=' + payload.pageable.index + '&size=' + payload.pageable.size + '&sort=' + payload.pageable.sort, payload.filters);
}

export function mainGrid(user, url) {
    return request(user, url + '/grid');
}

export function batchGrid(user, url) {
    return request(user, url + '/batch/grid');
}

export function batchRemove(user, url, ids) {
    return request(user, url + '/batch/remove', ids);
}

export function batchSave(user, url, data) {
    return request(user, url + '/batch/save', data);
}

export function batchExport(user, url, data) {
    return request(user, url + '/batch/export', data);
}

export function download(user, url, id) {
    return request(user, url + '/download?id=' + id);
}