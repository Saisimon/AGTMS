<template>
    <div class="export-container" v-if="batchExport">
        <b-modal v-model="modal.show"
            :title="$t('export')"
            size="lg"
            :hide-footer="true"
            header-border-variant="light"
            button-size="sm">
            <div class="form-container">
                <b-row class="mb-3">
                    <b-col>
                        <b-form-input v-model.trim="batchExport.exportFileName" :placeholder="$t('input_export_file_name')" />
                    </b-col>
                </b-row>
                <b-row class="mb-3">
                    <b-col>
                        <!-- <multiselect style="z-index: 10"
                            v-model="exportFieldSelects"
                            label="label"
                            track-by="id"
                            select-label=""
                            select-group-label=""
                            deselect-label=""
                            deselect-group-label=""
                            selected-label=""
                            group-values="options" 
                            group-label="group" 
                            :limit="3"
                            :group-select="true"
                            :searchable="false"
                            :multiple="true"
                            :options="exportFieldOptions"
                            :placeholder="$t('select_export_fields')" >
                            <template slot="noResult">{{ $t("no_result") }}</template>
                            <template slot="noOptions">{{ $t("no_options") }}</template>
                        </multiselect> -->
                        <treeselect 
                            v-model="exportFieldSelects"
                            :options="exportFieldOptions"
                            :multiple="true" 
                            :searchable="false"
                            :limit="3"
                            :noChildrenText="$t('no_childrens')"
                            :noOptionsText="$t('no_options')"
                            :noResultsText="$t('no_result')"
                            :placeholder="$t('select_export_fields')" />
                    </b-col>
                </b-row>
                <b-row class="mb-3">
                    <b-col>
                        <!-- <multiselect class="batch-select"
                            v-model="exportFileType"
                            label="label"
                            track-by="id"
                            select-label=""
                            deselect-label=""
                            selected-label=""
                            :allow-empty="false"
                            :searchable="false"
                            :options="batchExport.exportFileTypeOptions"
                            :placeholder="$t('select_export_file_type')" >
                            <template slot="noResult">{{ $t("no_result") }}</template>
                            <template slot="noOptions">{{ $t("no_options") }}</template>
                        </multiselect> -->
                        <treeselect 
                            v-model="exportFileType"
                            :options="batchExport.exportFileTypeOptions"
                            :multiple="false" 
                            :searchable="false"
                            :clearable="false"
                            :noChildrenText="$t('no_childrens')"
                            :noOptionsText="$t('no_options')"
                            :noResultsText="$t('no_result')"
                            :placeholder="$t('select_export_file_type')" />
                    </b-col>
                </b-row>
                <b-row class="mb-3" v-if="exportFieldSelects.length > 0 && exportFileType != null">
                    <b-col class="text-right">
                        <b-button variant="primary" 
                            @click="save">
                            <i class="fa fa-fw fa-download"></i>
                            {{ $t("export") }}
                        </b-button>
                    </b-col>
                </b-row>
            </div>
        </b-modal>
    </div>
</template>

<script>
export default {
    name: 'action-export',
    props: [
        "batchExport", 
        "selects",
        "modal",
        "filter"
    ],
    computed: {
        exportFieldOptions: function() {
            return this.batchExport.exportFieldOptions;
        }
    },
    data: function() {
        return {
            submit: false,
            exportFieldSelects: [],
            exportFileType: null
        }
    },
    methods: {
        save: function() {
            if (this.submit) {
                return;
            }
            this.submit = true;
            var data = {
                filter: this.filter
            }
            if (this.exportFieldSelects.length == 0 || this.exportFileType == null) {
                this.submit = false;
                return;
            }
            var exportFields = [];
            for (var i = 0; i < this.exportFieldSelects.length; i++) {
                var exportFieldSelect = this.exportFieldSelects[i];
                exportFields.push(exportFieldSelect);
            }
            data['exportFields'] = exportFields;
            data['exportFileType'] = this.exportFileType;
            data['exportFileName'] = this.batchExport.exportFileName;
            this.$store.dispatch('batchExportData', {
                url: this.$route.path,
                data: data
            }).then(resp => {
                var data = resp.data;
                if (data.code === 0) {
                    this.modal.show = false;
                    this.$emit('showAlert', data.message, 'success');
                } else {
                    this.$emit('showAlert', data.message, 'danger');
                }
                this.submit = false;
            }).catch(err => {
                this.submit = false;
            });
        }
    }
}
</script>