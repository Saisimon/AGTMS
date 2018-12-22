import { signIn, register, signOut } from '@/api/user'

const state = {
    progress: 100,
    intervalId: -1,
    token: getCookie('token', ''),
    language: getCookie('language', 'zh_CN')
};

const mutations = {
    setToken(state, token) {
        if (token && token !== '') {
            state.token = token;
        } else {
            state.token = '';
        }
        var c = 'token=' + escape(token);
        var exdate = new Date();
        exdate.setMilliseconds(exdate.getMilliseconds() + 30 * 60 * 1000);
        c += ';expires=' + exdate.toGMTString();
        c += ';path=/';
        document.cookie = c;
    },
    refreshToken() {
        var c = 'token=' + escape(state.token);
        var exdate = new Date();
        exdate.setMilliseconds(exdate.getMilliseconds() + 30 * 60 * 1000);
        c += ';expires=' + exdate.toGMTString();
        c += ';path=/';
        document.cookie = c;
    },
    setLanguage(state, language) {
        if (language) {
            state.language = language;
        } else {
            state.language = 'zh_CN';
        }
    },
    setProgress(state, progress) {
        state.progress = progress;
    },
    setIntervalId(state, intervalId) {
        state.intervalId = intervalId;
    },
    clearProgress(state) {
        state.progress = 100
        if (state.intervalId && state.intervalId !== -1) {
            clearInterval(state.intervalId);
        }
    }
};

const actions = {
    login(context, payload) {
        return new Promise((resolve, reject) => {
            if (context.state.token === "") {
                signIn(payload.username, payload.password).then(resp => {
                    resolve(resp);
                }, error => {
                    reject(error);
                });
            }
        });
    },
    reg(context, payload) {
        return new Promise((resolve, reject) => {
            if (context.state.token === "") {
                register(payload.username, payload.email, payload.password).then(resp => {
                    resolve(resp);
                }, error => {
                    reject(error);
                });
            }
        });
    },
    logout(context) {
        return new Promise((resolve, reject) => {
            if (context.state.token !== "") {
                signOut(context.state.token).then(resp => {
                    resolve(resp);
                }, error => {
                    reject(error);
                });
            }
        });
    }
};

function getCookie(name, defaultValue) {
    var reg = new RegExp("(^| )"+name+"=([^;]*)(;|$)");
    var arr = document.cookie.match(reg);
    if (arr) {
        return unescape(arr[2]); 
    } else {
        return defaultValue; 
    }
} 

export default {
    state,
    mutations,
    actions
}