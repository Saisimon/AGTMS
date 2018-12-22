<template>
    <div class="batch-edit-container">
        <b-modal v-model="model.show"
            :title="$t('batch_edit')"
            size="lg"
            :hide-footer="true"
            button-size="sm">
            <div class="form-container">
                <b-row class="mb-3">
                    <b-col sm="2">
                        <label for="select-input" class="form-label">
                            {{$t('select_edit_fields')}}
                        </label>
                    </b-col>
                    <b-col sm="10">
                        <multiselect class="filter-select"
                            v-model="editFieldSelects"
                            label="text"
                            track-by="value"
                            select-label=""
                            deselect-label=""
                            selectedLabel=""
                            :searchable="false"
                            :multiple="true"
                            :options="editFieldOptions"
                            :placeholder="''" />
                    </b-col>
                </b-row>
                <b-row class="mb-3" v-for="(editFieldSelect, index) in editFieldSelects" :key="index">
                    <b-col sm="2">
                        <label class="form-label">
                            {{editFieldSelect.text}}
                            <span class="text-danger" v-if="editFields[editFieldSelect.value].required">*</span>
                        </label>
                    </b-col>
                    <b-col sm="10">
                        <b-form-input 
                            v-model.trim="editFields[editFieldSelect.value].value" 
                            :state="editFields[editFieldSelect.value].state"
                            :type="editFields[editFieldSelect.value].type" />
                        <b-form-invalid-feedback v-if="editFields[editFieldSelect.value].required">
                            {{$t('please_input_valid')}}{{editFieldSelect.text}}
                        </b-form-invalid-feedback>
                    </b-col>
                </b-row>
                <b-row class="mb-3" v-if="editFieldSelects.length > 0">
                    <b-col class="text-right" sm="12">
                        <b-button variant="primary" 
                            @click="batchEdit"
                            size="sm">
                            <i class="fa fa-fw fa-save"></i>
                            {{ $t("confirm_save") }}
                        </b-button>
                    </b-col>
                </b-row>
            </div>
        </b-modal>
    </div>
</template>

<script>
export default {
    name: 'action-batch-edit',
    methods: {
        batchEdit: function() {
            var selects = this.$store.state.list.selects;
            var data = {
                ids: selects
            }
            var pass = true;
            for (var i in this.editFieldSelects) {
                var editFieldSelect = this.editFieldSelects[i];
                var key = editFieldSelect.value;
                var value = this.editFields[key].value;
                if (this.editFields[key].required && (value == undefined || value == "")) {
                    pass = false;
                    this.editFields[key].state = false;
                } else {
                    this.editFields[key].state = null;
                    data[key] = value;
                }
            }
            if (pass) {
                this.$store.dispatch('batchEditData', {
                    url: this.$route.path,
                    data: data
                }).then(resp => {
                    var data = resp.data;
                    if (data.code === 0) {
                        this.model.show = false;
                        this.$emit('on-custom-comp');
                    }
                });
            }
        }
    },
    data: function() {
        return {
            editFieldSelects: [],
            editFieldOptions: [
                { value: 'parentId', text: this.$t('parent_navigate'), $isDisabled: true },
                { value: 'icon', text: this.$t('icon') },
                { value: 'title', text: this.$t('title'), $isDisabled: true },
                { value: 'priority', text: this.$t('priority') }
            ],
            editFields: {
                icon: {
                    required: true,
                    state: null,
                    type: 'text',
                    value: ''
                },
                priority: {
                    required: false,
                    state: null,
                    type: 'number',
                    value: 0
                }
            }
        }
    },
    props: ['model']
}
</script>