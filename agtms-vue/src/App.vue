<template>
    <div id="app" :class="{'main-navbar-collapse': hasMainNavbarCollapse}" fullscreen="1">
        <b-progress 
            class="router-progress"
            height="5px"
            :value="progress" 
            :max="100" 
            :variant="'primary'"
            animated 
            v-if="progress != 100" />
        <nav-bar />
        <div class="main-container">
            <!-- 面包屑导航 -->
            <b-breadcrumb :items="breadcrumbs" v-if="breadcrumbs && breadcrumbs.length > 0" />
            <transition name="switch-view" mode="out-in">
                <router-view/>
            </transition>
            <!-- 警告框 -->
            <div class="modal d-block" v-if="alert.dismissCountDown" style="z-index: 9999">
                <div class="modal-dialog modal-md">
                    <b-alert :variant="alert.variant"
                        dismissible
                        :show="alert.dismissCountDown"
                        style="pointer-events: auto;"
                        class="w-100"
                        @dismissed="alert.dismissCountDown=0">
                        {{ alert.text }}
                    </b-alert>
                </div>
            </div>
        </div>
        <copy-right/>
    </div>
</template>

<script>
import NavBar from '@/components/NavBar.vue'
import Copyright from '@/components/Copyright.vue'

export default {
    name: 'app',
    computed: {
        hasMainNavbarCollapse: function() {
            return !this.$store.state.base.openTree;
        },
        progress: function() {
            return this.$store.state.base.progress;
        },
        breadcrumbs: function() {
            return this.$store.state.base.breadcrumbs;
        },
        alert: function() {
            return this.$store.state.base.alert;
        },
    },
    created: function() {
        document.addEventListener('fullscreenchange', function() {
            toggleFullscreenStatus();
        });
        document.addEventListener('mozfullscreenchange', function() {
            toggleFullscreenStatus();
        });
        document.addEventListener('webkitfullscreenchange', function() {
            toggleFullscreenStatus();
        });
        document.addEventListener('msfullscreenchange', function() {
            toggleFullscreenStatus();
        });
        function toggleFullscreenStatus() {
            var element = document.getElementById("app");
            var fullscreen = element.getAttribute('fullscreen');
            if (fullscreen && fullscreen == "1") {
                element.setAttribute('fullscreen', '0');
            } else {
                element.setAttribute('fullscreen', '1');
            }
        }
        if (this.$store.state.base.user != null) {
            this.$store.dispatch('getTree');
        }
        document.getElementsByTagName("body")[0].setAttribute('style', 'width: ' + document.documentElement.clientWidth + 'px');
    },
    mounted: function() {
        window.onresize = function() {
            document.getElementsByTagName("body")[0].setAttribute('style', 'width: ' + document.documentElement.clientWidth + 'px');
        };
    },
    components: {
        'nav-bar': NavBar,
        'copy-right': Copyright
    }
}
</script>

<style>
body {
    overflow-x: hidden;
    font-family: "Helvetica Neue",Helvetica,Arial,"Microsoft Yahei","Hiragino Sans GB","Heiti SC","WenQuanYi Micro Hei",sans-serif!important;
    font-size: 0.875rem!important;
}
body::-webkit-scrollbar, .main-sidebar::-webkit-scrollbar, .navbar-collapse::-webkit-scrollbar {
    width: 5px;
    height: 5px;
    background-color: #fff;
}
body::-webkit-scrollbar-thumb{
    background-color: #848f94;
}
a:hover, a:active, a:focus {
    outline: none!important;
    text-decoration: none!important;
}
select:active, select:focus {
    box-shadow: none!important;
    outline: none!important;
}
input:active, input:focus {
    box-shadow: none!important;
    outline: none!important;
}
.base-btn {
    min-width: 80px;
}
.btn-primary {
    background-color: #3c8dbc!important;
    border-color: #3c8dbc!important;
}
.btn-primary:hover {
    background-color: #2d688a!important;
    border-color: #2d688a!important;
}
.btn-outline-primary {
    color: #3c8dbc!important;
    border-color: #3c8dbc!important;
}
.btn-outline-primary:hover {
    color: #fff!important;
    background-color: #3c8dbc!important;
    border-color: #3c8dbc!important;
}
.main-container {
    z-index: 800;
    min-height: 100%;
    margin-top : 50px;
    padding: 10px;
}
.router-progress {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 9999;
    border-radius: 0!important;
}
@media (min-width: 576px) {
    .main-container {
        -webkit-transition: -webkit-transform .3s ease-in-out,margin .3s ease-in-out;
        -moz-transition: -moz-transform .3s ease-in-out,margin .3s ease-in-out;
        -o-transition: -o-transform .3s ease-in-out,margin .3s ease-in-out;
        transition: transform .3s ease-in-out,margin .3s ease-in-out;
        margin-left: 250px;
    }
    .main-navbar-collapse .main-container {
        margin-left: 50px;
    }
}
.switch-view-enter-active, .switch-view-leave-active {
    transition: all .5s;
}
.switch-view-enter, .switch-view-leave-to{
    opacity: 0;
}
.form-label {
    height: 38px;
    line-height: 38px;
    font-size: 14px;
    font-weight: 400;
}
.ellipsis-text {
    text-overflow: ellipsis;
    white-space: nowrap;
    overflow: hidden;
    max-width: 300px;
    cursor: pointer;
}
.close {
    font-size: 1.25rem!important;
}
.modal-header, .modal-body {
    padding: 0.5rem 1rem!important;
}
.form-label {
    height: inherit!important;
    line-height: inherit!important;
}
.breadcrumb {
    padding: 0.75rem 1.25rem!important;
}
.card-header-title-container {
    font-size: 17px;
    font-weight: bold;
    color: #555;
}
.filter-container {
    margin-bottom: 10px;
}
.filter-switch-btn {
    width: 100%;
    color: #000!important;
}
.filter-toggle {
    padding: 10px;
    border: 1px solid #e8e8e8;
    border-radius: 0.25rem;
}
.filter-select-container {
    border: 0!important;
    max-width: 150px!important;
}
.input-group-text {
    height: 36px;
    line-height: 36px;
}
.form-control {
    border: 1px solid #e8e8e8!important;
    height: 36px!important;
}
.rows-div {
    font-size: 14px;
    line-height: 35px;
    height: 35px;
}
.page-link {
    font-size: 14px;
}
.vgt-selection-info-row {
    display: none!important;
}
.vgt-wrap__actions-footer {
    border: 0px!important;
    position: relative;
}
.vgt-wrap__footer {
    border-top: 0px!important;
    padding: .75em!important;
}
.vgt-wrap__footer .footer__row-count__select {
    width: 50px!important;
    height: 30px!important;
    line-height: 30px!important;
}
.vgt-wrap__footer .footer__navigation__page-info__current-entry {
    width: 50px!important;
    margin: 0px!important;
    height: 30px!important;
    line-height: 30px!important;
}
table.vgt-table {
    font-size: 14px!important;
}
table.vgt-table .vgt-left-align {
    vertical-align: inherit!important;
}
table.vgt-table .sorting {
    padding-right: 1.5em!important;
}
table.vgt-table .vgt-right-align {
    vertical-align: inherit!important;
}
table.vgt-table th {
    height: 40px;
}
.input-group-text {
    font-size: 0.875rem!important;
}
label {
    margin-bottom: 0.25rem!important;
}
.form-group-label {
    font-size: 1rem;
    margin-bottom: 1rem!important;
    border-bottom: 2px solid #aaa;
}
</style>
