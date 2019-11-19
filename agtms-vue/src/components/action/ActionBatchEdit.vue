<template>
    <div v-if="batchEdit">
        <b-modal v-model="modal.show"
            :title="$t('batch_edit')"
            size="lg"
            :hide-footer="true"
            @hidden="initData"
            header-border-variant="light"
            button-size="sm">
            <div class="form-container">
                <b-row class="mb-3">
                    <b-col>
                        <treeselect 
                            class="edit-field-select"
                            v-model="editFieldSelects"
                            :options="batchEdit.editFieldOptions"
                            :multiple="true" 
                            :searchable="false"
                            :clearable="false"
                            :limit="3"
                            :noChildrenText="$t('no_childrens')"
                            :noOptionsText="$t('no_options')"
                            :noResultsText="$t('no_result')"
                            :placeholder="$t('select_edit_fields')" />
                    </b-col>
                </b-row>
                <template v-for="(editFieldSelect, index) in editFieldSelects" >
                    <select-form :field="batchEdit.editFields[editFieldSelect]" :key="index" v-if="batchEdit.editFields[editFieldSelect].views == 'selection'" />
                    <date-form :field="batchEdit.editFields[editFieldSelect]" :key="index" v-else-if="batchEdit.editFields[editFieldSelect].type == 'date'" />
                    <textarea-form :field="batchEdit.editFields[editFieldSelect]" :key="index" v-else-if="batchEdit.editFields[editFieldSelect].views == 'textarea'" />
                    <icon-form :field="batchEdit.editFields[editFieldSelect]" :key="index" v-else-if="batchEdit.editFields[editFieldSelect].views == 'icon'" />
                    <image-form :field="batchEdit.editFields[editFieldSelect]" :key="index" v-else-if="batchEdit.editFields[editFieldSelect].views == 'image'" />
                    <password-form :field="batchEdit.editFields[editFieldSelect]" :key="index" v-else-if="batchEdit.editFields[editFieldSelect].views == 'password'" />
                    <text-form :field="batchEdit.editFields[editFieldSelect]" :key="index" v-else />
                </template>
                <b-row class="mb-2" v-if="editFieldSelects.length > 0">
                    <b-col class="text-right">
                        <b-button variant="outline-primary" 
                            size="sm" 
                            class="save-btn"
                            @click="save">
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
import TextForm from '@/components/form/TextForm.vue'
import TextareaForm from '@/components/form/TextareaForm.vue'
import IconForm from '@/components/form/IconForm.vue'
import SelectForm from '@/components/form/SelectForm.vue'
import DateForm from '@/components/form/DateForm.vue'
import ImageForm from '@/components/form/ImageForm.vue'
import PasswordForm from '@/components/form/PasswordForm.vue'

export default {
    name: 'action-batch-edit',
    components: {
        'text-form': TextForm,
        'textarea-form': TextareaForm,
        'icon-form': IconForm,
        'select-form': SelectForm,
        'date-form': DateForm,
        'image-form': ImageForm,
        'password-form': PasswordForm
    },
    props: [
        "batchEdit", 
        "selects",
        "modal"
    ],
    data: function() {
        return {
            submit: false,
            editFieldSelects: []
        }
    },
    methods: {
        initData: function() {
            this.submit = false;
            this.editFieldSelects = [];
        },
        save: function() {
            if (this.submit) {
                return;
            }
            this.submit = true;
            var data = {
                ids: this.selects
            }
            var pass = true;
            for (var i in this.editFieldSelects) {
                var editFieldSelect = this.editFieldSelects[i];
                var field = this.batchEdit.editFields[editFieldSelect];
                var value = field.value;
                if (field.required && this.isNullEmpty(value)) {
                    pass = false;
                    field.state = false;
                } else {
                    field.state = null;
                    data[editFieldSelect] = value;
                }
            }
            if (pass) {
                this.$store.dispatch('batchEditData', {
                    url: this.$route.path,
                    data: data
                }).then(resp => {
                    var data = resp.data;
                    if (data.code === 0) {
                        this.modal.show = false;
                        this.$emit('succeed');
                    } else {
                        this.$emit('failed');
                    }
                    this.submit = false;
                }).catch(err => {
                    this.$emit('failed');
                    this.submit = false;
                });
            } else {
                this.submit = false;
            }
        }
    }
}
</script>