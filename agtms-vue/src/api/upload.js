import request from './request'

export function uploadImage(user, data) {
    return request(user, "/image/upload", data);
}