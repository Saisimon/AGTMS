import * as url from './url'
import request from './request'

export function side(token) {
    return request(token, url.NAVIGATE_SIDE);
}

export function navigateSelection(token, excludeId) {
    var reqUrl = url.NAVIGATE_SELECTION;
    if (excludeId) {
        reqUrl = reqUrl + '?id=' + excludeId;
    }
    return request(token, reqUrl);
}