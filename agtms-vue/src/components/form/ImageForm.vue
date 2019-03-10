<template>
    <b-row class="mb-3">
        <b-col sm="2">
            <label :for="field.name + '-input'" class="form-label">
                {{ field.text }}
                <span class="text-danger" v-if="field.required">*</span>
            </label>
        </b-col>
        <b-col sm="10">
            <b-form-input class="border-top-0 border-right-0 border-left-0 rounded-0" :id="field.name + '-input'" :name="field.name" v-model.trim="field.value" :state="field.state" type="url" @change="blank = false" />
            <b-form-invalid-feedback :id="field.name + '-input-feedback'" v-if="field.required">
                {{ $t('please_input_valid') }}{{ field.text }}
            </b-form-invalid-feedback>
            <b-img class="mt-2" :src="field.value" thumbnail fluid :blank="blank" :width="200" @error="blank = true" />
        </b-col>
    </b-row>
</template>

<script>
export default {
    name: 'image-form',
    props: [ 'field' ],
    data: function() {
        return {
            blank: false
        }
    }
}
</script>
