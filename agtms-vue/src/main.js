import Vue from 'vue'
import BootstrapVue from 'bootstrap-vue'
import VueGoodTablePlugin from 'vue-good-table';
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
import 'vue-good-table/dist/vue-good-table.css'
import 'vue-multiselect/dist/vue-multiselect.min.css'

Vue.config.productionTip = false;
axios.defaults.withCredentials = true;
Vue.use(BootstrapVue);
Vue.use(VueGoodTablePlugin);
Vue.component('multiselect', Multiselect);
Vue.component('datepicker', Datepicker);
Vue.prototype.cloneObject = function (obj) {
    var newObj = {};
	if (obj instanceof Array) {
		newObj = [];
	}
	for (var key in obj) {
		var val = obj[key];
		if (val != null) {
			newObj[key] = typeof val === 'object' ? this.cloneObject(val) : val;
		} else {
			newObj[key] = null;
		}
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
	} else if ((typeof a) == 'object' && a != null) {
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