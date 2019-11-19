<template>
    <div v-if="batchImport">
        <b-modal v-model="modal.show"
            :title="$t('import')"
            size="lg"
            :hide-footer="true"
            @hidden="initData"
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
                            <treeselect 
                                v-model="importFieldSelects"
                                :options="importFieldOptions"
                                :multiple="true" 
                                :searchable="false"
                                :limit="3"
                                :noChildrenText="$t('no_childrens')"
                                :noOptionsText="$t('no_options')"
                                :noResultsText="$t('no_result')"
                                :placeholder="$t('select_import_fields')" />
                        </b-col>
                    </b-row>
                    <b-row class="mb-3">
                        <b-col>
                            <treeselect 
                                v-model="importFileType"
                                :options="batchImport.importFileTypeOptions"
                                :multiple="false" 
                                :searchable="false"
                                :clearable="false"
                                :noChildrenText="$t('no_childrens')"
                                :noOptionsText="$t('no_options')"
                                :noResultsText="$t('no_result')"
                                :placeholder="$t('select_import_file_type')" />
                        </b-col>
                    </b-row>
                    <b-row class="mb-3" v-if="importFieldSelects.length > 0 && importFileType != null">
                        <b-col>
                            <b-form-file multiple 
                                name="importFiles" 
                                v-model="importFiles"
                                :placeholder="$t('choose_import_file')">
                                <template slot="file-name" slot-scope="{ names }">
                                    {{ $t("files_selected", {count: names.length}) }}
                                </template>
                            </b-form-file>
                        </b-col>
                    </b-row>
                </form>
                <b-row class="mb-2" v-if="importFieldSelects.length > 0 && importFileType != null && importFiles.length > 0">
                    <b-col class="text-right">
                        <b-button variant="outline-primary" 
                            size="sm" 
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
            return this.batchImport.importFieldOptions;
        }
    },
    data: function() {
        return {
            submit: false,
            importFieldSelects: [],
            importFileType: null,
            importFiles: []
        }
    },
    methods: {
        initData: function() {
            this.submit = false;
            this.importFieldSelects = [];
            this.importFileType = null;
            this.importFiles = [];
        },
        save: function() {
            if (this.submit) {
                return;
            }
            this.submit = true;
            if (this.importFieldSelects.length == 0 || this.importFileType == null || this.importFiles.length == 0) {
                this.submit = false;
                return;
            }
            if (this.importFiles.length > 10) {
                this.$emit('showAlert', this.$t('upload_file_max_size_limit', {count: 10}), 'danger');
                this.submit = false;
                return;
            }
            for (var i = 0; i < this.importFiles.length; i++) {
                var importFile = this.importFiles[i];
                if (importFile.size > 20 * 1024 * 1024) {
                    this.$emit('showAlert', this.$t('upload_max_size_limit', {size: 20}), 'danger');
                    this.submit = false;
                    return;
                }
            }
            var importFields = [];
            for (var j = 0; j < this.importFieldSelects.length; j++) {
                var importFieldSelect = this.importFieldSelects[j];
                importFields.push(importFieldSelect);
            }
            var formData = new FormData();
            formData.append("importFileType", this.importFileType);
            formData.append("importFileName", this.batchImport.importFileName);
            formData.append("importFields", importFields);
            for (var i = 0; i < this.importFiles.length; i++) {
                formData.append("importFiles", this.importFiles[i]);
            }
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
                this.submit = false;
            }).catch(err => {
                this.submit = false;
            });
        }
    }
}
</script>
