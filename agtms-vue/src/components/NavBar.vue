<template>
    <!-- 导航栏 -->
    <b-navbar class="main-navbar" toggleable>
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
                <b-nav-item-dropdown variant="link" href="javascript:void(0);" 
                    right no-caret 
                    class="nav-bar-item" 
                    :title="$t('setting')">
                    <template slot="button-content">
                        <i class="fa fa-fw fa-cog text-light"></i>
                        <span class="nav-bar-content text-light ml-1">{{ $t("setting") }}</span>
                    </template>
                    <b-dropdown-item href="javascript:void(0);" @click.stop="toggleFullscreen">
                        <i class="fa fa-fw fa-arrows-alt"></i>
                        <span class="ml-1">{{ $t("fullscreen") }}</span>
                    </b-dropdown-item>
                </b-nav-item-dropdown>
                <!-- 语言 -->
                <b-nav-item-dropdown variant="link" href="javascript:void(0);" 
                    right no-caret 
                    class="nav-bar-item" 
                    :title="$t('language')">
                    <template slot="button-content">
                        <i class="fa fa-fw fa-language text-light"></i>
                        <span class="nav-bar-content text-light ml-1">{{ $t('language') }}</span>
                    </template>
                    <div v-for="(text, code, index) in languages" :key="index">
                        <b-dropdown-item href="javascript:void(0);" @click.stop="changeLanguage(code)">
                            <span>{{ text }}</span>
                        </b-dropdown-item>
                        <b-dropdown-divider v-if="index != Object.keys(languages).length - 1"></b-dropdown-divider>
                    </div>
                </b-nav-item-dropdown>
                <!-- 登录/退出 -->
                <b-nav-item v-if="signIn" 
                    @click.stop="signOut" 
                    class="nav-bar-item" 
                    :title="$t('sign_out')">
                    <i class="fa fa-fw fa-sign-out text-light"></i>
                    <span class="nav-bar-content text-light ml-1">{{ $t('sign_out') }}</span>
                </b-nav-item>
                <b-nav-item v-else 
                    class="nav-bar-item" 
                    :title="$t('sign_in')">
                    <router-link to="/signin">
                        <i class="fa fa-fw fa-sign-in text-light"></i>
                        <span class="nav-bar-content text-light ml-1">{{ $t('sign_in') }}</span>
                    </router-link>
                </b-nav-item>
            </b-navbar-nav>
        </b-collapse>
    </b-navbar>
</template>

<script>
import SideBar from '@/components/SideBar.vue'

export default {
    name: 'nav-bar',
    computed: {
        languages: function() {
            return {
                'zh_CN': '简体中文', 
                'zh_TW': '繁體中文',
                'en': 'English'
            };
        },
        signIn: function() {
            return this.$store.state.base.user != null;
        }
    },
    methods: {
        signOut: function() {
            this.$store.dispatch('logout');
            this.$store.commit('setUser', null);
            this.$store.commit('setTree', {});
            this.$store.commit('setBreadcrumbs', []);
            this.$router.push({
                path: '/signin?reply=' + encodeURIComponent(this.$route.path)
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
        toggleBars: function() {
            var openTree = this.$store.state.navigation.openTree;
            this.$store.commit('changeOpenTree', !openTree);
        }
    },
    components: {
        'side-bar': SideBar
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
</style>
