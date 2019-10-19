<template>
    <b-row class="mb-2">
        <b-col :class="'invalid'">
            <label :for="field.name + '-input'" class="form-label">
                <span class="text-danger" v-if="field.required">*</span>
                {{ field.text }} :
            </label>
            <treeselect 
                :id="field.name + '-input'" 
                v-model="field.value"
                :options="options"
                :multiple="field.multiple" 
                :limit="3"
                :searchable="field.searchable"
                :clearable="field.required"
                :disabled="field.disabled"
                :flat="field.flat"
                :value-consists-of="field.consists"
                :noChildrenText="$t('no_childrens')"
                :noOptionsText="$t('no_options')"
                :noResultsText="$t('no_result')"
                :placeholder="''"
                @search-change="search" />
            <b-form-invalid-feedback :id="field.name + '-input-feedback'" v-if="field.required" :class="{'d-block': field.state == false}">
                {{ $t('please_select_valid') }}{{ field.text }}
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
            var options = this.field.options;
            if (options == null) {
                options = [];
            }
            return options;
        }
    },
    methods: {
        search: function(query) {
            if (this.field.sign == null) {
                return;
            }
            this.$store.dispatch('searchSelection', {
                sign: this.field.sign,
                keyword: query
            }).then(resp => {
                if (resp.data.code === 0) {
                    var options = resp.data.data;
                    this.field.options = options;
                }
            });
        }
    }
}
</script>
