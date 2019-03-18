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
                            label="text"
                            track-by="value"
                            select-label=""
                            deselect-label=""
                            selected-label=""
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
                    <select-form :field="batchEdit.editFields[editFieldSelect.value]" :key="index" v-if="batchEdit.editFields[editFieldSelect.value].view == 'selection'" />
                    <date-form :field="batchEdit.editFields[editFieldSelect.value]" :key="index" v-else-if="batchEdit.editFields[editFieldSelect.value].type == 'date'" />
                    <textarea-form :field="batchEdit.editFields[editFieldSelect.value]" :key="index" v-else-if="batchEdit.editFields[editFieldSelect.value].view == 'textarea'" />
                    <icon-form :field="batchEdit.editFields[editFieldSelect.value]" :key="index" v-else-if="batchEdit.editFields[editFieldSelect.value].view == 'icon'" />
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

export default {
    name: 'action-batch-edit',
    components: {
        'text-form': TextForm,
        'textarea-form': TextareaForm,
        'icon-form': IconForm,
        'select-form': SelectForm,
        'date-form': DateForm
    },
    props: [
        "batchEdit", 
        "selects",
        "modal"
    ],
    data: function() {
        return {
            editFieldSelects: []
        }
    },
    methods: {
        save: function() {
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
                    if (field.view === 'selection') {
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
                });
            }
        }
    }
}
</script>