<template>
    <b-row class="mb-3">
        <b-col sm="2">
            <label :for="field.name + '-input'" class="form-label">
                {{ field.text }}
                <span class="text-danger" v-if="field.required">*</span>
            </label>
        </b-col>
        <b-col sm="10">
            <datepicker v-model="field.value"
                :clear-button="true"
                :language="datepickerLanguage"
                :state="field.state"
                input-class="datepicker-input"
                format="yyyy-MM-dd" 
                class="datepicker-container" />
            <b-form-invalid-feedback :id="field.name + '-input-feedback'" v-if="field.required">
                {{ $t('please_input_valid') }}{{ field.text }}
            </b-form-invalid-feedback>
        </b-col>
    </b-row>
</template>

<script>
import * as locale from 'vuejs-datepicker/dist/locale'

export default {
    name: 'date-form',
    props: [ 'field' ],
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
