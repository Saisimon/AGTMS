<template>
    <vue-good-table
        :columns="columns"
        :rows="rows"
        @on-cell-click="cellClick" >
        <template slot="table-row" slot-scope="props">
            <span v-if="props.row.key == 'sorted' && props.column.field == 'add'">
                <div class="add-editor">
                    <i class="fa fa-plus"></i>
                </div>
            </span>
            <span v-else-if="props.row.editor == 'input' && props.column.field != 'title' && props.column.field != 'add' ">
                <input-editor 
                    :editor="props.formattedRow[props.column.field]"
                    :field="props.column.field" 
                    :rowKey="props.row.key"
                    :class="'editor-text'"
                    @updateInputEditor="updateTableEditor" ></input-editor>
            </span>
            <span v-else-if="props.row.editor == 'select' && props.column.field != 'title' && props.column.field != 'add' ">
                <b-row class="m-0">
                    <b-col class="p-0" >
                        <select-editor 
                            :editor="props.formattedRow[props.column.field]"
                            :field="props.column.field" 
                            :rowKey="props.row.key"
                            :class="'editor-text'"
                            @syncDefaultType="syncDefaultType"
                            @updateSelectEditor="updateTableEditor" ></select-editor>
                    </b-col>
                    <b-col class="p-0" v-if="props.row.key == 'showType' && props.formattedRow[props.column.field].value == 'selection'">
                        <select-editor 
                            :editor="props.row['selection-' + props.column.field]"
                            :field="'selection-' + props.column.field" 
                            :rowKey="props.row.key"
                            :class="'editor-text'"
                            @syncDefaultType="syncDefaultType"
                            @updateSelectEditor="updateTableEditor" ></select-editor>
                    </b-col>
                </b-row>
            </span>
            <span v-else-if="props.row.editor == 'remove' && props.column.field != 'title' && props.column.field != 'add' ">
                <div class="remove-editor">
                    <i class="fa fa-trash"></i>
                </div>
            </span>
            <div v-else class="text-center">
                {{ props.formattedRow[props.column.field] }}
            </div>
        </template>
    </vue-good-table>
</template>

<script>
import InputEditor from '@/components/editor/InputEditor.vue'
import SelectEditor from '@/components/editor/SelectEditor.vue'

export default {
    name: 'table-editor',
    props: {
        table: {
            type: Object,
            default: function () {
                return {
                    columns: [],
                    rows: [],
                    idx: 0
                };
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
    components: {
        'input-editor': InputEditor,
        'select-editor': SelectEditor,
    },
    computed: {
        columns: function() {
            var columns = this.table.columns;
            if (columns) {
                for (var i = 0; i < columns.length; i++) {
                    var column = columns[i];
                    if (column.field == 'add') {
                        column['tdClass'] = 'add-field-td';
                    } else {
                        column['tdClass'] = 'field-td';
                    }
                }
            }
            return columns;
        },
        rows: function() {
            var rows = this.table.rows;
            for (var i = 0; i < rows.length; i++) {
                var row = rows[i];
                if (row.editor != 'select') {
                    continue;
                }
                for (var field in row) {
                    if (field.startsWith('field')) {
                        if (row.key == 'fieldType') {
                            row[field]['options'] = this.$store.state.template.classOptions;
                        } else if (row.key == 'showType') {
                            row[field]['options'] = this.$store.state.template.viewOptions;
                            row['selection-' + field]['options'] = this.$store.state.template.selectionOptions;
                        } else {
                            row[field]['options'] = this.$store.state.template.whetherOptions;
                        }
                    }
                }
            }
            return rows;
        }
    },
    methods: {
        cellClick: function(params) {
            var pcolumn = params.column;
            var prow = params.row;
            if (pcolumn.field == 'add') {
                if (this.columns.length > 10) {
                    return;
                }
                var idx = this.table.idx;
                var fieldKey = "field" + idx;
                var ordered = this.columns.length - 1;
                this.columns.splice(this.columns.length - 1, 0, { field: fieldKey, sortable: false, ordered: ordered});
                for (var a = 0; a < this.rows.length; a++) {
                    var row = this.rows[a];
                    if (row.editor == 'input') {
                        row[fieldKey] = {value: "", className: fieldKey};
                    } else if (row.editor == 'select') {
                        if (row.key == 'fieldType') {
                            var classOptions = this.$store.state.template.classOptions;
                            row[fieldKey] = {value: classOptions[0].id, options: classOptions, className: fieldKey};
                        } else if (row.key == 'showType') {
                            var viewOptions = this.$store.state.template.viewOptions;
                            row[fieldKey] = {value: viewOptions[0].id, options: viewOptions, className: fieldKey};
                            var selectionOptions = this.$store.state.template.selectionOptions;
                            var selectionValue = null;
                            if (selectionOptions.length > 0) {
                                selectionValue = selectionOptions[0].id;
                            }
                            row['selection-' + fieldKey] = {value: selectionValue, options: selectionOptions, className: 'selection-' + fieldKey};
                        } else {
                            var whetherOptions = this.$store.state.template.whetherOptions;
                            row[fieldKey] = {value: whetherOptions[0].id, options: whetherOptions, className: fieldKey};
                        }
                    } else if (row.editor == 'remove') {
                        row[fieldKey] = "";
                    }
                }
                this.rows.push();
                this.table.idx = idx + 1;
                this.table.columns = this.columns;
                this.table.rows = this.rows;
                this.$emit('updateTableEditor', this.table, this.rowKey, this.field);
            } else if (prow.key == 'remove') {
                if (this.columns.length <= 2) {
                    return;
                }
                var columnIdx = 0;
                var key = pcolumn['field'];
                for (;columnIdx < this.columns.length; columnIdx++) {
                    if (this.columns[columnIdx]['field'] == key) {
                        break;
                    }
                }
                for (var b = columnIdx + 1; b < this.columns.length; b++) {
                    this.columns[b]['ordered'] = b - 2;
                }
                this.columns.splice(columnIdx, 1);
                for (var c = 0; c < this.rows.length; c++) {
                    delete this.rows[c][key];
                }
                this.rows.push();
                this.table.columns = this.columns;
                this.table.rows = this.rows;
                this.$emit('updateTableEditor', this.table, this.rowKey, this.field);
            }
            params.event.stopPropagation();
        },
        updateTableEditor: function(editor, rowKey, field) {
            for (var i = 0; i < this.rows.length; i++) {
                var row = this.rows[i];
                if (row.key == rowKey) {
                    row[field] = editor;
                    break;
                }
            }
            this.table.rows = this.rows;
            this.table.columns = this.columns;
            this.$emit('updateTableEditor', this.table, this.rowKey, this.field);
        },
        syncDefaultType: function(defaultValue, field) {
            var defaultRow = this.rows[8];
            if (defaultValue && defaultValue.type) {
                if (defaultValue.type === 'select' && !defaultValue.selectionSign) {
                    var selectionField = this.rows[2]["selection-" + field];
                    if (selectionField && selectionField.value) {
                        defaultValue['selectionSign'] = selectionField.value;
                    }
                }
                if (field.startsWith('selection-')) {
                    defaultRow[field.substring(10)] = defaultValue;
                } else {
                    defaultRow[field] = defaultValue;
                }
            }
            this.table.rows = this.rows;
            this.table.columns = this.columns;
            this.$emit('updateTableEditor', this.table, this.rowKey, this.field);
        }
    }
}
</script>

<style scoped>
.add-editor {
    cursor: pointer;
    text-align: center;
}
.remove-editor {
    cursor: pointer;
    text-align: center;
}
</style>