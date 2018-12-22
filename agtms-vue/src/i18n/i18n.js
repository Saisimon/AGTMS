import Vue from 'vue'
import VueI18n from 'vue-i18n'
import zh_CN from './lang/zh_CN'
import en from './lang/en'
import store from '@/store/store'

Vue.use(VueI18n);

const i18n = new VueI18n({
    locale: store.state.base.language,
    messages: {
        'zh_CN': zh_CN,
        'en': en
    }
});

export default i18n;