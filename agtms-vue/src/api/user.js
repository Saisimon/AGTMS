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

export function passwordChange(user, oldPassword, newPassword) {
    return request(user, "/user/password/change", {
        oldPassword: oldPassword,
        newPassword: newPassword
    });
}

export function profileInfo(user) {
    return request(user, "/user/profile/info");
}

export function profileSave(user, data) {
    return request(user, "/user/profile/save", data);
}