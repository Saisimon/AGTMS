<template>
    <div :class="editor.type == 'select' ? 'select-editor' : 'input-editor'" @click="show=true">
        <template v-if="editor.type == 'select'">
            <treeselect 
                v-if="show"
                @blur.native.capture.stop="show=false" 
                v-model="editor.value"
                :options="editor.options"
                :multiple="false" 
                :searchable="true"
                :noChildrenText="$t('no_childrens')"
                :noOptionsText="$t('no_options')"
                :noResultsText="$t('no_result')"
                :placeholder="''"
                :class="editor.className + '-select'"
                @search-change="search"
                @select="updateValue"
                @deselect="removeValue" />
            <span class="select-editor-text" v-else-if="label != null" >{{ label }}</span>
            <span class="select-editor-text" v-else >{{ placeholder }}</span>
        </template>
        <template v-else>
            <b-form-input 
                :class="'border-0 editor-input ' + editor.className + '-input'"
                v-if="show"
                @blur.native.capture="show=false" 
                size="sm" 
                v-model="editor.value" 
                :type="editor.type"
                :placeholder="placeholder" 
                @input="updateValue" />
            <span class="select-editor-text" v-else-if="label != null" >{{ label }}</span>
            <span class="select-editor-text" v-else >{{ placeholder }}</span>
        </template>
    </div>
</template>

<script>
export default {
    name: 'input-editor',
    props: {
        editor: {
            type: Object,
            default: function () {
                return { 
                    show: false,
                    placeholder: this.$t("click_to_edit"),
                    value: null,
                    type: 'text',
                    selectionSign: null,
                    options: []
                }
            }
        },
        field: {
            type: String,
            required: false,
            default: ''
        },
        rowKey: {
            type: String,
            required: false,
            default: ''
        }
    },
    watch: {
        "editor.selectionSign": function(newV, oldV) {
            this.search();
        }
    },
    computed: {
        label: function() {
            if (this.editor.type == 'select') {
                return this.getLabel(this.editor.options, this.editor.value);
            } else {
                if (this.editor.type == 'password') {
                    return '******';
                } else if (this.editor.value != '') {
                    return this.editor.value;
                }
            }
        }
    },
    data: function() {
        var show = false;
        if (this.editor.show != null) {
            show = this.editor.show;
        }
        var placeholder = this.$t("click_to_edit");
        if (this.editor.placeholder != null) {
            placeholder = this.editor.placeholder;
        }
        return {
            show: show,
            placeholder: placeholder
        }
    },
    methods: {
        updateValue: function(val) {
            if (val != null) {
                this.editor.value = val;
                this.$emit('updateInputEditor', this.editor, this.rowKey, this.field);
            }
        },
        removeValue: function(val) {
            if (val != null) {
                this.editor.value = null;
                this.$emit('updateInputEditor', this.editor, this.rowKey, this.field);
            }
        },
        search: function(query) {
            if (this.editor.selectionSign == null) {
                return;
            }
            this.$store.dispatch('searchSelection', {
                sign: this.editor.selectionSign,
                keyword: query
            }).then(resp => {
                if (resp.data.code === 0) {
                    var options = resp.data.data;
                    this.editor.options = options;
                }
            });
        }
    }
}
</script>