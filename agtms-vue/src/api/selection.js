import * as url from './url'
import request from './request'

export function select(key, token) {
    return request(token, url.SELECTION + '/' + key);
}