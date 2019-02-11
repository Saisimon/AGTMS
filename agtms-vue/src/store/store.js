import Vue from 'vue'
import Vuex from 'vuex'
import base from './modules/base'
import list from './modules/list'
import edit from './modules/edit'
import template from './modules/template'
import navigation from './modules/navigation'

Vue.use(Vuex)

export default new Vuex.Store({
    modules: {
        base,
        list,
        edit,
        template,
        navigation
    }
})
