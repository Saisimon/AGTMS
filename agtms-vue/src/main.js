import Vue from 'vue'
import BootstrapVue from 'bootstrap-vue'
import {VTable, VPagination} from 'vue-easytable'
import Multiselect from 'vue-multiselect'
import Datepicker from 'vuejs-datepicker';
import App from './App.vue'
import router from './router'
import store from './store/store'
import i18n from './i18n/i18n'
import axios from 'axios'

import 'font-awesome/css/font-awesome.css'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import 'vue-easytable/libs/themes-base/index.css'
import 'vue-multiselect/dist/vue-multiselect.min.css'

Vue.config.productionTip = false;
axios.defaults.withCredentials = true;
Vue.use(BootstrapVue);
Vue.component(VTable.name, VTable);
Vue.component(VPagination.name, VPagination);
Vue.component('multiselect', Multiselect);
Vue.component('datepicker', Datepicker);
Vue.prototype.cloneObject = function (obj) {
    var newObj = {};
	if (obj instanceof Array) {
		newObj = [];
	}
	for (var key in obj) {
		var val = obj[key];
		newObj[key] = typeof val === 'object' ? this.cloneObject(val) : val;
	}
	return newObj;
}

Vue.prototype.isNullEmpty = function (a) {
	if ((typeof a) == 'string') {
		if (a) {
			return false;
		} else {
			return true;
		}
	} else if ((typeof a) == 'number' && !isNaN(a)) {
		return false;
	} else {
		return true;
	} 
}

new Vue({
    router,
    store,
    i18n,
    render: h => h(App)
}).$mount('#app');