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
            <transition name="switch-view" mode="out-in">
                <router-view/>
            </transition>
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
            return !this.$store.state.navigation.openTree;
        },
        progress: function() {
            return this.$store.state.base.progress;
        }
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
            this.$store.dispatch('getTrees');
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
}
body::-webkit-scrollbar, .main-sidebar::-webkit-scrollbar, .navbar-collapse::-webkit-scrollbar {
    width: 8px;
    height: 8px;
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
    height: 40px;
    line-height: 40px;
    font-size: 14px;
    font-weight: 400;
}
.marquee {
    white-space: nowrap;
    overflow-x: hidden;
    width: 100%;
}
.marquee span:hover {
    display: inline-block;
    animation: marquee 5s linear infinite;
}
</style>
