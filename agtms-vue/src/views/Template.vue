<template>
    <div class="template-container">
        <b-card header-tag="header" footer-tag="footer">
            <!-- 头部 -->
            <b-row slot="header">
                <b-col sm="3">
                    <input-editor :editor="templateGrid.title" @updateInputEditor="updateTitle" :class="'editor-text'"></input-editor>
                </b-col>
                <b-col sm="9" class="text-right">
                    <b-form-checkbox v-model="draggable" class="draggable-switch">
                        {{ $t("draggable_mode") }}
                    </b-form-checkbox>
                    <b-button variant="primary" size="sm" @click="addColumn" :disabled="draggable">
                        <i class="fa fa-fw fa-plus-circle"></i>
                        {{ $t("add_column") }}
                    </b-button>
                </b-col>
            </b-row>
            <div class="draggable-container draggable-table mb-3" v-if="draggable">
                <draggable v-model="templateColumns" element='div' :move="draggableMove">
                    <transition-group :class="draggableClass" tag="div">
                        <div class="draggable-column" v-for="column in templateColumns" :key="column.id" :class="column.class">
                            <div class="draggable-field" v-for="field in column.fields" :key="field.id">
                                <div v-if="field.type == 'table'" class="draggable-table">
                                    <draggable v-model="field.columns" :move="draggableMove">
                                        <transition-group class="d-flex" tag="div">
                                            <div class="draggable-column" v-for="column in field.columns" :key="column.id" :class="column.class">
                                                <div class="draggable-field" v-for="field in column.fields" :key="field.id">
                                                    <div v-if="field.type == 'select'" v-html="field.value.text"></div>
                                                    <div v-else>
                                                        <span>{{ field.value }}</span>
                                                    </div>
                                                </div>
                                            </div>
                                        </transition-group>
                                    </draggable>
                                </div>
                                <div v-else-if="field.columns != null" class="draggable-table">
                                    <div class="draggable-column" v-for="column in field.columns" :key="column.id" :class="column.class">
                                        <div class="draggable-field" v-for="field in column.fields" :key="field.id">
                                            <div>
                                                <span>{{ field.value }}</span>
                                                <span class="text-danger" v-if="field.required">*</span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div v-else>
                                    <span>{{ field.value }}</span>
                                    <span class="text-danger" v-if="field.required">*</span>
                                </div>
                            </div>
                        </div>
                    </transition-group>
                </draggable>
            </div>
            <!-- 表单 -->
            <div class="form-container mb-3" v-else>
                <vue-good-table
                    id="templateTable"
                    :columns="columns"
                    :rows="rows"
                    @on-cell-click="cellClick" >
                    <template slot="table-row" slot-scope="props">
                        <span v-if="props.row.key == 'field' && props.column.field == 'title'">
                            <vue-good-table
                                :columns="fieldTitleColumns"
                                :rows="fieldTitleRows" >
                                <template slot="table-row" slot-scope="props">
                                    <div class="text-center">
                                        <span>{{ props.formattedRow['title'] }}</span>
                                        <span class="text-danger" v-if="props.row.required">*</span>
                                    </div>
                                </template>
                            </vue-good-table>
                        </span>
                        <span v-else-if="props.row.editor == 'input' && props.column.field != 'title'">
                            <input-editor 
                                :editor="props.formattedRow[props.column.field]" 
                                :field="props.column.field" 
                                :rowKey="props.row.key"
                                :class="'editor-text'"
                                @updateInputEditor="updateEditor"></input-editor>
                        </span>
                        <span v-else-if="props.row.editor == 'table' && props.column.field != 'title'">
                            <table-editor 
                                :table="props.formattedRow[props.column.field]"
                                :field="props.column.field" 
                                :rowKey="props.row.key"
                                @updateTableEditor="updateEditor"></table-editor>
                        </span>
                        <span v-else-if="props.row.editor == 'remove' && props.column.field != 'title'">
                            <div class="remove-editor">
                                <i class="fa fa-trash"></i>
                            </div>
                        </span>
                        <div v-else class="text-center">
                            <span>{{props.formattedRow[props.column.field]}}</span>
                            <span class="text-danger" v-if="props.row.required">*</span>
                        </div>
                    </template>
                </vue-good-table>
            </div>
            <select-form :field="navigationField"></select-form>
            <select-form :field="functionField"></select-form>
            <!-- 尾部 -->
            <b-row slot="footer">
                <b-col class="text-right">
                    <!-- 返回 -->
                    <b-button 
                        :to="'/template/main'"
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
                    <!-- 预览 -->
                    <b-button 
                        variant="info" 
                        v-b-modal="'preview-model'"
                        @click="toExample"
                        size="sm" 
                        class="ml-2">
                        <i class="fa fa-fw fa-eye"></i>
                        {{ $t("preview") }}
                    </b-button>
                    <!-- 保存 -->
                    <b-button 
                        variant="primary" 
                        @click="saveTemplate"
                        size="sm" 
                        class="ml-2">
                        <i class="fa fa-fw fa-save"></i>
                        {{ $t("save") }}
                    </b-button>
                </b-col>
            </b-row>
            <!-- 重置确认 -->
            <b-modal id="reset-model"
                centered 
                :cancel-title="$t('cancel')"
                :ok-title="$t('confirm_reset')"
                @ok="reset()"
                ok-variant="danger"
                button-size="sm">
                <div class="text-center font-weight-bold">
                    {{ $t('are_you_confirm' )}}
                </div>
            </b-modal>
            <b-modal id="preview-model"
                centered
                size="lg" 
                hide-footer 
                hide-header >
                <vue-good-table
                    :columns="exampleColumns"
                    :rows="exampleRows">
                    <template slot="table-row" slot-scope="props">
                        <span v-if="props.column.field == 'action'">
                            <action-cell 
                                :rowData="props.row" 
                                :field="props.column.field" 
                                :index="props.row.originalIndex" 
                                :actions="actions" 
                                @succeed="searchByFilters" />
                        </span>
                        <span v-else-if="props.column.view == 'icon'">
                            <icon-cell 
                                :rowData="props.formattedRow" 
                                :field="props.column.field" 
                                :index="props.row.originalIndex"  />
                        </span>
                        <span v-else-if="props.column.view == 'link'">
                            <link-cell 
                                :rowData="props.formattedRow" 
                                :field="props.column.field" 
                                :index="props.row.originalIndex"  />
                        </span>
                        <span v-else-if="props.column.view == 'image'">
                            <image-cell 
                                :rowData="props.formattedRow" 
                                :field="props.column.field" 
                                :index="props.row.originalIndex"  />
                        </span>
                        <span v-else-if="props.column.view == 'html'">
                            <html-cell 
                                :rowData="props.formattedRow" 
                                :field="props.column.field" 
                                :index="props.row.originalIndex"  />
                        </span>
                        <span v-else>
                            <text-cell 
                                :rowData="props.formattedRow" 
                                :field="props.column.field" 
                                :index="props.row.originalIndex" />
                        </span>
                    </template>
                </vue-good-table>
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
import draggable from 'vuedraggable'
import InputEditor from '@/components/editor/InputEditor.vue'
import SelectEditor from '@/components/editor/SelectEditor.vue'
import TableEditor from '@/components/editor/TableEditor.vue'
import SelectForm from '@/components/form/SelectForm.vue'
import ActionCell from '@/components/cell/ActionCell.vue'
import TextCell from '@/components/cell/TextCell.vue'
import IconCell from '@/components/cell/IconCell.vue'
import ImageCell from '@/components/cell/ImageCell.vue'
import LinkCell from '@/components/cell/LinkCell.vue'
import HtmlCell from '@/components/cell/HtmlCell.vue'

export default {
    name: 'template-edit',
    created: function() {
        if (this.$store.state.base.user != null) {
            this.$store.dispatch('getTemplateGrid', this.$route.query.id).then(resp => {
                var templateGrid = resp.data.data;
                this.$store.commit('setTemplateGrid', templateGrid);
                this.$store.commit('setBreadcrumbs', templateGrid.breadcrumbs);
                this.$store.commit('setClassOptions', templateGrid.classOptions);
                this.$store.commit('setViewOptions', templateGrid.viewOptions);
                this.$store.commit('setWhetherOptions', templateGrid.whetherOptions);
                this.resetTemplateGrid = this.cloneObject(templateGrid);
                this.$store.commit('clearProgress');
            });
        }
    },
    components: {
        'draggable': draggable,
        'input-editor': InputEditor,
        'select-editor': SelectEditor,
        'table-editor': TableEditor,
        'select-form': SelectForm,
        'action-cell': ActionCell,
        'icon-cell': IconCell,
        'link-cell': LinkCell,
        'image-cell': ImageCell,
        'html-cell': HtmlCell,
        'text-cell': TextCell
    },
    computed: {
        templateGrid: function() {
            var templateGrid = this.$store.state.template.templateGrid;
            var functionSelect = templateGrid.functionSelect;
            if (functionSelect) {
                this.functionField['options'] = functionSelect.options;
                this.functionField['value'] = functionSelect.selected;
            }
            var navigationSelect = templateGrid.navigationSelect;
            if (navigationSelect) {
                this.navigationField['options'] = navigationSelect.options;
                this.navigationField['value'] = navigationSelect.selected;
                var nid = this.$route.query.nid;
                if (nid && nid > 0) {
                    for (var i = 0; i < navigationSelect.options.length; i++) {
                        var option = navigationSelect.options[i];
                        if (option.value == nid) {
                            this.navigationField['value'] = option;
                            break;
                        }
                    }
                }
            }
            return templateGrid;
        },
        columns: function() {
            var columns = this.templateGrid.table.columns;
            if (columns) {
                for (var i = 0; i < columns.length; i++) {
                    var column = columns[i];
                    if (column.field == 'title') {
                        column['tdClass'] = 'field-title-td';
                    } else {
                        column['tdClass'] = 'field-td';
                    }
                }
            }
            return columns;
        },
        rows: function() {
            return this.templateGrid.table.rows;
        }
    },
    data: function() {
        return {
            exampleColumns: [],
            exampleRows: [],
            alert: {
                dismissSecs: 3,
                dismissCountDown: 0,
                variant: 'success',
                text: ''
            },
            fieldTitleColumns: [
                { field: 'title', label: '', width: 100, sortable:false }
            ],
            fieldTitleRows: [
                { 'title': this.$t('field_name'), required: true },
                { 'title': this.$t('field_type') },
                { 'title': this.$t('show_type') },
                { 'title': this.$t('filter') },
                { 'title': this.$t('sorted') },
                { 'title': this.$t('required') },
                { 'title': this.$t('uniqued') },
                { 'title': this.$t('hidden') },
                { 'title': this.$t('default') },
                { 'title': this.$t('width') },
                { 'title': '-' }
            ],
            resetTemplateGrid: {},
            draggable: false,
            draggableClass: 'd-flex',
            draggableTitleColumn: {
                id: 'title', 
                fields: [
                    {id: 0, value: this.$t('column_name'), required: true}, 
                    {id: 1, columns: [
                        {
                            id: 'title', 
                            fields: [
                                {id: 0, value: this.$t('field_name'), required: true}, 
                                {id: 1, value: this.$t('field_type')}, 
                                {id: 2, value: this.$t('show_type')}, 
                                {id: 3, value: this.$t('filter')}, 
                                {id: 4, value: this.$t('sorted')}, 
                                {id: 5, value: this.$t('required')}, 
                                {id: 6, value: this.$t('uniqued')}, 
                                {id: 7, value: this.$t('hidden')}, 
                                {id: 8, value: this.$t('default')}, 
                                {id: 9, value: this.$t('width')}, 
                                {id: 10, value: '-'}
                            ], 
                            class: 'text-center w-100'
                        }
                    ]}, 
                    {id: 2, value: '-'}
                ], 
                class: 'field-title-td text-center', 
                fixed: true
            },
            draggableAddFieldColumn: {
                id: 'add',
                fields: [
                    {id: 0, value: ' '},
                    {id: 1, value: ' '},
                    {id: 2, value: ' '},
                    {id: 3, value: ' '},
                    {id: 4, value: ' '},
                    {id: 5, value: ' '},
                    {id: 6, value: ' '},
                    {id: 7, value: ' '},
                    {id: 8, value: ' '},
                    {id: 9, value: ' '},
                    {id: 10, value: ' '},
                ], 
                class: 'add-field-td text-center',
                fixed: true
            },
            templateColumns: [],
            functionField: {
                name: 'functions',
                text: this.$t('function'),
                multiple: true,
                options: [],
                value: []
            },
            navigationField: {
                name: 'navigationId',
                text: this.$t('navigation'),
                required: true,
                options: [],
                value: {}
            }
        }
    },
    methods: {
        reset: function() {
            this.$store.commit('setTemplateGrid', this.cloneObject(this.resetTemplateGrid));
        },
        addColumn: function() {
            if (this.columns.length > 10) {
                return;
            }
            var idx = this.templateGrid.table.idx;
            var columnKey = 'column' + idx;
            var ordered = this.columns.length - 1;
            this.columns.push({
                field: columnKey,
                sortable: false,
                width: null,
                ordered: ordered
            });
            for (var i = 0; i < this.rows.length; i++) {
                var row = this.rows[i];
                if (row.key == 'columnName') {
                    row[columnKey] = {
                        value: ''
                    }
                } else if (row.key == 'field') {
                    row[columnKey] = {
                        idx: 1,
                        columns: [
                            { field: "field0", sortable: false, tdClass: 'field-td', ordered: 0},
                            { field: "add", sortable: false, width: 50, tdClass: 'add-field-td' }
                        ],
                        rows: [
                            { editor: "input", field0: {value: ""}, key: "fieldName" },
                            { editor: "select", field0: {value: this.templateGrid.classOptions[0], options: this.templateGrid.classOptions}, key: "fieldType" },
                            { editor: "select", field0: {value: this.templateGrid.viewOptions[0], options: this.templateGrid.viewOptions}, key: "showType" },
                            { editor: "select", field0: {value: this.templateGrid.whetherOptions[0], options: this.templateGrid.whetherOptions}, key: "filter" },
                            { editor: "select", field0: {value: this.templateGrid.whetherOptions[0], options: this.templateGrid.whetherOptions}, key: "sorted" },
                            { editor: "select", field0: {value: this.templateGrid.whetherOptions[0], options: this.templateGrid.whetherOptions}, key: "required" },
                            { editor: "select", field0: {value: this.templateGrid.whetherOptions[0], options: this.templateGrid.whetherOptions}, key: "uniqued" },
                            { editor: "select", field0: {value: this.templateGrid.whetherOptions[0], options: this.templateGrid.whetherOptions}, key: "hidden" },
                            { editor: "input", field0: {value: ""}, key: "default" },
                            { editor: "input", field0: {value: ""}, key: "width" },
                            { editor: "remove", field0: "", key: "remove" }
                        ]
                    }
                } else {
                    row[columnKey] = '';
                }
            }
            this.rows.push();
            this.templateGrid.table.idx = idx + 1;
        },
        cellClick: function(params) {
            var column = params.column;
            var row = params.row;
            if (row.key == 'remove') {
                if (this.columns.length <= 2) {
                    return;
                }
                var idx = 0;
                var key = column['field'];
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
            }
        },
        updateEditor: function(editor, rowKey, field) {
            for (var i = 0; i < this.rows.length; i++) {
                var row = this.rows[i];
                if (row.key == rowKey) {
                    row[field] = editor;
                    break;
                }
            }
            this.rows.push();
        },
        updateTitle: function(editor) {
            this.templateGrid.title = editor;
        },
        draggableMove: function({ relatedContext, draggedContext }) {
            const relatedElement = relatedContext.element;
            const draggedElement = draggedContext.element;
            return ((!relatedElement || !relatedElement.fixed) && !draggedElement.fixed);
        },
        toExample: function() {
            var exampleColumns = [];
            var exampleRows = [];
            var exampleRow = {};
            var tableRow = this.rows[1];
            for (var i = 1; i < this.columns.length; i++) {
                var column = this.columns[i];
                var fieldTable = tableRow[column.field];
                var fieldColumns = fieldTable.columns;
                var fieldRows = fieldTable.rows;
                for (var j = 0; j < fieldColumns.length - 1; j++) {
                    var fieldColumn = fieldColumns[j];
                    var fieldName = column.field + fieldColumn.field
                    var fieldTitle = fieldRows[0][fieldColumn.field].value;
                    var view = fieldRows[2][fieldColumn.field].value.value;
                    var fieldType = fieldRows[1][fieldColumn.field].value.value;
                    var exampleColumn = {
                        field: fieldName,
                        label: fieldTitle,
                        view: view,
                        sortable: false
                    };
                    var exampleValue = '';
                    if (fieldType == 'date') {
                        exampleValue = '1970-01-01';
                    } else if (fieldType == 'long') {
                        exampleValue = 999
                    } else if (fieldType == 'double') {
                        exampleValue = 999.99
                    } else if (view == 'icon') {
                        exampleValue = 'list'
                    } else if (view == 'link') {
                        exampleValue = 'https://www.google.com'
                    } else if (view == 'image') {
                        exampleValue = '/images/preview.jpg'
                    } else {
                        exampleValue = this.$t('preview');
                    }
                    exampleRow[fieldName] = exampleValue;
                    exampleColumns.push(exampleColumn);
                }
            }
            exampleRows[0] = exampleRow;
            this.exampleColumns = exampleColumns;
            this.exampleRows = exampleRows;
        },
        toTemplate: function() {
            var template = {};
            var title = this.templateGrid.title.value;
            if (title == '') {
                return false;
            }
            template['title'] = title;
            if (this.columns.length < 2) {
                return false;
            }
            template['navigationId'] = this.navigationField.value.value;
            var functions = this.functionField.value;
            var func = new Number(0);
            for (var i = 0; i < functions.length; i++) {
                func += new Number(functions[i].value);
            }
            template['function'] = func;
            template['columnIndex'] = this.templateGrid.table.idx;
            var templateColumns = new Array(this.columns.length - 1);
            var columnNameRow = this.rows[0];
            var tableRow = this.rows[1];
            for (var i = 1; i < this.columns.length; i++) {
                var column = this.columns[i];
                var templateColumn = {
                    columnName: column.field,
                    title: columnNameRow[column.field].value,
                    ordered: column.ordered
                };
                var fieldTable = tableRow[column.field];
                var fieldColumns = fieldTable.columns;
                var fieldRows = fieldTable.rows;
                var templateFields = new Array(fieldColumns.length - 1);
                for (var j = 0; j < fieldColumns.length - 1; j++) {
                    var fieldColumn = fieldColumns[j];
                    var templateField = {
                        fieldName: fieldColumn.field,
                        fieldTitle: fieldRows[0][fieldColumn.field].value,
                        fieldType: fieldRows[1][fieldColumn.field].value.value,
                        view: fieldRows[2][fieldColumn.field].value.value,
                        filter: fieldRows[3][fieldColumn.field].value.value == '1' ? true : false,
                        sorted: fieldRows[4][fieldColumn.field].value.value == '1' ? true : false,
                        required: fieldRows[5][fieldColumn.field].value.value == '1' ? true : false,
                        uniqued: fieldRows[6][fieldColumn.field].value.value == '1' ? true : false,
                        hidden: fieldRows[7][fieldColumn.field].value.value == '1' ? true : false,
                        defaultValue: fieldRows[8][fieldColumn.field].value,
                        width: fieldRows[9][fieldColumn.field].value,
                        ordered: fieldColumn.ordered,
                    };
                    templateFields[j] = templateField;
                }
                templateColumn['fields'] = templateFields;
                templateColumn['fieldIndex'] = fieldTable.idx;
                templateColumns[i - 1] = templateColumn;
            }
            template['columns'] = templateColumns;
            return template;
        },
        toTable: function() {
            var columns = new Array(this.columns.length);
            columns[0] = this.columns[0];
            var row = this.rows[1];
            for (var i = 1; i < this.templateColumns.length; i++) {
                var templateColumn = this.templateColumns[i];
                var subtemplateColumns = templateColumn['fields'][1]['columns'];
                var subColumns = row[templateColumn.id]['columns'];
                var newSubColumns = new Array(subColumns.length);
                for (var j = 0; j < subtemplateColumns.length - 1; j++) {
                    var subtemplateColumn = subtemplateColumns[j];
                    var subColumn = subColumns[subtemplateColumn.ordered];
                    subColumn.ordered = j;
                    newSubColumns[j] = subColumn;
                }
                newSubColumns[subColumns.length - 1] = row[templateColumn.id]['columns'][subColumns.length - 1];
                row[templateColumn.id]['columns'] = newSubColumns;
                var column = this.columns[templateColumn.ordered + 1];
                column.ordered = i - 1;
                columns[i] = column;
            }
            this.templateGrid.table.columns = columns;
        },
        toDraggable: function() {
            var templateColumns = new Array(this.columns.length);
            templateColumns[0] = this.draggableTitleColumn;
            var columnNameRow = this.rows[0];
            var tableRow = this.rows[1];
            for (var i = 0; i < this.columns.length; i++) {
                var column = this.columns[i];
                var key = column.field;
                if (key == 'title') {
                    continue;
                }
                var templateColumn = {id: key, class: 'field-td flex-grow-1', fixed: false, ordered: column.ordered};
                var fields = [];
                fields.push({value: this.getValue(columnNameRow, key, ' ')});
                var subColumns = tableRow[key].columns;
                var subRows = tableRow[key].rows;
                var subTemplateColumns = new Array(subColumns.length);
                var fieldNameRow = subRows[0];
                var fieldTypeRow = subRows[1];
                var showTypeRow = subRows[2];
                var filterRow = subRows[3];
                var sortedRow = subRows[4];
                var requiredRow = subRows[5];
                var uniquedRow = subRows[6];
                var hiddenRow = subRows[7];
                var defaultRow = subRows[8];
                var widthRow = subRows[9];
                for (var j = 0; j < subColumns.length; j++) {
                    var subColumn = subColumns[j];
                    var subKey = subColumn.field;
                    if (subKey == 'add') {
                        continue;
                    }
                    var subTemplateColumn = {id: subKey, class: 'field-td flex-grow-1', fixed: false, ordered: subColumn.ordered};
                    var subFields = [];
                    subFields.push({value: this.getValue(fieldNameRow, subKey, ' ')});
                    subFields.push({value: this.getValue(fieldTypeRow, subKey, ' '), type: 'select'});
                    subFields.push({value: this.getValue(showTypeRow, subKey, ' '), type: 'select'});
                    subFields.push({value: this.getValue(filterRow, subKey, ' '), type: 'select'});
                    subFields.push({value: this.getValue(sortedRow, subKey, ' '), type: 'select'});
                    subFields.push({value: this.getValue(requiredRow, subKey, ' '), type: 'select'});
                    subFields.push({value: this.getValue(uniquedRow, subKey, ' '), type: 'select'});
                    subFields.push({value: this.getValue(hiddenRow, subKey, ' '), type: 'select'});
                    subFields.push({value: this.getValue(defaultRow, subKey, ' ')});
                    subFields.push({value: this.getValue(widthRow, subKey, ' ')});
                    subFields.push({value: ' '});
                    subTemplateColumn['fields'] = subFields;
                    subTemplateColumns[subColumn.ordered] = subTemplateColumn;
                }
                subTemplateColumns[subColumns.length - 1] = this.draggableAddFieldColumn;
                fields.push({columns: subTemplateColumns, type: 'table'});
                fields.push({value: ' '});
                templateColumn['fields'] = fields;
                templateColumns[column.ordered + 1] = templateColumn;
            }
            this.templateColumns = templateColumns;
            this.rows.push();
        },
        getValue: function(row, key, defaultValue) {
            if (row == undefined || row == null) {
                return defaultValue;
            }
            var obj = row[key];
            if (obj == null) {
                return defaultValue;
            }
            var val = obj['value'];
            if (val == null || val == '') {
                return defaultValue;
            }
            return val;
        },
        saveTemplate: function() {
            var template = this.toTemplate();
            if (!template) {
                return;
            }
            if (this.$route.query.id) {
                template['id'] = this.$route.query.id;
            }
            this.$store.dispatch('saveTemplate', template).then(resp => {
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
    },
    watch: {
        rows(curVal, oldVal) {
            var templateTable = document.getElementById("templateTable");
            var outWidth = templateTable.offsetWidth;
            var table = templateTable.getElementsByTagName("table")[0];
            var inWidth = table.offsetWidth;
            this.draggableClass = outWidth < inWidth ? 'd-inline-flex' : 'd-flex';
        },
        draggable(curVal, oldVal) {
            if (curVal) {
                this.toDraggable();
            } else {
                this.toTable();
            }
        }
    }
}
</script>

<style scoped>
.remove-editor {
    cursor: pointer;
    text-align: center;
}
.form-container >>> .vgt-table thead {
    display: none!important;
}
.draggable-container {
    width: 100%;
    overflow-x: auto;
}
.draggable-table {
    width: 100%;
    min-width: 100%;
    font-size: 14px;
    border: 1px solid #DCDFE6;
    border-left: 0px;
    border-top: 0px;
}
.draggable-column {
    color: #606266;
    border-left: 1px solid #DCDFE6;
    cursor: move;
}
.draggable-field {
    padding: 0.5em;
    border-top: 1px solid #DCDFE6;
    white-space: pre;
}
.draggable-switch {
    padding-right: 5px;
    font-size: 14px;
    color: #606266;
}
</style>

<style>
.field-title-td {
    width: 120px;
    min-width: 120px;
}
.field-td {
    min-width: 150px;
}
.editor-text {
    color: #007bff;
}
</style>