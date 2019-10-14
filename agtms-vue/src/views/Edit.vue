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
                <template v-for="(group, key) in groups">
                    <b-row :key="key" class="mb-2 pb-2 border-bottom">
                        <b-col>
                            <label :for="'form-group-' + key" class="form-label font-weight-bold form-group-label">
                                {{ group.text }}
                            </label>
                            <div :id="'form-group-' + key">
                                <template v-for="(field, key) in group.fields">
                                    <select-form :field="field" :key="key" v-if="field.views == 'selection'" />
                                    <date-form :field="field" :key="key" v-else-if="field.type == 'date'" />
                                    <textarea-form :field="field" :key="key" v-else-if="field.views == 'textarea'" />
                                    <icon-form :field="field" :key="key" v-else-if="field.views == 'icon'" />
                                    <image-form :field="field" :key="key" v-else-if="field.views == 'image'" />
                                    <password-form :field="field" :key="key" v-else-if="field.views == 'password'" />
                                    <text-form :field="field" :key="key" v-else />
                                </template>
                            </div>
                        </b-col>
                    </b-row>
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
                        class="ml-2 back-btn">
                        <i class="fa fa-fw fa-undo"></i>
                        {{ $t("back") }}
                    </b-button>
                    <!-- 重置 -->
                    <b-button 
                        variant="danger" 
                        v-b-modal="'reset-modal'"
                        size="sm" 
                        class="ml-2 reset-btn">
                        <i class="fa fa-fw fa-repeat"></i>
                        {{ $t("reset") }}
                    </b-button>
                    <!-- 保存 -->
                    <b-button 
                        variant="primary" 
                        @click="save"
                        size="sm" 
                        class="ml-2 save-btn">
                        <i class="fa fa-fw fa-save"></i>
                        {{ $t("save") }}
                    </b-button>
                </b-col>
            </b-row>
            <!-- 重置确认 -->
            <b-modal
                id="reset-modal"
                centered 
                :cancel-title="$t('cancel')"
                :ok-title="$t('confirm_reset')"
                @ok="reset"
                cancel-variant="outline-info"
                ok-variant="outline-danger"
                header-border-variant="light"
                footer-border-variant="light"
                button-size="sm">
                <div class="text-center">
                    {{ $t('are_you_confirm' )}}
                </div>
            </b-modal>
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
            backUrl: "",
            resetGroups: []
        }
    },
    computed: {
        groups: function() {
            return this.$store.state.edit.groups;
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
        this.$store.commit('initState');
        if (this.$store.state.base.user !== '') {
            var vm = this;
            this.$store.dispatch('getEditGrid', {
                url: this.$route.path,
                id: this.$route.query.id
            }).then(resp => {
                if (resp.data.code === 0) {
                    var breadcrumbs = resp.data.data.breadcrumbs;
                    vm.$store.commit('setBreadcrumbs', breadcrumbs);
                    if (breadcrumbs && breadcrumbs.length > 0) {
                        for (var i = breadcrumbs.length - 1; i >= 0; i--) {
                            if (breadcrumbs[i].to) {
                                vm.backUrl = breadcrumbs[i].to;
                                break;
                            }
                        }
                    }
                    var groups = resp.data.data.groups;
                    vm.$store.commit('setGroups', groups);
                    vm.resetGroups = vm.cloneObject(groups);
                }
                vm.$store.commit('clearProgress');
            });
        } else {
            this.$store.commit('clearProgress');
        }
    },
    methods: {
        reset: function() {
            this.$store.commit('setGroups', this.cloneObject(this.resetGroups));
        },
        save: function() {
            var data = {};
            var pass = true;
            for (var i = 0; i < this.groups.length; i++) {
                var group = this.groups[i];
                if (!group.fields) {
                    continue;
                }
                for (var j = 0; j < group.fields.length; j++) {
                    var field = group.fields[j];
                    var value = field.value;
                    if (field.required && this.isNullEmpty(value)) {
                        pass = false;
                        field.state = false;
                    } else {
                        field.state = null;
                    }
                    data[field.name] = value;
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
                        if (this.$route.params.module === 'navigation') {
                            this.$store.dispatch('getTree');
                        }
                        this.$store.commit('showAlert', {
                            message: data.message,
                            variant: 'success'
                        });
                    }
                });
            }
        }
    }
}
</script>
