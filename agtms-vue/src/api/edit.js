import request from './request'

export function info(token, url, id) {
    return request(token, url + '/info?id=' + id);
}

export function save(token, url, navigate) {
    return request(token, url + '/save', navigate);
}

export function editGrid(token, url, id) {
    var reqUrl = url + '/grid';
    if (id != null) {
        reqUrl += '?id=' + id;
    }
    return request(token, reqUrl);
}