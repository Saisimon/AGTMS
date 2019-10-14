<template>
    <b-row class="mb-2">
        <b-col>
            <label :for="field.name + '-input'" class="form-label">
                <span class="text-danger" v-if="field.required">*</span>
                {{ field.text }} :
            </label>
            <b-form-input
                :id="field.name + '-input'" 
                v-model.trim="field.value" 
                :state="field.state" 
                :disabled="field.disabled" />
            <b-form-invalid-feedback :id="field.name + '-input-feedback'" v-if="field.required">
                {{ $t('please_input_valid') }}{{ field.text }}
            </b-form-invalid-feedback>
            <div class="preview-div">
                <span class="mr-1">{{$t('preview')}}:</span>
                <i class="fa" :class="'fa-' + field.value"></i>
                <a class="ml-2" href="http://www.fontawesome.com.cn/faicons/" target="_blank">{{$t('reference')}}</a>
            </div>
        </b-col>
    </b-row>
</template>

<script>
export default {
    name: 'icon-form',
    props: [ 'field' ]
}
</script>

<style scoped>
.preview-div {
    font-size: 12px;
    margin-left: 0.5rem;
    margin-top: 0.5rem;
}
</style>

