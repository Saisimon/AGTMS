<template>
    <datepicker v-model="input.value"
        :clear-button="true"
        :language="datepickerLanguage"
        :calendar-class="calendarClass"
        input-class="datepicker-input"
        format="yyyy-MM-dd" 
        class="datepicker-container" />
</template>

<script>
import * as locale from 'vuejs-datepicker/dist/locale'

export default {
    name: 'search-filter-datepicker',
    props: ['input', 'calendarClass'],
    computed: {
        datepickerLanguage: function() {
            var lang = this.$store.state.base.language;
            switch (lang) {
                case 'zh_CN':
                case 'zh_TW':
                    return locale.zh;
                default:
                    return locale.en;
            }
        }
    }
}
</script>

<style>
.datepicker-container {
    height: 36px!important;
    width: 100%!important;
}
.datepicker-input {
    height: 36px!important;
    width: 100%!important;
    border: 1px solid #e8e8e8!important;
    padding: 0.375rem 0.75rem;
}
.vdp-datepicker__clear-button {
    position: relative;
    top: -34px;
    float: right;
    right: 15px;
    font-size: 18px;
}
.filter-datepicker-calendar-right {
    right: 0;
}
</style>