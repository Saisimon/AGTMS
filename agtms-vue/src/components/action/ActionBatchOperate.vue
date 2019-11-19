<template>
    <div v-if="batchOperate">
        <b-modal v-model="modal.show"
            :size="batchOperate.size"
            :hide-footer="true"
            @hidden="initData"
            header-border-variant="light"
            button-size="sm">
            <div class="form-container" v-if="batchOperate.operateFields && batchOperate.operateFields.length > 0">
                <template v-for="(operateField, key) in batchOperate.operateFields" >
                    <select-form :field="operateField" :key="key" v-if="operateField.views == 'selection'" />
                    <date-form :field="operateField" :key="key" v-else-if="operateField.type == 'date'" />
                    <textarea-form :field="operateField" :key="key" v-else-if="operateField.views == 'textarea'" />
                    <icon-form :field="operateField" :key="key" v-else-if="operateField.views == 'icon'" />
                    <image-form :field="operateField" :key="key" v-else-if="operateField.views == 'image'" />
                    <password-form :field="operateField" :key="key" v-else-if="operateField.views == 'password'" />
                    <text-form :field="operateField" :key="key" v-else />
                </template>
            </div>
            <div class="text-center mt-2 mb-2" v-else>
                {{ $t('are_you_confirm') }}
            </div>
            <b-row class="mt-3 mb-2">
                <b-col class="text-right">
                    <b-button variant="outline-primary" 
                        size="sm" 
                        class="save-btn"
                        @click="operate">
                        <i class="fa fa-fw fa-save"></i>
                        {{ $t("confirm") }}
                    </b-button>
                </b-col>
            </b-row>
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
    name: 'action-batch-operate',
    components: {
        'text-form': TextForm,
        'textarea-form': TextareaForm,
        'icon-form': IconForm,
        'select-form': SelectForm,
        'date-form': DateForm,
        'image-form': ImageForm,
        'password-form': PasswordForm
    },
    data: function() {
        return {
            submit: false
        }
    },
    methods: {
        initData: function() {
            this.submit = false;
        },
        operate() {
            if (this.submit) {
                return;
            }
            this.submit = true;
            var data = {
                ids: this.selects
            }
            var pass = true;
            if (this.batchOperate.operateFields && this.batchOperate.operateFields.length > 0) {
                for (var i in this.batchOperate.operateFields) {
                    var operateField = this.batchOperate.operateFields[i];
                    var value = operateField.value;
                    if (operateField.required && this.isNullEmpty(value)) {
                        pass = false;
                        operateField.state = false;
                    } else {
                        operateField.state = null;
                        data[operateField.name] = value;
                    }
                }
            }
            if (pass) {
                this.$store.dispatch('batchOperateData', {
                    url: this.$route.path,
                    path: this.batchOperate.path,
                    data: data
                }).then(resp => {
                    var data = resp.data;
                    if (data.code === 0) {
                        this.modal.show = false;
                        this.$emit('succeed');
                        if (this.$route.params.module === 'navigation' || this.$route.params.module === 'template') {
                            this.$store.dispatch('getTree');
                        }
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
    },
    props: [
        "batchOperate", 
        'modal',
        "selects",
    ]
}
</script>
