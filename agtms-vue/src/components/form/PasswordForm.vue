<template>
    <b-row class="mb-3">
        <b-col>
            <label :for="field.name + '-input'" class="form-label font-weight-bold">
                {{ field.text }}
                <span class="text-danger" v-if="field.required">*</span>
            </label>
            <b-input-group>
                <b-form-input class="border-top-0 border-right-0 border-left-0 rounded-0" 
                    v-if="show" 
                    :id="field.name + '-input'" 
                    :name="field.name" 
                    v-model.trim="field.value" 
                    :disabled="field.disabled" 
                    :state="field.state" 
                    type="text" />
                <b-form-input class="border-top-0 border-right-0 border-left-0 rounded-0" 
                    v-else 
                    :id="field.name + '-input'" 
                    :name="field.name" 
                    v-model.trim="field.value" 
                    :disabled="field.disabled" 
                    :state="field.state" 
                    type="password" />
                <b-input-group-text class="border-top-0 border-right-0 border-left-0 rounded-0" slot="append" style="cursor: pointer;">
                    <i class="fa fa-eye-slash" @click="show = false" v-if="show"></i>
                    <i class="fa fa-eye" @click="show = true" v-else></i>
                </b-input-group-text>
            </b-input-group>
            <b-form-invalid-feedback :id="field.name + '-input-feedback'" v-if="field.required" :class="{'d-block': field.state == false}">
                {{ $t('please_input_valid') }}{{ field.text }}
            </b-form-invalid-feedback>
        </b-col>
    </b-row>
</template>

<script>
export default {
    name: 'password-form',
    props: [ 'field' ],
    data: function() {
        return {
            show: false
        }
    }
}
</script>

