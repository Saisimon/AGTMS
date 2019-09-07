<template>
    <div class="select-editor" :class="editor.className" @click="show=true">
        <multiselect v-if="show" 
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
            this.editor.value = val;
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
                defaultValue['selectionSign'] = val.value;
                this.$emit('syncDefaultType', defaultValue, this.field);
            } else if (val.value === 'long' || val.value === 'double') {
                defaultValue['type'] = "number";
                this.$emit('syncDefaultType', defaultValue, this.field);
            } else if (val.value === 'date') {
                defaultValue['type'] = "date";
                this.$emit('syncDefaultType', defaultValue, this.field);
            } else if (val.value === 'password') {
                defaultValue['type'] = "password";
                this.$emit('syncDefaultType', defaultValue, this.field);
            } else if (val.value === 'link') {
                defaultValue['type'] = "url";
                this.$emit('syncDefaultType', defaultValue, this.field);
            } else if (val.value === 'email') {
                defaultValue['type'] = "email";
                this.$emit('syncDefaultType', defaultValue, this.field);
            } else if (val.value === 'selection') {
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
