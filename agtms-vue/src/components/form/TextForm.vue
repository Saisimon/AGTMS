<template>
    <b-row class="mb-3">
        <b-col sm="2">
            <label :for="field.name + '-input'" class="form-label">
                {{ field.text }}
                <span class="text-danger" v-if="field.required">*</span>
            </label>
        </b-col>
        <b-col sm="10">
            <b-form-input :id="field.name + '-input'" v-model.trim="field.value" :state="field.state" :type="type" />
            <b-form-invalid-feedback :id="field.name + '-input-feedback'" v-if="field.required">
                {{ $t('please_input_valid') }}{{ field.text }}
            </b-form-invalid-feedback>
        </b-col>
    </b-row>
</template>

<script>
export default {
    name: 'text-form',
    props: [ 'field' ],
    data: function() {
        var type = 'text';
        if (this.field) {
            if (this.field.type == 'long' || this.field.type == 'double') {
                type = 'number';
            } else if (this.field.view == 'link' || this.field.view == 'image') {
                type = 'url';
            } else if (this.field.view == 'email') {
                type = 'email';
            }
        }
        return {
            type: type
        }
    }
}
</script>
