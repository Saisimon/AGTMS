<template>
    <div class="import-container" v-if="batchImport">
        <b-modal v-model="model.show"
            :title="$t('import')"
            size="lg"
            :hide-footer="true"
            button-size="sm">
            <div class="form-container">
                <b-row class="mb-3">
                    <b-col>
                        <multiselect style="z-index: 11"
                            v-model="importFieldSelects"
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
                            :options="importFieldOptions"
                            :placeholder="$t('select_import_fields')" />
                    </b-col>
                </b-row>
                <b-row class="mb-3">
                    <b-col>
                        <multiselect style="z-index: 10"
                            v-model="importFileType"
                            label="text"
                            track-by="value"
                            select-label=""
                            deselect-label=""
                            selected-label=""
                            :allow-empty="false"
                            :searchable="false"
                            :options="batchImport.importFileTypeOptions"
                            :placeholder="$t('select_import_file_type')" />
                    </b-col>
                </b-row>
                <b-row class="mb-3" v-if="importFieldSelects.length > 0 && importFileType != null">
                    <b-col>
                        <b-form-file v-model="importFile" :placeholder="$t('choose_import_file')" :accept="'.' + importFileType.value" ></b-form-file>
                    </b-col>
                </b-row>
                <b-row class="mb-3" v-if="importFieldSelects.length > 0 && importFileType != null && Boolean(importFile)">
                    <b-col class="text-right">
                        <b-button variant="primary" 
                            @click="save">
                            <i class="fa fa-fw fa-upload"></i>
                            {{ $t("import") }}
                        </b-button>
                    </b-col>
                </b-row>
            </div>
        </b-modal>
    </div>
</template>

<script>
export default {
    name: 'action-import',
    props: [
        "batchImport", 
        "selects",
        "model"
    ],
    computed: {
        importFieldOptions: function() {
            return [{
                group: this.$t('select_all'),
                options: this.batchImport.importFieldOptions
            }]
        }
    },
    data: function() {
        return {
            importFieldSelects: [],
            importFileType: null,
            importFile: null
        }
    },
    methods: {
        save: function() {

        }
    }
}
</script>
