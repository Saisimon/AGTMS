<template>
    <div class="batch-edit-container" v-if="batchEdit">
        <b-modal v-model="modal.show"
            :title="$t('batch_edit')"
            size="lg"
            :hide-footer="true"
            header-border-variant="light"
            button-size="sm">
            <div class="form-container">
                <b-row class="mb-3">
                    <b-col>
                        <multiselect v-model="editFieldSelects"
                            style="z-index: 11"
                            label="text"
                            track-by="value"
                            select-label=""
                            deselect-label=""
                            selected-label=""
                            :limit="3"
                            :searchable="false"
                            :multiple="true"
                            :options="batchEdit.editFieldOptions"
                            :placeholder="$t('select_edit_fields')" >
                            <template slot="noResult">{{ $t("no_result") }}</template>
                            <template slot="noOptions">{{ $t("no_options") }}</template>
                        </multiselect>
                    </b-col>
                </b-row>
                <template v-for="(editFieldSelect, index) in editFieldSelects" >
                    <select-form :field="batchEdit.editFields[editFieldSelect.value]" :key="index" v-if="batchEdit.editFields[editFieldSelect.value].views == 'selection'" />
                    <date-form :field="batchEdit.editFields[editFieldSelect.value]" :key="index" v-else-if="batchEdit.editFields[editFieldSelect.value].type == 'date'" />
                    <textarea-form :field="batchEdit.editFields[editFieldSelect.value]" :key="index" v-else-if="batchEdit.editFields[editFieldSelect.value].views == 'textarea'" />
                    <icon-form :field="batchEdit.editFields[editFieldSelect.value]" :key="index" v-else-if="batchEdit.editFields[editFieldSelect.value].views == 'icon'" />
                    <image-form :field="batchEdit.editFields[editFieldSelect.value]" :key="index" v-else-if="batchEdit.editFields[editFieldSelect.value].views == 'image'" />
                    <password-form :field="batchEdit.editFields[editFieldSelect.value]" :key="index" v-else-if="batchEdit.editFields[editFieldSelect.value].views == 'password'" />
                    <text-form :field="batchEdit.editFields[editFieldSelect.value]" :key="index" v-else />
                </template>
                <b-row class="mb-3" v-if="editFieldSelects.length > 0">
                    <b-col class="text-right">
                        <b-button variant="primary" 
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
                var key = editFieldSelect.value;
                var field = this.batchEdit.editFields[key];
                var value = field.value;
                if (field.required && (value == undefined || value == "")) {
                    pass = false;
                    field.state = false;
                } else {
                    field.state = null;
                    if (field.views === 'selection') {
                        data[key] = value.value;
                    } else {
                        data[key] = value;
                    }
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
                    this.submit = false;
                });
            } else {
                this.submit = false;
            }
        }
    }
}
</script>