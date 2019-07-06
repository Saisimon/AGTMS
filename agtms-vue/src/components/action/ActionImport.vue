<template>
    <div class="import-container" v-if="batchImport">
        <b-modal v-model="modal.show"
            :title="$t('import')"
            size="lg"
            :hide-footer="true"
            header-border-variant="light"
            button-size="sm">
            <div class="form-container">
                <form id="importFile" name="importFile" method="POST">
                    <b-row class="mb-3">
                        <b-col>
                            <b-form-input name="importFileName" v-model.trim="batchImport.importFileName" :placeholder="$t('input_import_file_name')" />
                        </b-col>
                    </b-row>
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
                                :limit="3"
                                :group-select="true"
                                :searchable="false"
                                :multiple="true"
                                :options="importFieldOptions"
                                :placeholder="$t('select_import_fields')" >
                            <template slot="noResult">{{ $t("no_result") }}</template>
                            <template slot="noOptions">{{ $t("no_options") }}</template>
                        </multiselect>
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
                                :placeholder="$t('select_import_file_type')" >
                            <template slot="noResult">{{ $t("no_result") }}</template>
                            <template slot="noOptions">{{ $t("no_options") }}</template>
                        </multiselect>
                        </b-col>
                    </b-row>
                    <b-row class="mb-3" v-if="importFieldSelects.length > 0 && importFileType != null">
                        <b-col>
                            <b-form-file name="importFile" v-model="importFile" :placeholder="$t('choose_import_file')" :accept="'.' + importFileType.value" ></b-form-file>
                        </b-col>
                    </b-row>
                </form>
                <b-row class="mb-3" v-if="importFieldSelects.length > 0 && importFileType != null && importFile">
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
        "modal"
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
            if (this.importFieldSelects.length == 0 || this.importFileType == null || !this.importFile) {
                return;
            }
            if (this.importFile.size > 10 * 1024 * 1024) {
                this.$emit('showAlert', this.$t('upload_file_max_size_limit'), 'danger');
                return;
            }
            var importFields = [];
            for (var i = 0; i < this.importFieldSelects.length; i++) {
                var importFieldSelect = this.importFieldSelects[i];
                importFields.push(importFieldSelect.value);
            }
            var formData = new FormData();
            formData.append("importFileType", this.importFileType.value);
            formData.append("importFileName", this.batchImport.importFileName);
            formData.append("importFields", importFields);
            formData.append("importFile", this.importFile);
            this.$store.dispatch('batchImportData', {
                url: this.$route.path,
                data: formData
            }).then(resp => {
                var data = resp.data;
                if (data.code === 0) {
                    this.modal.show = false;
                    this.$emit('showAlert', data.message, 'success');
                } else {
                    this.$emit('showAlert', data.message, 'danger');
                }
            });
        }
    }
}
</script>
