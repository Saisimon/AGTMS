import { signIn, signOut, passwordChange, profileSave, profileInfo, nav, notification, notifications, notificationRead } from '@/api/user'
import { statistics } from '@/api/index'
import { uploadImage } from '@/api/upload'
import router from '@/router'

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
    showChangePasswordModal: false,
    openTree: false,
    tree: {},
    notificationLoopId: null,
    notification: 0,
    notifications: [],
    statistics: {}
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
                state.alert.text = router.app.$t('server_error');
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
    },
    changeOpenTree(state, status) {
        if (status) {
            state.openTree = true;
        } else {
            state.openTree = false;
        }
    },
    setTree(state, tree) {
        if (tree) {
            state.tree = tree;
        }
    },
    setNotification(state, notification) {
        if (notification != null) {
            state.notification = notification;
        }
    },
    setNotifications(state, notifications) {
        if (notifications) {
            state.notifications = notifications;
        }
    },
    setNotificationLoopId(state, notificationLoopId) {
        state.notificationLoopId = notificationLoopId;
    },
    setStatistics(state, statistics) {
        if (statistics) {
            state.statistics = statistics;
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
    },
    changeOpenTree(context, status) {
        context.commit('changeOpenTree', status);
    },
    getTree(context) {
        nav(context.state.user).then(resp => {
            return context.commit('setTree', resp.data.data);
        });
    },
    getNotifications(context) {
        notifications(context.state.user).then(resp => {
            return context.commit('setNotifications', resp.data.data);
        });
    },
    readNotification(context, payload) {
        notificationRead(context.state.user, payload.id);
    },
    loopGetNotification(context) {
        notification(context.state.user).then(resp => {
            return context.commit('setNotification', resp.data.data);
        });
        (function loop() {
            var notificationLoopId = setTimeout(function() {
                if (context.state.user) {
                    notification(context.state.user).then(resp => {
                        context.commit('setNotification', resp.data.data);
                        loop();
                    });
                }
            }, 10000);
            context.commit('setNotificationLoopId', notificationLoopId);
        })();
    },
    getStatistics(context) {
        statistics(context.state.user).then(resp => {
            return context.commit('setStatistics', resp.data.data);
        });
    }
};

export default {
    state,
    mutations,
    actions
}