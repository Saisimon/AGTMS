import * as url from './url'
import request from './request'

export function select(key, user) {
    return request(user, url.SELECTION + '/' + key);
}