<template>
    <!-- 导航栏 -->
    <b-navbar class="main-navbar" toggleable="sm">
        <b-navbar-toggle target="nav-bar"></b-navbar-toggle>
        <!-- 导航栏标题 -->
        <b-navbar-brand class="nav-bar-title">
            <b class="text-light">AGTMS</b>
        </b-navbar-brand>
        <b-collapse is-nav id="nav-bar">
            <!-- 顶部导航栏左侧 -->
            <b-navbar-nav>
                <b-nav-item href="javascript:void(0);" class="nav-bar-item nav-bar-bars" @click="toggleBars">
                    <i class="fa fa-fw fa-bars text-light"></i>
                </b-nav-item>
            </b-navbar-nav>
            <!-- 顶部导航栏右侧 -->
            <b-navbar-nav class="ml-auto">
                <b-nav-text class="p-0">
                    <!-- 左侧边栏 -->
                    <side-bar />
                </b-nav-text>
                <!-- 设置 -->
                <b-nav-item-dropdown 
                    variant="link" 
                    href="javascript:void(0);" 
                    right no-caret
                    class="nav-bar-item setting-nav-bar" 
                    :title="$t('setting')">
                    <template slot="button-content">
                        <i class="fa fa-fw fa-cog pt-2 text-light"></i>
                        <span class="nav-bar-content text-light ml-1">{{ $t("setting") }}</span>
                    </template>
                    <b-dropdown-item href="javascript:void(0);" @click.stop="toggleFullscreen">
                        <i class="fa fa-fw fa-arrows-alt"></i>
                        <span class="ml-1">{{ $t("fullscreen") }}</span>
                    </b-dropdown-item>
                </b-nav-item-dropdown>
                <!-- 语言 -->
                <b-nav-item-dropdown 
                    variant="link" 
                    href="javascript:void(0);" 
                    right no-caret
                    class="nav-bar-item language-nav-bar" 
                    :title="$t('language')">
                    <template slot="button-content">
                        <i class="fa fa-fw fa-language pt-2 text-light"></i>
                        <span class="nav-bar-content text-light ml-1">{{ $t('language') }}</span>
                    </template>
                    <b-dropdown-item v-for="(text, code, index) in languages" :key="index" href="javascript:void(0);" @click.stop="changeLanguage(code)">
                        <span>{{ text }}</span>
                    </b-dropdown-item>
                </b-nav-item-dropdown>
                <!-- 通知 -->
                <b-nav-item-dropdown v-if="$store.state.base.user" 
                    variant="link" 
                    href="javascript:void(0);" 
                    right no-caret
                    class="nav-bar-item notification-nav-bar"
                    @show="getNotifications()"
                    :title="$t('notification')">
                    <template slot="button-content">
                        <span :class="{'red-point': notification > 0}" >
                            <i class="fa fa-fw fa-bell-o pt-2 text-light"></i>
                        </span>
                        <span class="nav-bar-content text-light ml-1">{{ $t('notification') }}</span>
                    </template>
                    <b-dropdown-header class="font-weight-bold">
                        {{ $t('notification') }}
                    </b-dropdown-header>
                    <b-dropdown-divider></b-dropdown-divider>
                    <template v-if="notifications && notifications.length > 0">
                        <b-dropdown-item v-for="(data, index) in notifications" :key="index">
                            <div v-b-modal.modal-scrollable="'modal-notification-' + index" >
                                <div class="ellipsis-text" v-html="'「' + data.type + '」' + data.title"></div>
                                <div class="text-secondary" style="font-size:0.75rem">{{ new Date(data.createTime).toLocaleString() }}</div>
                            </div>
                            <b-modal
                                :id="'modal-notification-' + index" 
                                centered 
                                @shown="readNotification(data.id)"
                                hide-header
                                hide-footer>
                                <div v-html="data.title" class="mt-2 mb-1 font-weight-bold text-center" style="font-size:1rem"></div>
                                <div class="text-secondary text-center" style="font-size:0.75rem">{{ new Date(data.createTime).toLocaleString() }}</div>
                                <hr />
                                <div v-html="data.content" class="m-4" style="font-size:0.875rem"></div>
                            </b-modal>
                        </b-dropdown-item>
                    </template>
                    <template v-else>
                        <b-dropdown-item href="javascript:void(0);">
                            <div class="mt-2 mb-2">{{ $t('no_notification') }}</div>
                        </b-dropdown-item>
                    </template>
                    <b-dropdown-divider></b-dropdown-divider>
                    <b-dropdown-item to="/notification/main">
                        <div>{{ $t('see_more') }}</div>
                    </b-dropdown-item>
                </b-nav-item-dropdown>
                <!-- 用户 -->
                <b-nav-item-dropdown v-if="$store.state.base.user" 
                    variant="link" 
                    href="javascript:void(0);" 
                    right no-caret
                    class="nav-bar-item user-nav-bar">
                    <template slot="button-content">
                        <div style="line-height: 30px;">
                            <img class="avatar-image" :src="$store.state.base.urlPrefix + $store.state.base.user.avatar" width="30" height="30" v-if="$store.state.base.user.avatar">
                            <avatar v-else :username="$store.state.base.user.loginName" :size="30" :rounded="false" class="float-left avatar-image"></avatar>
                            <span class="nav-bar-content text-light ml-2">{{ $store.state.base.user.loginName }}</span>
                        </div>
                    </template>
                    <!-- 修改密码 -->
                    <b-dropdown-item href="javascript:void(0);" @click.stop="$store.commit('changePasswordModal', true)">
                        <i class="fa fa-fw fa-key"></i>
                        <span class="ml-1">{{ $t('change_password') }}</span>
                    </b-dropdown-item>
                    <!-- 编辑个人资料 -->
                    <b-dropdown-item to="/profile">
                        <i class="fa fa-fw fa-edit"></i>
                        <span class="ml-1">{{ $t('edit_profile') }}</span>
                    </b-dropdown-item>
                    <!-- 登出 -->
                    <b-dropdown-item href="javascript:void(0);" @click.stop="signOut" class="signout-btn">
                        <i class="fa fa-fw fa-sign-out"></i>
                        <span class="ml-1">{{ $t('sign_out') }}</span>
                    </b-dropdown-item>
                </b-nav-item-dropdown>
                <!-- 登录 -->
                <b-nav-item v-else 
                    class="nav-bar-item" 
                    :title="$t('sign_in')">
                    <router-link to="/signin" class="signin-link">
                        <i class="fa fa-fw fa-sign-in pt-2 text-light"></i>
                        <span class="nav-bar-content text-light ml-1">{{ $t('sign_in') }}</span>
                    </router-link>
                </b-nav-item>
            </b-navbar-nav>
        </b-collapse>
        <b-modal :id="'modal-change-password'" 
            @hide="changePasswordValue"
            :title="$t('change_password')"
            v-model="$store.state.base.showChangePasswordModal"
            centered 
            hide-footer
            no-close-on-backdrop
            header-border-variant="light"
            footer-border-variant="light">
            <div>
                <b-row class="mb-3">
                    <b-col>
                        <label for="oldPassword-input" class="form-label font-weight-bold">
                            {{ $t('old_password') }}
                        </label>
                        <b-input-group>
                            <b-form-input class="border-top-0 border-right-0 border-left-0 rounded-0" 
                                v-if="oldPassword.show" 
                                id="oldPassword-input" 
                                name="oldPassword" 
                                v-model.trim="oldPassword.value" 
                                :state="oldPassword.state" 
                                type="text" />
                            <b-form-input class="border-top-0 border-right-0 border-left-0 rounded-0" 
                                v-else
                                id="oldPassword-input" 
                                name="oldPassword" 
                                v-model.trim="oldPassword.value" 
                                :state="oldPassword.state" 
                                type="password" />
                            <b-input-group-text class="border-top-0 border-right-0 border-left-0 rounded-0" slot="append" style="cursor: pointer;">
                                <i class="fa fa-eye-slash" @click="oldPassword.show = false" v-if="oldPassword.show"></i>
                                <i class="fa fa-eye" @click="oldPassword.show = true" v-else></i>
                            </b-input-group-text>
                        </b-input-group>
                        <b-form-invalid-feedback id="oldPassword-input-feedback" :class="{'d-block': oldPassword.state == false}">
                            {{ oldPassword.message }}
                        </b-form-invalid-feedback>
                    </b-col>
                </b-row>
                <b-row class="mb-3">
                    <b-col>
                        <label for="newPassword-input" class="form-label font-weight-bold">
                            {{ $t('new_password') }}
                        </label>
                        <b-input-group>
                            <b-form-input class="border-top-0 border-right-0 border-left-0 rounded-0" 
                                v-if="newPassword.show" 
                                id="newPassword-input" 
                                name="newPassword" 
                                v-model.trim="newPassword.value" 
                                :state="newPassword.state" 
                                type="text" />
                            <b-form-input class="border-top-0 border-right-0 border-left-0 rounded-0" 
                                v-else
                                id="newPassword-input" 
                                name="newPassword" 
                                v-model.trim="newPassword.value" 
                                :state="newPassword.state" 
                                type="password" />
                            <b-input-group-text class="border-top-0 border-right-0 border-left-0 rounded-0" slot="append" style="cursor: pointer;">
                                <i class="fa fa-eye-slash" @click="newPassword.show = false" v-if="newPassword.show"></i>
                                <i class="fa fa-eye" @click="newPassword.show = true" v-else></i>
                            </b-input-group-text>
                        </b-input-group>
                        <b-form-invalid-feedback id="newPassword-input-feedback" :class="{'d-block': newPassword.state == false}">
                            {{ newPassword.message }}
                        </b-form-invalid-feedback>
                    </b-col>
                </b-row>
                <b-row class="mb-3">
                    <b-col>
                        <label for="confirmPassword-input" class="form-label font-weight-bold">
                            {{ $t('confirm_password') }}
                        </label>
                        <b-input-group>
                            <b-form-input class="border-top-0 border-right-0 border-left-0 rounded-0" 
                                v-if="confirmPassword.show" 
                                id="confirmPassword-input" 
                                name="confirmPassword" 
                                v-model.trim="confirmPassword.value" 
                                :state="confirmPassword.state" 
                                type="text" />
                            <b-form-input class="border-top-0 border-right-0 border-left-0 rounded-0" 
                                v-else
                                id="confirmPassword-input" 
                                name="confirmPassword" 
                                v-model.trim="confirmPassword.value" 
                                :state="confirmPassword.state" 
                                type="password" />
                            <b-input-group-text class="border-top-0 border-right-0 border-left-0 rounded-0" slot="append" style="cursor: pointer;">
                                <i class="fa fa-eye-slash" @click="confirmPassword.show = false" v-if="confirmPassword.show"></i>
                                <i class="fa fa-eye" @click="confirmPassword.show = true" v-else></i>
                            </b-input-group-text>
                        </b-input-group>
                        <b-form-invalid-feedback id="confirmPassword-input-feedback" :class="{'d-block': confirmPassword.state == false}">
                            {{ confirmPassword.message }}
                        </b-form-invalid-feedback>
                    </b-col>
                </b-row>
                <b-row class="mb-3">
                    <b-col class="text-right">
                        <b-button variant="outline-primary" 
                            size="sm"
                            @click="changePassword">
                            <i class="fa fa-fw fa-save"></i>
                            {{ $t("confirm_save") }}
                        </b-button>
                    </b-col>
                </b-row>
            </div>
        </b-modal>
    </b-navbar>
</template>

<script>
import SideBar from '@/components/SideBar.vue'
import Avatar from 'vue-avatar'

export default {
    name: 'nav-bar',
    data: function() {
        return {
            oldPassword: {
                value: '',
                state: null,
                message: '',
                show: false
            },
            newPassword: {
                value: '',
                state: null,
                message: '',
                show: false
            },
            confirmPassword: {
                value: '',
                state: null,
                message: '',
                show: false
            }
        }
    },
    computed: {
        languages: function() {
            return {
                'zh_CN': '简体中文', 
                'en': 'English'
            };
        },
        notification: function() {
            var notification = this.$store.state.base.notification;
            if (!notification) {
                return 0;
            } else {
                return notification;
            }
        },
        notifications: function() {
            return this.$store.state.base.notifications;
        }
    },
    created: function() {
        if (this.$store.state.base.user != null && this.$store.state.base.notificationLoopId == null) {
            this.$store.dispatch('loopGetNotification');
        }
    },
    methods: {
        signOut: function() {
            this.$store.dispatch('logout');
            this.$store.commit('setUser', null);
            this.$store.commit('setTree', {});
            this.$store.commit('setBreadcrumbs', []);
            if (this.$store.state.base.notificationLoopId != null) {
                clearTimeout(this.$store.state.base.notificationLoopId);
                this.$store.commit('setNotificationLoopId', null);
            }
            this.$router.push({
                path: '/signin'
            });
        },
        toggleFullscreen: function() {
            var fullscreen = document.getElementById('app').getAttribute('fullscreen');
            if (fullscreen && fullscreen == '1') {
                var element = document.documentElement;
                if (element.requestFullscreen) {
                    element.requestFullscreen();
                } else if (element.mozRequestFullScreen) {
                    element.mozRequestFullScreen();
                } else if (element.webkitRequestFullscreen) {
                    element.webkitRequestFullscreen();
                } else if (element.msRequestFullscreen) {
                    element.msRequestFullscreen();
                }
            } else {
                if (document.exitFullscreen) {
                    document.exitFullscreen();
                } else if (document.mozCancelFullScreen) {
                    document.mozCancelFullScreen();
                } else if (document.webkitExitFullscreen) {
                    document.webkitExitFullscreen();
                } else if (document.msExitFullscreen) {
                    document.msExitFullscreen();
                }
            }
        },
        changeLanguage: function(code) {
            var c = 'language=' + escape(code);
            var exdate = new Date();
            exdate.setDate(exdate.getDate() + 30);
            c += ';expires=' + exdate.toGMTString();
            c += ';path=/';
            document.cookie = c;
            window.location.reload();
        },
        getNotifications: function() {
            this.$store.dispatch('getNotifications');
        },
        readNotification: function(id) {
            this.$store.dispatch('readNotification', {
                id: id
            });
        },
        toggleBars: function() {
            var openTree = this.$store.state.base.openTree;
            this.$store.commit('changeOpenTree', !openTree);
        },
        changePasswordValue: function() {
            this.oldPassword.value='';
            this.oldPassword.state=null;
            this.oldPassword.message='';
            this.oldPassword.show=false;
            this.newPassword.value='';
            this.newPassword.state=null;
            this.newPassword.message='';
            this.newPassword.show=false;
            this.confirmPassword.value='';
            this.confirmPassword.state=null;
            this.confirmPassword.message='';
            this.confirmPassword.show=false;
        },
        changePassword: function() {
            var checked = true;
            var oldPasswordValue = this.oldPassword.value;
            if (oldPasswordValue == null || oldPasswordValue == '') {
                checked = false;
                this.oldPassword.message = this.$t('old_password') + this.$t('not_blank');
                this.oldPassword.state = false;
            } else {
                this.oldPassword.message = '';
                this.oldPassword.state = null;
            }
            var newPasswordValue = this.newPassword.value;
            if (newPasswordValue == null || newPasswordValue == '') {
                checked = false;
                this.newPassword.message = this.$t('new_password') + this.$t('not_blank');
                this.newPassword.state = false;
            } else {
                this.newPassword.message = '';
                this.newPassword.state = null;
            }
            var confirmPasswordValue = this.confirmPassword.value;
            if (confirmPasswordValue == null || confirmPasswordValue == '') {
                checked = false;
                this.confirmPassword.message = this.$t('confirm_password') + this.$t('not_blank');
                this.confirmPassword.state = false;
            } else if (confirmPasswordValue != newPasswordValue) {
                checked = false;
                this.confirmPassword.message = this.$t('confirm_password_diff');
                this.confirmPassword.state = false;
            } else {
                this.confirmPassword.message = '';
                this.confirmPassword.state = null;
            }
            if (checked) {
                var vm = this;
                this.$store.dispatch('changePwd', {
                    oldPassword: oldPasswordValue,
                    newPassword: newPasswordValue
                }).then(resp => {
                    if (resp.data.code === 0) {
                        vm.$store.commit('changePasswordModal', false);
                        vm.$store.commit('showAlert', {
                            variant: 'success',
                            message: vm.$t('change_password_success')
                        });
                        vm.$store.commit('setUser', null);
                        vm.$store.commit('setTree', {});
                        vm.$store.commit('setBreadcrumbs', []);
                        vm.$router.push({
                            path: '/signin'
                        });
                    }
                });
            }
        }
    },
    components: {
        'side-bar': SideBar,
        'avatar': Avatar
    }
}
</script>

<style scoped>
@media (min-width: 576px) {
    .nav-bar-title {
        width: 250px;
        height: 50px;
        line-height: 50px;
        text-align: center;
        background-color: #367fa9;
        padding: 0px;
        margin: 0px;
        -webkit-transition: -webkit-transform .3s ease-in-out,width .3s ease-in-out;
        -moz-transition: -moz-transform .3s ease-in-out,width .3s ease-in-out;
        -o-transition: -o-transform .3s ease-in-out,width .3s ease-in-out;
        transition: transform .3s ease-in-out,width .3s ease-in-out;
    }
    .nav-bar-content {
        display: none;
    }
    .nav-bar-item {
        padding: 2px;
    }
    .main-navbar-collapse .nav-bar-title {
        width: 50px;
    }
    .main-navbar-collapse .nav-bar-title b {
        display: none;
    }
}
@media (max-width: 575px) {
    .navbar-collapse::-webkit-scrollbar-thumb {
        background-color: #848f94;
    }
    .navbar-collapse {
        overflow: auto;
        max-height: 75vh;
    }
    .nav-bar-bars {
        display: none;
    }
    .nav-bar-item {
        padding: 2px 5px 2px 15px;
    }
    .nav-bar-item:hover {
        border-left: 4px solid #fff;
    }
}
.main-navbar {
    z-index: 1000;
    background-color: #3c8dbc;
    padding: 0;
    position: fixed;
    top: 0;
    width: 100%;
}
.nav-bar-item:hover {
    background: rgba(0,0,0,0.1);
}
.avatar-image {
    border-radius: 50%!important;
}
</style>
