import axios from 'axios'
import * as url from './url'
import router from '@/router'
import store from '@/store/store'

export default function request(token, reqUrl, payload) {
    var req = {
        method: 'post',
        url: url.URL_PREFIX + reqUrl
    };
    if (token != undefined && token != null) {
        req.headers = {
            'X-TOKEN': token
        };
    }
    if (payload != undefined && payload != null) {
        req.data = payload;
    }
    return new Promise((resolve, reject) => {
        axios(req).then(response => {
            resolve(response);
        }).catch(error => {
            if (error.response && error.response.status === 401) {
                store.commit('setToken', '');
                store.commit('setTrees', []);
                router.push({
                    path: '/signin?reply=' + encodeURIComponent(window.location.pathname)
                });
            }
            reject(error);
        });
    });
}