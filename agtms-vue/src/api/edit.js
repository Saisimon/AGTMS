import request from './request'

export function info(user, url, id) {
    return request(user, url + '/info?id=' + id);
}

export function save(user, url, data) {
    return request(user, url + '/save', data);
}

export function editGrid(user, url, id) {
    var reqUrl = url + '/grid';
    if (id != null) {
        reqUrl += '?id=' + id;
    }
    return request(user, reqUrl);
}