<template>
    <div class="export-container" v-if="batchExport">
        <b-modal v-model="model.show"
            :title="$t('export')"
            size="lg"
            :hide-footer="true"
            button-size="sm">
            <div class="form-container">
                <b-row class="mb-3">
                    <b-col>
                        <multiselect style="z-index: 10"
                            v-model="exportFieldSelects"
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
                            :searchable="false"
                            :multiple="true"
                            :options="exportFieldOptions"
                            :placeholder="$t('select_export_fields')" />
                    </b-col>
                </b-row>
                <b-row class="mb-3">
                    <b-col>
                        <multiselect class="batch-select"
                            v-model="exportFileType"
                            label="text"
                            track-by="value"
                            select-label=""
                            deselect-label=""
                            selected-label=""
                            :allow-empty="false"
                            :searchable="false"
                            :options="batchExport.exportFileTypeOptions"
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
        "model",
        "filter"
    ],
    computed: {
        exportFieldOptions: function() {
            return [{
                group: this.$t('select_all'),
                options: this.batchExport.exportFieldOptions
            }]
        }
    },
    data: function() {
        return {
            exportFieldSelects: [],
            exportFileType: null
        }
    },
    methods: {
        save: function() {
            var data = {
                ids: this.selects,
                filter: this.filter
            }
            if (this.exportFieldSelects.length <= 0 || this.exportFileType == null) {
                return;
            }
            var exportFields = [];
            for (var i = 0; i < this.exportFieldSelects.length; i++) {
                var exportFieldSelect = this.exportFieldSelects[i];
                exportFields.push(exportFieldSelect.value);
            }
            data['exportFields'] = exportFields;
            data['exportFileType'] = this.exportFileType.value;
            this.$store.dispatch('batchExportData', {
                url: this.$route.path,
                data: data
            }).then(resp => {
                var data = resp.data;
                if (data.code === 0) {
                    this.model.show = false;
                }
            });
        }
    }
}
</script>