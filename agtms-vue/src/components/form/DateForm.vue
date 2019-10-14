<template>
    <b-row class="mb-2">
        <b-col>
            <label :for="field.name + '-input'" class="form-label">
                <span class="text-danger" v-if="field.required">*</span>
                {{ field.text }} :
            </label>
            <datepicker v-model="field.value"
                :id="field.name + '-input'" 
                :clear-button="true"
                :language="datepickerLanguage"
                :state="field.state"
                :disabled="field.disabled" 
                :input-class="inputClass"
                format="yyyy-MM-dd" 
                class="datepicker-container" />
            <b-form-invalid-feedback :id="field.name + '-input-feedback'" v-if="field.required" :class="feedbackClass">
                {{ $t('please_select_valid') }}{{ field.text }}
            </b-form-invalid-feedback>
        </b-col>
    </b-row>
</template>

<script>
import * as locale from 'vuejs-datepicker/dist/locale'

export default {
    name: 'date-form',
    props: [ 'field' ],
    data: function() {
        return {
            inputClass: 'datepicker-input',
            feedbackClass: ''
        }
    },
    updated: function() {
        if (this.field) {
            if (this.field.state == false) {
                this.inputClass = 'datepicker-input border-danger'
                this.feedbackClass = 'd-block'
            } else {
                this.inputClass = 'datepicker-input'
                this.feedbackClass = ''
            }
        }
    },
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
