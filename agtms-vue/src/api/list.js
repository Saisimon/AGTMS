import request from './request'

export function list(user, payload) {
    return request(user, payload.url + '/list', {
        "filter": payload.filters,
        "pageable": payload.pageable
    });
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

export function batchImport(user, url, data) {
    return request(user, url + '/batch/import', data);
}

export function download(user, url, id) {
    return request(user, url + '/download?id=' + id);
}