import request from './request'

export function statistics(user) {
    return request(user, "/index/statistics");
}