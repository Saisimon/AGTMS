import * as url from './url'
import request from './request'

export function side(user) {
    return request(user, url.NAVIGATION_SIDE);
}