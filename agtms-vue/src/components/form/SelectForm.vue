<template>
    <b-row class="mb-3">
        <b-col :class="'invalid'">
            <label :for="field.name + '-input'" class="form-label font-weight-bold">
                {{ field.text }}
                <span class="text-danger" v-if="field.required">*</span>
            </label>
            <multiselect
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
                :disabled="field.disabled"
                :searchable="field.searchable"
                :loading="isLoading"
                :options="options"
                :limit="3"
                :multiple="true"
                :placeholder="''"
                @search-change="search" >
                <template slot="noResult">{{ $t("no_result") }}</template>
                <template slot="noOptions">{{ $t("no_options") }}</template>
            </multiselect>
            <multiselect
                v-else
                v-model="field.value"
                label="text"
                track-by="value"
                select-label=""
                deselect-label=""
                selected-label=""
                :allow-empty="!field.required"
                :disabled="field.disabled"
                :searchable="field.searchable"
                :loading="isLoading"
                :options="options"
                :placeholder="''"
                @search-change="search" >
                <template slot="noResult">{{ $t("no_result") }}</template>
                <template slot="noOptions">{{ $t("no_options") }}</template>
            </multiselect>
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
    data: function() {
        return {
            isLoading: false
        }
    },
    computed: {
        options: function() {
            var options = this.field.options;
            if (options == null) {
                options = [];
            }
            if (this.field.multiple) {
                return [{
                    group: this.$t('select_all'),
                    options: options
                }];
            } else {
                return options;
            }
        }
    },
    methods: {
        search: function(query) {
            if (this.field.sign == null) {
                return;
            }
            this.isLoading = true;
            this.$store.dispatch('searchSelection', {
                sign: this.field.sign,
                keyword: query
            }).then(resp => {
                if (resp.data.code === 0) {
                    var options = resp.data.data;
                    this.field.options = options;
                }
                this.isLoading = false;
            });
        }
    }
}
</script>
