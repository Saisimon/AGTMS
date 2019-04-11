import * as url from './url'
import request from './request'

export function signIn(username, password) {
    return request(null, url.SIGN_IN, {
        name: username,
        password: password
    });
}

export function signOut(user) {
    return request(user, url.SIGN_OUT);
}

export function resetPassword(user, oldPassword, newPassword) {
    return request(user, url.RESET_PASSWORD, {
        oldPassword: oldPassword,
        newPassword: newPassword
    });
}