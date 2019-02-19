<template>
    <div class="edit-container">
        <b-card header-tag="header" footer-tag="footer">
            <!-- 头部 -->
            <b-row slot="header">
                <b-col>
                    <!-- 标题 -->
                    <div class="card-header-title-container" v-if="$route.query.id == null">
                        {{ $t("create") }}
                    </div>
                    <div class="card-header-title-container" v-else>
                        {{ $t("edit") }}
                    </div>
                </b-col>
            </b-row>
            <!-- 表单 -->
            <div class="form-container">
                <template v-for="(field, key) in fields">
                    <select-form :field="field" :key="key" v-if="field.type == 'select'" />
                    <date-form :field="field" :key="key" v-else-if="field.type == 'date'" />
                    <textarea-form :field="field" :key="key" v-else-if="field.view == 'textarea'" />
                    <icon-form :field="field" :key="key" v-else-if="field.view == 'icon'" />
                    <image-form :field="field" :key="key" v-else-if="field.view == 'image'" />
                    <password-form :field="field" :key="key" v-else-if="field.view == 'password'" />
                    <text-form :field="field" :key="key" v-else />
                </template>
            </div>
            <!-- 尾部 -->
            <b-row slot="footer">
                <b-col class="text-right">
                    <!-- 返回 -->
                    <b-button 
                        :to="backUrl"
                        variant="secondary" 
                        size="sm" 
                        class="ml-2">
                        <i class="fa fa-fw fa-undo"></i>
                        {{ $t("back") }}
                    </b-button>
                    <!-- 重置 -->
                    <b-button 
                        variant="danger" 
                        v-b-modal="'reset-model'"
                        size="sm" 
                        class="ml-2">
                        <i class="fa fa-fw fa-repeat"></i>
                        {{ $t("reset") }}
                    </b-button>
                    <!-- 保存 -->
                    <b-button 
                        variant="primary" 
                        @click="save"
                        size="sm" 
                        class="ml-2">
                        <i class="fa fa-fw fa-save"></i>
                        {{ $t("save") }}
                    </b-button>
                </b-col>
            </b-row>
            <!-- 重置确认 -->
            <b-modal
                id="reset-model"
                centered 
                :cancel-title="$t('cancel')"
                :ok-title="$t('confirm_reset')"
                @ok="reset()"
                ok-variant="danger"
                button-size="sm">
                <div class="text-center font-weight-bold">
                    {{ $t('confirm' )}}
                </div>
            </b-modal>
            <!-- 警告框 -->
            <div class="modal d-block" v-if="alert.dismissCountDown">
                <div class="modal-dialog modal-md modal-dialog-centered">
                    <b-alert :variant="alert.variant"
                        dismissible
                        :show="alert.dismissCountDown"
                        style="pointer-events: auto;"
                        class="w-100"
                        @dismissed="alert.dismissCountDown=0">
                        {{ alert.text }}
                    </b-alert>
                </div>
            </div>
        </b-card>
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
    name: 'edit',
    data: function() {
        return {
            alert: {
                dismissSecs: 3,
                dismissCountDown: 0,
                variant: 'success',
                text: ''
            },
            backUrl: "",
            resetFields: []
        }
    },
    computed: {
        fields: function() {
            return this.$store.state.edit.fields;
        }
    },
    components: {
        'text-form': TextForm,
        'textarea-form': TextareaForm,
        'icon-form': IconForm,
        'select-form': SelectForm,
        'date-form': DateForm,
        'image-form': ImageForm,
        'password-form': PasswordForm
    },
    created: function() {
        if (this.$store.state.base.user !== '') {
            this.$store.dispatch('getEditGrid', {
                url: this.$route.path,
                id: this.$route.query.id
            }).then(resp => {
                var breadcrumbs = resp.data.data.breadcrumbs;
                this.$store.commit('setBreadcrumbs', breadcrumbs);
                if (breadcrumbs && breadcrumbs.length > 0) {
                    for (var i = breadcrumbs.length - 1; i >= 0; i--) {
                        if (breadcrumbs[i].to) {
                            this.backUrl = breadcrumbs[i].to;
                            break;
                        }
                    }
                }
                var fields = resp.data.data.fields;
                this.$store.commit('setFields', fields);
                this.resetFields = this.cloneObject(fields);
            });
        }
        this.$store.commit('clearProgress');
    },
    methods: {
        reset: function() {
            this.$store.commit('setFields', this.cloneObject(this.resetFields));
        },
        save: function() {
            var data = {};
            var pass = true;
            for (var i = 0; i < this.fields.length; i++) {
                var field = this.fields[i];
                if (field.required && this.isNullEmpty(field.value)) {
                    pass = false;
                    field.state = false;
                } else {
                    field.state = null;
                }
                if (field.type === 'select') {
                    data[field.name] = field.value.value;
                } else {
                    data[field.name] = field.value;
                }
            }
            if (pass) {
                if (this.$route.query.id) {
                    data['id'] = this.$route.query.id;
                }
                this.$store.dispatch('saveData', {
                    url: this.$route.path,
                    data: data
                }).then(resp => {
                    var data = resp.data;
                    if (data.code === 0) {
                        this.$store.dispatch('getTree');
                        this.alert.variant = 'success';
                    } else {
                        this.alert.variant = 'danger';
                    }
                    this.alert.text = data.message;
                    this.alert.dismissCountDown = this.alert.dismissSecs;
                });
            }
        }
    }
}
</script>
