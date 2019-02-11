import axios from 'axios'
import * as url from './url'
import router from '@/router'
import store from '@/store/store'

export default function request(user, reqUrl, payload) {
    var req = {
        method: 'post',
        url: url.URL_PREFIX + "/agtms" + reqUrl
    };
    if (user != undefined && user != null && user.token && user.userId) {
        req.headers = {
            'X-TOKEN': user.token,
            'X-UID': user.userId
        };
    }
    if (payload != undefined && payload != null) {
        req.data = payload;
    }
    return new Promise((resolve, reject) => {
        axios(req).then(response => {
            resolve(response);
        }).catch(error => {
            if (error.response) {
                if (error.response.status === 401) {
                    store.commit('setUser', null);
                    store.commit('setTrees', []);
                    router.push({
                        path: '/signin?reply=' + encodeURIComponent(window.location.pathname + window.location.search)
                    });
                } else if (error.response.status === 404) {
                    router.push({
                        path: '/notfound'
                    });
                }
            }
            reject(error);
        });
    });
}