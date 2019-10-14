<template>
    <div class="select-editor" :class="editor.className" @click="show=true">
        <treeselect 
            v-if="show"
            @blur.native.capture.stop="show=false" 
            v-model="editor.value"
            :options="editor.options"
            :multiple="false" 
            :searchable="false"
            :clearable="false"
            :noChildrenText="$t('no_childrens')"
            :noOptionsText="$t('no_options')"
            :noResultsText="$t('no_result')"
            :placeholder="''"
            @select="updateValue" />
        <span class="select-editor-text" v-else-if="label != null" >{{ label }}</span>
        <span class="select-editor-text" v-else >{{ placeholder }}</span>
    </div>
</template>

<script>
export default {
    name: "select-editor",
    props: {
        editor: {
            type: Object,
            default: function () {
                return { 
                    show: false,
                    value: '',
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
    computed: {
        label: function() {
            return this.getLabel(this.editor.options, this.editor.value);
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
            if (!val) {
                return;
            }
            this.editor.value = val.id;
            this.$emit('updateSelectEditor', this.editor, this.rowKey, this.field);
            this.updateDefaultType(val);
        },
        updateDefaultType: function(val) {
            if (this.rowKey !== 'fieldType' && this.rowKey !== 'showType') {
                return;
            }
            var defaultValue = Object.create({
                className: this.field,
                selectionSign: null,
                options: [],
                value: null
            });
            if (this.field.startsWith('selection-')) {
                defaultValue['type'] = "select";
                defaultValue['selectionSign'] = val.id;
                this.$emit('syncDefaultType', defaultValue, this.field);
            } else if (val.id === 'long' || val.id === 'double') {
                defaultValue['type'] = "number";
                this.$emit('syncDefaultType', defaultValue, this.field);
            } else if (val.id === 'date') {
                defaultValue['type'] = "date";
                this.$emit('syncDefaultType', defaultValue, this.field);
            } else if (val.id === 'password') {
                defaultValue['type'] = "password";
                this.$emit('syncDefaultType', defaultValue, this.field);
            } else if (val.id === 'link') {
                defaultValue['type'] = "url";
                this.$emit('syncDefaultType', defaultValue, this.field);
            } else if (val.id === 'email') {
                defaultValue['type'] = "email";
                this.$emit('syncDefaultType', defaultValue, this.field);
            } else if (val.id === 'selection') {
                defaultValue['type'] = "select";
                this.$emit('syncDefaultType', defaultValue, this.field);
            } else {
                defaultValue['type'] = "text";
                this.$emit('syncDefaultType', defaultValue, this.field);
            }
        }
    }
}
</script>

<style>
.select-editor .vue-treeselect__control {
    height: 30px;
    line-height: 30px;
    border-collapse: separate;
    border-radius: 0.2rem;
    border-color: #e8e8e8;
}
.select-editor .vue-treeselect__value-container {
    position: inherit;
}
</style>