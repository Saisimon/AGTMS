<template>
    <b-row class="mb-3">
        <b-col sm="2">
            <label :for="field.name + '-input'" class="form-label">
                {{ field.text }}
                <span class="text-danger" v-if="field.required">*</span>
            </label>
        </b-col>
        <b-col sm="10">
            <b-form-textarea :id="field.name + '-input'"
                    :state="field.state"
                    v-model.trim="field.value"
                    placeholder="Enter something"
                    :max-rows="6">
            </b-form-textarea>
            <b-form-invalid-feedback :id="field.name + '-input-feedback'" v-if="field.required">
                {{ $t('please_input_valid') }}{{ field.text }}
            </b-form-invalid-feedback>
        </b-col>
    </b-row>
</template>

<script>
export default {
    name: 'text-form',
    props: [ 'field' ]
}
</script>
