import { signIn, signOut, passwordChange, profileSave, profileInfo } from '@/api/user'
import { uploadImage } from '@/api/upload'

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

const state = {
    urlPrefix: '/agtms',
    progress: 100,
    intervalId: -1,
    user: JSON.parse(getCookie('user', null)),
    language: getCookie('language', 'zh_CN'),
    breadcrumbs:[],
    alert: {
        dismissSecs: 3,
        dismissCountDown: 0,
        variant: 'success',
        text: ''
    },
    showChangePasswordModal: false
};

const mutations = {
    setAvatar(state, avatar) {
        if (avatar) {
            state.user.avatar = avatar;
        }
    },
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
    setBreadcrumbs(state, breadcrumbs) {
        if (breadcrumbs) {
            state.breadcrumbs = breadcrumbs;
        }
    },
    clearProgress(state) {
        state.progress = 100
        if (state.intervalId && state.intervalId !== -1) {
            clearInterval(state.intervalId);
        }
    },
    showAlert(state, alert) {
        if (alert) {
            if (alert.message != null) {
                state.alert.text = alert.message;
            } else {
                state.alert.text = 'Error';
            }
            if (alert.variant) {
                state.alert.variant = alert.variant;
            } else {
                state.alert.variant = 'danger';
            }
            if (alert.dismissSecs) {
                state.alert.dismissSecs = alert.dismissSecs
            } else {
                state.alert.dismissSecs = 3
            }
            state.alert.dismissCountDown = state.alert.dismissSecs;
        }
    },
    changePasswordModal(state, show) {
        state.showChangePasswordModal = show;
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
    },
    changePwd(context, payload) {
        return new Promise((resolve, reject) => {
            if (context.state.user !== null) {
                passwordChange(context.state.user, payload.oldPassword, payload.newPassword).then(resp => {
                    resolve(resp);
                }, error => {
                    reject(error);
                });
            }
        });
    },
    uploadImg(context, payload) {
        return new Promise((resolve, reject) => {
            if (context.state.user !== null) {
                uploadImage(context.state.user, payload).then(resp => {
                    resolve(resp);
                }, error => {
                    reject(error);
                });
            }
        });
    },
    profile(context) {
        return new Promise((resolve, reject) => {
            if (context.state.user !== null) {
                profileInfo(context.state.user).then(resp => {
                    resolve(resp);
                }, error => {
                    reject(error);
                });
            }
        });
    },
    saveProfile(context, payload) {
        return new Promise((resolve, reject) => {
            if (context.state.user !== null) {
                profileSave(context.state.user, payload).then(resp => {
                    resolve(resp);
                }, error => {
                    reject(error);
                });
            }
        });
    }
};

export default {
    state,
    mutations,
    actions
}