import Vue from 'vue'
import Router from 'vue-router'
import Home from './views/Home.vue'
import Signin from './views/Signin.vue'
import Profile from './views/Profile.vue'
import List from './views/List.vue'
import Edit from './views/Edit.vue'
import Template from './views/Template.vue'
import Selection from './views/Selection.vue'
import NotFound from './views/NotFound.vue'
import store from './store/store'

Vue.use(Router)

const router = new Router({
    mode: 'history',
    base: process.env.BASE_URL,
    routes: [
        {
            path: '/',
            name: 'home',
            component: Home
        }, {
            path: '/signin',
            name: 'signin',
            component: Signin
        }, {
            path: '/profile',
            name: 'profile',
            component: Profile
        }, {
            path: '/template/edit',
            name: 'template-edit',
            component: Template
        }, {
            path: '/selection/edit',
            name: 'selection-edit',
            component: Selection
        }, {
            path: '/:module/main/:id?',
            name: 'list',
            component: List
        }, {
            path: '/:module/edit/:id?',
            name: 'edit',
            component: Edit
        }, {
            path: '*',
            name: 'not-found',
            component: NotFound
        }
    ]
});

router.beforeEach((to, from, next) => {
    store.commit('setProgress', 0);
    var $store = store;
    var intervalId = setInterval(() => {
        var progress = $store.state.base.progress + 1;
        if (progress <= 95) {
            $store.commit('setProgress', progress);
        }
    }, 100);
    store.commit('setIntervalId', intervalId);
    store.commit('refreshUser');
    const whiteList = ['/', '/signin'];
    if (whiteList.indexOf(to.path) === -1 && store.state.base.user === null) {
        next('/signin?reply=' + encodeURIComponent(to.path));
    } else if ((to.path === '/signin') && store.state.base.user != null) {
        if (to.query.reply && to.query.reply !== '/') {
            next(decodeURIComponent(to.query.reply));
        } else {
            next('/');
        }
    } else {
        next();
    }
});

export default router