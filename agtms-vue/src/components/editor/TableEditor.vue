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
                <select-editor 
                    :editor="props.formattedRow[props.column.field]"
                    :field="props.column.field" 
                    :rowKey="props.row.key"
                    :class="'editor-text'"
                    @updateSelectEditor="updateTableEditor" ></select-editor>
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
                var idx = this.table.idx;
                var fieldKey = "field" + idx;
                var ordered = this.columns.length - 1;
                this.columns.splice(this.columns.length - 1, 0, { field: fieldKey, sortable: false, ordered: ordered});
                for (var i = 0; i < this.rows.length; i++) {
                    var row = this.rows[i];
                    if (row.editor == 'input') {
                        row[fieldKey] = {value: ""};
                    } else if (row.editor == 'select') {
                        if (row.key == 'fieldType') {
                            var options = this.$store.state.template.classOptions;
                            row[fieldKey] = {value: options[0], options: options};
                        } else if (row.key == 'showType') {
                            var options = this.$store.state.template.viewOptions;
                            row[fieldKey] = {value: options[0], options: options};
                        } else {
                            var options = this.$store.state.template.whetherOptions;
                            row[fieldKey] = {value: options[0], options: options};
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
                var idx = 0;
                var key = pcolumn['field'];
                for (;idx < this.columns.length; idx++) {
                    if (this.columns[idx]['field'] == key) {
                        break;
                    }
                }
                for (var i = idx + 1; i < this.columns.length; i++) {
                    this.columns[i]['ordered'] = i - 2;
                }
                this.columns.splice(idx, 1);
                for (var i = 0; i < this.rows.length; i++) {
                    var row = this.rows[i];
                    delete row[key];
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

<style>
.add-field-td {
    width: 50px;
    min-width: 50px;
}
</style>
