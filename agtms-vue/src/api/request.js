import axios from 'axios'
import router from '@/router'
import store from '@/store/store'

export default function request(user, reqUrl, payload) {
    var req = {
        method: 'post',
        url: store.state.base.urlPrefix + reqUrl
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
                    store.commit('setTree', {});
                    store.commit('setBreadcrumbs', []);
                    const whiteList = ['/', '/signin', '/register'];
                    if (whiteList.indexOf(window.location.pathname) === -1) {
                        router.push({
                            path: '/signin?reply=' + encodeURIComponent(window.location.pathname + window.location.search)
                        });
                    }
                } else if (error.response.status === 500) {
                    store.commit('showAlert', {
                        message: error.response.statusText
                    });
                }
            }
            reject(error);
        });
    });
}