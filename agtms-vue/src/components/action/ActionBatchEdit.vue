<template>
    <div class="batch-edit-container" v-if="batchEdit">
        <b-modal v-model="model.show"
            :title="$t('batch_edit')"
            size="lg"
            :hide-footer="true"
            button-size="sm">
            <div class="form-container">
                <b-row class="mb-3">
                    <b-col>
                        <multiselect v-model="editFieldSelects"
                            label="text"
                            track-by="value"
                            select-label=""
                            deselect-label=""
                            selectedLabel=""
                            :searchable="false"
                            :multiple="true"
                            :options="batchEdit.editFieldOptions"
                            :placeholder="$t('select_edit_fields')" />
                    </b-col>
                </b-row>
                <template v-for="(editFieldSelect, index) in editFieldSelects" >
                    <select-form :field="batchEdit.editFields[editFieldSelect.value]" :key="index" v-if="batchEdit.editFields[editFieldSelect.value].type == 'select'" />
                    <date-form :field="batchEdit.editFields[editFieldSelect.value]" :key="index" v-else-if="batchEdit.editFields[editFieldSelect.value].type == 'date'" />
                    <textarea-form :field="batchEdit.editFields[editFieldSelect.value]" :key="index" v-else-if="batchEdit.editFields[editFieldSelect.value].view == 'textarea'" />
                    <icon-form :field="batchEdit.editFields[editFieldSelect.value]" :key="index" v-else-if="batchEdit.editFields[editFieldSelect.value].view == 'icon'" />
                    <text-form :field="batchEdit.editFields[editFieldSelect.value]" :key="index" v-else />
                </template>
                <b-row class="mb-3" v-if="editFieldSelects.length > 0">
                    <b-col class="text-right">
                        <b-button variant="primary" 
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
        "model"
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
                var value = this.batchEdit.editFields[key].value;
                if (this.batchEdit.editFields[key].required && (value == undefined || value == "")) {
                    pass = false;
                    this.batchEdit.editFields[key].state = false;
                } else {
                    this.batchEdit.editFields[key].state = null;
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
                        this.$emit('succeed');
                    }
                });
            }
        }
    }
}
</script>