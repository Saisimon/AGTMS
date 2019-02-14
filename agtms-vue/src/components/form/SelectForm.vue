<template>
    <b-row class="mb-3">
        <b-col sm="2">
            <label :for="field.name + '-input'" class="form-label">
                {{ field.text }}
                <span class="text-danger" v-if="field.required">*</span>
            </label>
        </b-col>
        <b-col sm="10" :class="'invalid'">
            <multiselect class="filter-select"
                v-if="field.multiple"
                v-model="field.value"
                label="text"
                track-by="value"
                select-label=""
                select-group-label=""
                deselect-label=""
                deselect-group-label=""
                selected-label=""
                group-values="options" 
                group-label="group" 
                :group-select="true"
                :allow-empty="!field.required"
                :state="field.state"
                :searchable="false"
                :options="options"
                :multiple="true"
                :placeholder="''" />
            <multiselect class="filter-select"
                v-else
                v-model="field.value"
                label="text"
                track-by="value"
                select-label=""
                deselect-label=""
                selected-label=""
                :allow-empty="!field.required"
                :state="field.state"
                :searchable="false"
                :options="options"
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
    props: [ 'field' ],
    computed: {
        options: function() {
            if (this.field.multiple) {
                return [{
                    group: this.$t('select_all'),
                    options: this.field.options
                }];
            } else {
                return this.field.options;
            }
        }
    }
}
</script>
