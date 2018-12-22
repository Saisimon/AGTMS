import Vue from 'vue'
import Vuex from 'vuex'
import base from './modules/base'
import list from './modules/list'
import edit from './modules/edit'
import navigate from './modules/navigate'

Vue.use(Vuex)

export default new Vuex.Store({
    modules: {
        base,
        list,
        edit,
        navigate
    }
})
