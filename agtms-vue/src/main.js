import Vue from 'vue';
import BootstrapVue from 'bootstrap-vue';
import VueGoodTablePlugin from 'vue-good-table';
import Datepicker from 'vuejs-datepicker';
import Treeselect from '@riophae/vue-treeselect';
import App from './App.vue';
import router from './router';
import store from './store/store';
import i18n from './i18n/i18n';
import axios from 'axios';

import 'font-awesome/css/font-awesome.css';
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap-vue/dist/bootstrap-vue.css';
import 'vue-good-table/dist/vue-good-table.css';
import '@riophae/vue-treeselect/dist/vue-treeselect.css'

Vue.config.productionTip = false;
axios.defaults.withCredentials = true;
Vue.use(BootstrapVue);
Vue.use(VueGoodTablePlugin);
Vue.component('datepicker', Datepicker);
Vue.component('treeselect', Treeselect);
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

Vue.prototype.getLabel = function (options, id) {
	if (options != null && options.length > 0) {
		for (var i = 0; i < options.length; i++) {
			if (options[i].id === id) {
				return options[i].label;
			}
		}
	}
	return null;
}

new Vue({
    router,
    store,
    i18n,
    render: h => h(App)
}).$mount('#app');