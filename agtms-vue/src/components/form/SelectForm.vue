<template>
    <b-row class="mb-3">
        <b-col sm="2">
            <label :for="field.name + '-input'" class="form-label">
                {{ field.text }}
                <span class="text-danger" v-if="field.required">*</span>
            </label>
        </b-col>
        <b-col sm="10">
            <multiselect class="filter-select"
                v-model="field.value"
                label="text"
                track-by="value"
                select-label=""
                deselect-label=""
                selectedLabel=""
                :state="field.state"
                :searchable="false"
                :options="field.options"
                :placeholder="''" />
            <b-form-invalid-feedback :id="field.name + '-input-feedback'" v-if="field.required">
                {{ $t('please_input_valid') }}{{ field.text }}
            </b-form-invalid-feedback>
        </b-col>
    </b-row>
</template>

<script>
export default {
    name: 'select-form',
    props: [ 'field' ]
}
</script>
