import request from './request'

export function side(user) {
    return request(user, "/navigation/main/side");
}