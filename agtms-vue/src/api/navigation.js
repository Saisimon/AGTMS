import * as url from './url'
import request from './request'

export function side(user) {
    return request(user, url.NAVIGATION_SIDE);
}

export function navigationSelection(user, excludeId) {
    var reqUrl = url.NAVIGATION_SELECTION;
    if (excludeId) {
        reqUrl = reqUrl + '?id=' + excludeId;
    }
    return request(user, reqUrl);
}