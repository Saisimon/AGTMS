<template>
    <div class="input-editor" :class="editor.className" @click="show=true">
        <b-form-input 
            :class="'border-0 editor-input ' + editor.className + '-input'"
            size="sm" 
            v-model="editor.value" 
            v-if="show" 
            @blur.native.capture="show=false" 
            :placeholder="placeholder" 
            @input="updateValue" />
        <span class="input-editor-text" v-else-if="editor.value == ''">{{ placeholder }}</span>
        <span class="input-editor-text" v-else >{{ editor.value }}</span>
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
                    value: ''
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
        }
    }
}
</script>