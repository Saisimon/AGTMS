import { signIn, register, signOut } from '@/api/user'

const state = {
    urlPrefix: '///' + window.location.hostname + ':8100/agtms',
    progress: 100,
    intervalId: -1,
    user: JSON.parse(getCookie('user', null)),
    language: getCookie('language', 'zh_CN')
};

const mutations = {
    setUser(state, user) {
        if (user) {
            state.user = user;
        } else {
            state.user = null;
        }
        setCookie('user', JSON.stringify(state.user), 1800);
    },
    refreshUser() {
        setCookie('user', JSON.stringify(state.user), 1800);
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
            if (context.state.user == null) {
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
            if (context.state.user == null) {
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
            if (context.state.user !== null) {
                signOut(context.state.user).then(resp => {
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

function setCookie(name, value, timeout) { 
    var exdate = new Date();
    exdate.setMilliseconds(exdate.getMilliseconds() + timeout * 1000);
    document.cookie = name + "="+ escape(value) + ";expires=" + exdate.toGMTString() + ';path=/'; 
} 

export default {
    state,
    mutations,
    actions
}