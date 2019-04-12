import request from './request'

export function signIn(username, password) {
    return request(null, "/user/auth", {
        name: username,
        password: password
    });
}

export function signOut(user) {
    return request(user, "/user/logout");
}

export function changePassword(user, oldPassword, newPassword) {
    return request(user, "/user/change/password", {
        oldPassword: oldPassword,
        newPassword: newPassword
    });
}