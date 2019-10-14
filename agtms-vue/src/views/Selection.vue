<template>
    <div class="selection-container">
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
                <text-form :field="selectionGrid.title" v-if="selectionGrid.title" />
                <select-form :field="selectionGrid.type" v-if="selectionGrid.type" />
                <div v-if="selectionGrid.type" class="border p-3">
                    <!-- option -->
                    <div v-if="selectionGrid.type.value === 0">
                        <b-row class="pl-4 pr-4">
                            <b-col>
                                <label class="form-label">
                                    {{ $t('option_value') }}
                                    <span class="text-danger">*</span>
                                    <i class="fa fa-fw fa-question-circle" v-b-tooltip :title="$t('options_value_desc')"></i>
                                </label>
                            </b-col>
                            <b-col>
                                <label class="form-label">
                                    {{ $t('option_text') }}
                                    <span class="text-danger">*</span>
                                    <i class="fa fa-fw fa-question-circle" v-b-tooltip :title="$t('options_text_desc')"></i>
                                </label>
                            </b-col>
                        </b-row>
                        <draggable v-model="selectionGrid.options" element='div'>
                            <transition-group v-for="(option, key) in selectionGrid.options" :key="key">
                                <b-row class="mb-2 pl-4 pr-4" :key="key">
                                    <div class="draggable-option-div">
                                        <i class="fa fa-list"></i>
                                    </div>
                                    <b-col>
                                        <b-form-input class="border-top-0 border-right-0 border-left-0 rounded-0" :id="'optionValue-' + key + '-input'" v-model.trim="option.value.id" :state="option.value.state" />
                                        <b-form-invalid-feedback :state="option.value.state">
                                            {{ $t('please_input_valid') }}{{ $t('option_value') }}
                                        </b-form-invalid-feedback>
                                    </b-col>
                                    <b-col>
                                        <b-form-input class="border-top-0 border-right-0 border-left-0 rounded-0" :id="'optionText-' + key + '-input'" v-model.trim="option.text.id" :state="option.text.state" />
                                        <b-form-invalid-feedback :state="option.text.state">
                                            {{ $t('please_input_valid') }}{{ $t('option_value') }}
                                        </b-form-invalid-feedback>
                                    </b-col>
                                    <div class="remove-option-div" @click="removeOption(key)">
                                        <i class="fa fa-close"></i>
                                    </div>
                                </b-row>
                            </transition-group>
                        </draggable>
                        <b-row >
                            <b-col class="add-option-div text-center" @click="addOption">
                                <i class="fa fa-plus"></i>
                            </b-col>
                        </b-row>
                    </div>
                    <!-- template -->
                    <div v-else>
                        <b-row class="mb-2">
                            <b-col>
                                <treeselect 
                                    v-model="selectionGrid.template.template.value"
                                    :options="selectionGrid.template.template.options"
                                    :multiple="false" 
                                    :searchable="false"
                                    :placeholder="''"
                                    :noChildrenText="$t('no_childrens')"
                                    :noOptionsText="$t('no_options')"
                                    :noResultsText="$t('no_result')"
                                    @select="templateChange" />
                            </b-col>
                        </b-row>
                        <b-row>
                            <b-col>
                                <label class="form-label">
                                    {{ $t('option_value') }}
                                    <span class="text-danger">*</span>
                                    <i class="fa fa-fw fa-question-circle" v-b-tooltip :title="$t('template_value_desc')"></i>
                                </label>
                            </b-col>
                            <b-col>
                                <label class="form-label">
                                    {{ $t('option_text') }}
                                    <span class="text-danger">*</span>
                                    <i class="fa fa-fw fa-question-circle" v-b-tooltip :title="$t('template_text_desc')"></i>
                                </label>
                            </b-col>
                        </b-row>
                        <b-row class="mb-2">
                            <b-col>
                                <treeselect 
                                    v-model="selectionGrid.template.value.value"
                                    :options="selectionGrid.template.value.options"
                                    :multiple="false" 
                                    :searchable="false"
                                    :noChildrenText="$t('no_childrens')"
                                    :noOptionsText="$t('no_options')"
                                    :noResultsText="$t('no_result')"
                                    :placeholder="''" />
                                <b-form-invalid-feedback :class="{'d-block': selectionGrid.template.value.state == false}">
                                    {{ $t('please_select_valid') }}{{ $t('option_value') }}
                                </b-form-invalid-feedback>
                            </b-col>
                            <b-col>
                                <treeselect 
                                    v-model="selectionGrid.template.text.value"
                                    :options="selectionGrid.template.text.options"
                                    :multiple="false" 
                                    :searchable="false"
                                    :noChildrenText="$t('no_childrens')"
                                    :noOptionsText="$t('no_options')"
                                    :noResultsText="$t('no_result')"
                                    :placeholder="''" />
                                <b-form-invalid-feedback :class="{'d-block': selectionGrid.template.text.state == false}">
                                    {{ $t('please_select_valid') }}{{ $t('option_text') }}
                                </b-form-invalid-feedback>
                            </b-col>
                        </b-row>
                    </div>
                </div>
            </div>
            <!-- 尾部 -->
            <b-row slot="footer">
                <b-col class="text-right">
                    <!-- 返回 -->
                    <b-button 
                        :to="'/selection/main'"
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
                        @click="saveSelection"
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
import draggable from 'vuedraggable'
import TextForm from '@/components/form/TextForm.vue'
import SelectForm from '@/components/form/SelectForm.vue'

export default {
    name: 'selection-edit',
    created: function() {
        if (this.$store.state.base.user != null) {
            var vm = this;
            this.$store.dispatch('getSelectionGrid', this.$route.query.id).then(resp => {
                if (resp.data.code === 0) {
                    var selectionGrid = resp.data.data;
                    if (selectionGrid && selectionGrid.template) {
                        var valueOptions = selectionGrid.template.value.options;
                        if (valueOptions && valueOptions.length > 0) {
                            for (var a = 0; a < valueOptions.length; a++) {
                                var valueOption = valueOptions[a];
                                if (valueOption.disable) {
                                    valueOption["$isDisabled"] = true;
                                }
                            }
                        }
                        var textOptions = selectionGrid.template.text.options;
                        if (textOptions && textOptions.length > 0) {
                            for (var b = 0; b < textOptions.length; b++) {
                                var textOption = textOptions[b];
                                if (textOption.disable) {
                                    textOption["$isDisabled"] = true;
                                }
                            }
                        }
                    }
                    vm.$store.commit('setSelectionGrid', selectionGrid);
                    vm.$store.commit('setBreadcrumbs', selectionGrid.breadcrumbs);
                    vm.resetSelectionGrid = vm.cloneObject(selectionGrid);
                }
                vm.$store.commit('clearProgress');
            });
        } else {
            this.$store.commit('clearProgress');
        }
    },
    computed: {
        selectionGrid: function() {
            return this.$store.state.selection.selectionGrid;
        }
    },
    components: {
        'draggable': draggable,
        'text-form': TextForm,
        'select-form': SelectForm
    },
    data: function() {
        return {
            resetSelectionGrid: {},
            templateMap: {},
        }
    },
    methods: {
        reset: function() {
            this.$store.commit('setSelectionGrid', this.cloneObject(this.resetSelectionGrid));
        },
        templateChange: function(selectedOption) {
            if (selectedOption.id < 0) {
                this.selectionGrid.template.value.options = [];
                this.selectionGrid.template.value.id = null;
                this.selectionGrid.template.text.options = [];
                this.selectionGrid.template.text.id = null;
                return;
            }
            var data = this.templateMap[selectedOption.id];
            if (data) {
                this.selectionGrid.template.value.options = data;
                this.selectionGrid.template.value.id = data[0];
                this.selectionGrid.template.text.options = data;
                this.selectionGrid.template.text.id = data[0];
            } else {
                this.$store.dispatch('getSelectionTemplate', selectedOption.id).then(resp => {
                    if (resp.data.code === 0) {
                        var options = resp.data.data;
                        if (options && options.length > 0) {
                            for (var i = 0; i < options.length; i++) {
                                var option = options[i];
                                if (option.disable) {
                                    option["$isDisabled"] = true;
                                }
                            }
                        }
                        this.selectionGrid.template.value.options = options;
                        this.selectionGrid.template.value.id = options[0];
                        this.selectionGrid.template.text.options = options;
                        this.selectionGrid.template.text.id = options[0];
                        this.templateMap[selectedOption.id] = options;
                    }
                });
            }
        },
        addOption: function() {
            var option = {
                value: {
                    value: null,
                    state: null
                },
                text: {
                    value: null,
                    state: null
                }
            };
            this.selectionGrid.options.push(option);
        },
        removeOption: function(key) {
            if (this.selectionGrid.options.length < 2) {
                return;
            }
            this.selectionGrid.options.splice(key, 1);
        },
        toSelection: function() {
            var pass = true;
            var selection = {};
            var title = this.selectionGrid.title.value;
            if (title == null || title == '') {
                this.selectionGrid.title.state = false;
                pass = false;
            }
            selection['title'] = title;
            var type = this.selectionGrid.type.value;
            selection['type'] = type;
            var value;
            var text;
            if (type === 0) {
                var options = [];
                var values = [];
                var texts = [];
                for (var i = 0; i < this.selectionGrid.options.length; i++) {
                    var option = this.selectionGrid.options[i];
                    value = option.value.id;
                    if (value == null || value == '') {
                        pass = false;
                        option.value.state = false;
                    } else if (values.includes(value)) {
                        pass = false;
                        option.value.state = false;
                    } else {
                        values.push(value);
                        option.value.state = null;
                    }
                    text = option.text.id;
                    if (text == null || text == '') {
                        pass = false;
                        option.text.state = false;
                    } else if (texts.includes(text)) {
                        pass = false;
                        option.text.state = false;
                    } else {
                        texts.push(text);
                        option.text.state = null;
                    }
                    options.push({
                        'value': value,
                        'text': text
                    });
                }
                selection['options'] = options;
            } else {
                var template = {};
                var templateId = this.selectionGrid.template.template.value.id;
                if (!templateId) {
                    pass = false;
                    this.selectionGrid.template.template.state = false;
                } else {
                    template['id'] = templateId;
                    value = this.selectionGrid.template.value.id;
                    if (value == null || value.id == null) {
                        pass = false;
                        this.selectionGrid.template.value.state = false;
                    }
                    if (value != null) {
                        template['value'] = value.id;
                    }
                    text = this.selectionGrid.template.text.id;
                    if (text == null || text.id == null) {
                        pass = false;
                        this.selectionGrid.template.text.state = false;
                    }
                    if (text != null) {
                        template['text'] = text.id;
                    }
                }
                selection['template'] = template;
            }
            if (pass) {
                return selection;
            }
            return false;
        },
        saveSelection: function() {
            var selection = this.toSelection();
            if (!selection) {
                return;
            }
            if (this.$route.query.id) {
                selection['id'] = this.$route.query.id;
            }
            this.$store.dispatch('saveSelection', selection).then(resp => {
                var data = resp.data;
                if (data.code === 0) {
                    this.$store.commit('showAlert', {
                        message: data.message,
                        variant: 'success'
                    });
                }
            });
        }
    }
}
</script>

<style scoped>
.remove-option-div, .add-option-div {
    height: 38px;
    line-height: 38px;
    cursor: pointer;
}
.draggable-option-div {
    height: 38px;
    line-height: 38px;
    cursor: move;
}
</style>
