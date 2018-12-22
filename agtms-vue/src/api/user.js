import * as url from './url'
import request from './request'

export function signIn(username, password) {
    return request(null, url.SIGN_IN, {
        name: username,
        password: password
    });
}

export function register(username, email, password) {
    return request(null, url.REGISTER, {
        name: username,
        email: email,
        password: password
    });
}

export function signOut(token) {
    return request(token, url.SIGN_OUT);
}