<template>
    <div :class="editor.type == 'select' ? 'select-editor' : 'input-editor'" @click="show=true">
        <template v-if="editor.type == 'select'">
            <multiselect
                v-if="show"
                @blur.native.capture="show=false" 
                v-model="editor.value"
                label="text"
                track-by="value"
                select-label=""
                deselect-label=""
                selected-label=""
                :allow-empty="false"
                :searchable="false"
                :options="editor.options"
                :placeholder="''"
                @select="updateValue" >
                <template slot="noResult">{{ $t("no_result") }}</template>
                <template slot="noOptions">{{ $t("no_options") }}</template>
            </multiselect>
            <span class="select-editor-text" v-else-if="editor.value != null" >{{ editor.value.text }}</span>
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
            <span class="input-editor-text" v-else-if="editor.value == ''">{{ placeholder }}</span>
            <span class="input-editor-text" v-else >{{ editor.value }}</span>
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
                    value: '',
                    type: 'text'
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