<template>
    <div class="list-container">
        <b-card header-tag="header" footer-tag="footer">
            <!-- 头部 -->
            <b-row slot="header">
                <b-col>
                    <!-- 标题 -->
                    <div class="card-header-title-container text-truncate">
                        <b-link v-if="header.editUrl != null" :to="header.editUrl" >{{ header.title }}</b-link>
                        <div v-else>{{ header.title }}</div>
                    </div>
                </b-col>
                <b-col v-if="functions.indexOf('create') !== -1">
                    <!-- 创建 -->
                    <b-button variant="primary" size="sm" 
                        :to="header.createUrl" 
                        class="base-btn float-right">
                        <i class="fa fa-fw fa-plus-circle"></i>
                        {{ $t("create") }}
                    </b-button>
                </b-col>
            </b-row>
            <!-- 筛选 -->
            <div class="filter-container" v-if="filters.length > 0">
                <b-btn href="javascript:void(0);" 
                    v-b-toggle.filterToggle
                    variant="outline-light"
                    size="sm"
                    class="filter-switch-btn">
                    {{ $t('filter') }}
                    <i class="fa fa-caret-down ml-1"></i>
                </b-btn>
                <b-collapse id="filterToggle" class="filter-toggle" :visible="showFilters">
                    <div class="mb-2 filter-div" v-for="(filter, key) in filters" :key="key">
                        <b-input-group>
                            <b-input-group-text slot="prepend" class="filter-select-container">
                                <multiselect class="filter-select" 
                                    v-model="filter.key.selected"
                                    label="text"
                                    track-by="value"
                                    select-label=""
                                    deselect-label=""
                                    selected-label=""
                                    :allow-empty="false"
                                    :searchable="false"
                                    :options="filter.key.options"
                                    :placeholder="''" />
                            </b-input-group-text>
                            <template v-for="option in filter.key.options">
                                <search-filter-text v-if="filter.value[option.value].type == 'text' && filter.key.selected.value == option.value" 
                                    :key="option.value" 
                                    :filter="filter.value[option.value]" />
                                <search-filter-select v-if="filter.value[option.value].type == 'select' && filter.key.selected.value == option.value" 
                                    :key="option.value" 
                                    :filter="filter.value[option.value]" />
                                <search-filter-range v-if="filter.value[option.value].type == 'range' && filter.key.selected.value == option.value" 
                                    :key="option.value" 
                                    :filter="filter.value[option.value]" />
                            </template>
                        </b-input-group>
                    </div>
                    <div class="clearfix">
                        <!-- 搜索 -->
                        <b-button variant="primary" size="sm" 
                            @click="searchByFilters"
                            class="base-btn float-right ml-1">
                            {{ $t("search") }}
                        </b-button>
                        <!-- 清除 -->
                        <b-button variant="secondary" size="sm" 
                            @click="clearFilters"
                            class="base-btn float-right ml-1">
                            {{ $t("clear") }}
                        </b-button>
                    </div>
                </b-collapse>
            </div>
            <!-- 批量操作 -->
            <div class="batch-action-container mb-2" v-if="batches.length > 0">
                <b-row>
                    <b-col>
                        <div class="selects-hint">
                        {{ selects.length }} {{ $t('rows_selected') }}
                        </div>
                    </b-col>
                    <b-col cols="8" class="text-right">
                        <template v-for="(batch, index) in batches" >
                            <b-button :key="index" 
                                :size="'sm'" 
                                :variant="batch.variant" 
                                @click="batch.click"
                                v-b-tooltip.hover 
                                :title="batch.text"
                                class="ml-1">
                                <i class="fa fa-fw" :class="batch.icon"></i>
                            </b-button>
                        </template>
                    </b-col>
                </b-row>
            </div>
            <!-- 列表 -->
            <div class="table-container">
                <vue-good-table
                    mode="remote"
                    styleClass="vgt-table striped bordered"
                    :is-loading="isLoading"
                    :columns="columns"
                    :rows="datas"
                    :totalRecords="total"
                    :sort-options="sortOptions"
                    :select-options="selectOptions"
                    @on-select-all="selectAll"
                    @on-selected-rows-change="selectChange"
                    @on-sort-change="sortChange" >
                    <div slot="loadingContent" class="text-center">
                        <i class="fa fa-circle-o-notch fa-spin fa-2x fa-fw"></i>
                    </div>
                    <div slot="emptystate" class="text-center">
                        {{ $t('no_data') }}
                    </div>
                    <b-row slot="table-actions-bottom" class="mt-2">
                        <b-col>
                            <div class="rows-div">{{ $t("total") }} {{ total }} {{ $t("rows") }}</div>
                        </b-col>
                        <b-col cols="8">
                            <b-pagination :total-rows="total" :per-page="10" align="right" @change="pageChange"></b-pagination>
                        </b-col>
                    </b-row>
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
            </div>
            <!-- 警告框 -->
            <div class="modal d-block" v-if="alert.dismissCountDown" style="z-index: 9999">
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
            <action-batch-remove 
                v-if="functions.indexOf('batchRemove') !== -1" 
                :model="showBatchRemoveModel"
                :selects="selects"
                @succeed="searchByFilters"
                @failed="showAlert" />
            <action-batch-edit 
                v-if="functions.indexOf('batchEdit') !== -1"
                :model="showBatchEditModel"
                :batchEdit="batchEdit"
                :selects="selects"
                @succeed="searchByFilters"
                @failed="showAlert" />
            <action-export 
                v-if="functions.indexOf('export') !== -1" 
                :model="showExportModel"
                :batchExport="batchExport"
                :selects="selects"
                :filter="searchFilters()"
                @showAlert="showAlert" />
            <action-import 
                v-if="functions.indexOf('import') !== -1" 
                :model="showImportModel"
                :batchImport="batchImport"
                :selects="selects"
                @showAlert="showAlert" />
        </b-card>
    </div>
</template>

<script>
import SearchFilterText from '@/components/filter/SearchFilterText.vue'
import SearchFilterSelect from '@/components/filter/SearchFilterSelect.vue'
import SearchFilterRange from '@/components/filter/SearchFilterRange.vue'
import ActionCell from '@/components/cell/ActionCell.vue'
import TextCell from '@/components/cell/TextCell.vue'
import IconCell from '@/components/cell/IconCell.vue'
import ImageCell from '@/components/cell/ImageCell.vue'
import LinkCell from '@/components/cell/LinkCell.vue'
import HtmlCell from '@/components/cell/HtmlCell.vue'
import ActionBatchRemove from '@/components/action/ActionBatchRemove.vue'
import ActionBatchEdit from '@/components/action/ActionBatchEdit.vue'
import ActionExport from '@/components/action/ActionExport.vue'
import ActionImport from '@/components/action/ActionImport.vue'

export default {
    name: 'list',
    beforeRouteUpdate: function(to, from, next) {
        this.$store.commit('initState');
        if (this.$store.state.base.user != null) {
            var vm = this;
            this.$store.dispatch('getMainGrid', to.path).then(() => {
                vm.$store.dispatch('getDatas', {
                    url: to.path,
                    filters: vm.searchFilters(),
                    pageable: vm.searchPageable()
                });
            });
        }
        next();
    },
    components: {
        'search-filter-text': SearchFilterText,
        'search-filter-select': SearchFilterSelect,
        'search-filter-range': SearchFilterRange,
        'action-batch-remove': ActionBatchRemove,
        'action-batch-edit': ActionBatchEdit,
        'action-export': ActionExport,
        'action-import': ActionImport,
        'action-cell': ActionCell,
        'icon-cell': IconCell,
        'link-cell': LinkCell,
        'image-cell': ImageCell,
        'html-cell': HtmlCell,
        'text-cell': TextCell
    },
    computed: {
        header: function() {
            return this.$store.state.list.header;
        },
        isLoading: function() {
            return this.$store.state.list.isLoading;
        },
        functions: function() {
            return this.$store.state.list.functions;
        },
        batches: function() {
            var batches = [];
            var functions = this.$store.state.list.functions;
            for (var i in functions) {
                var func = functions[i];
                switch (func) {
                    case 'batchEdit':
                        batches.push({
                            variant:'outline-primary',
                            text: this.$t('batch_edit'),
                            icon: 'fa-edit',
                            click: this.batchEditClick
                        });
                        break;
                    case 'batchRemove':
                        batches.push({
                            variant:'outline-danger',
                            text: this.$t('batch_remove'),
                            icon: 'fa-trash',
                            click: this.batchRemoveClick
                        });
                        break;
                    case 'export':
                        batches.push({
                            variant:'outline-secondary',
                            text: this.$t('export'),
                            icon: 'fa-download',
                            click: this.exportClick
                        });
                        break;
                    case 'import':
                        batches.push({
                            variant:'outline-secondary',
                            text: this.$t('import'),
                            icon: 'fa-upload',
                            click: this.importClick
                        });
                        break;
                    default:
                        break;
                }
            }
            return batches;
        },
        filters: function() {
            this.showFilters = this.$store.state.list.showFilters;
            return this.$store.state.list.filters;
        },
        columns: function() {
            if (this.batches.length > 0) {
                this.selectOptions.enabled = true;
            } else {
                this.selectOptions.enabled = false;
            }
            return this.$store.state.list.columns;
        },
        actions: function() {
            return this.$store.state.list.actions;
        },
        batchEdit: function() {
            return this.$store.state.list.batchEdit;
        },
        batchExport: function() {
            return this.$store.state.list.batchExport;
        },
        batchImport: function() {
            return this.$store.state.list.batchImport;
        },
        datas: function() {
            return this.$store.state.list.datas;
        },
        pageable: function() {
            return this.$store.state.list.pageable;
        },
        total: function() {
            return this.$store.state.list.total;
        }
    },
    created: function() {
        var vm = this;
        this.$store.commit('initState');
        if (this.$store.state.base.user != null) {
            this.$store.dispatch('getMainGrid', this.$route.path).then(() => {
                vm.searchByFilters();
            });
        }
    },
    data: function() {
        return {
            showBatchRemoveModel: {
                show: false
            },
            showBatchEditModel: {
                show: false
            },
            showExportModel: {
                show: false
            },
            showImportModel: {
                show: false
            },
            alert: {
                dismissSecs: 3,
                dismissCountDown: 0,
                variant: 'danger',
                text: ''
            },
            sortOptions: {
                enabled: true
            },
            selectOptions: {
                enabled: false,
                selectionText: this.$t('rows_selected'),
                clearSelectionText: '',
            },
            selects: [],
            showFilters: false
        }
    },
    methods: {
        batchRemoveClick: function() {
            if (this.checkSelect()) {
                this.showBatchRemoveModel.show = true;
            }
        },
        batchEditClick: function() {
            if (this.checkSelect()) {
                this.$store.dispatch('getBatchGrid', this.$route.path);
                this.showBatchEditModel.show = true;
            }
        },
        exportClick: function() {
            this.$store.dispatch('getBatchGrid', this.$route.path);
            this.showExportModel.show = true;
        },
        importClick: function() {
            this.$store.dispatch('getBatchGrid', this.$route.path);
            this.showImportModel.show = true;
        },
        checkSelect: function() {
            if (this.selects.length == 0) {
                this.showAlert(this.$t("notselected"), 'danger');
                return false;
            } else {
                return true;
            }
        },
        showAlert: function(message, variant) {
            this.alert.text = message;
            this.alert.dismissCountDown = this.alert.dismissSecs;
            if (variant) {
                this.alert.variant = variant;
            }
        },
        selectAll: function(params) {
            this.select(params.selectedRows);
        },
        selectChange: function(params) {
            this.select(params.selectedRows);
        },
        select: function(selection) {
            var selects = [];
            for (var i in selection) {
                var sel = selection[i];
                if (sel.id) {
                    selects.push(sel.id);
                }
            }
            this.selects = selects;
        },
        columnCellClass: function(rowIndex, columnName, rowData) {
            if (columnName === 'action') {
                return 'table-actions-cell';
            }
        },
        pageChange: function(pageIndex) {
            this.$store.commit('setPageIndex', pageIndex);
            this.searchByFilters();
        },
        pageSizeChange: function(pageSize) {
            this.$store.commit('setPageSize', params.pageSize);
            this.searchByFilters();
        },
        sortChange: function(params) {
            this.$store.commit('setSort', params);
            this.searchByFilters();
        },
        searchByFilters: function() {
            this.$store.dispatch('getDatas', {
                url: this.$route.path,
                filters: this.searchFilters(),
                pageable: this.searchPageable()
            });
        },
        clearFilters: function() {
            this.$store.commit('clearFilters');
            this.searchByFilters();
        },
        searchPageable: function() {
            var sorts = [];
            for (var i in this.columns) {
                var column = this.columns[i];
                if (column.orderBy && column.field) {
                    if (column.orderBy === 'asc') {
                        sorts.push('%2B' + column.field);
                    } else if (column.orderBy === 'desc') {
                        sorts.push('%2D' + column.field);
                    }
                }
            }
            return {
                'index': (this.pageable.pageIndex - 1),
                'size': this.pageable.pageSize,
                'sort': sorts.join(',')
            };
        },
        searchFilters: function() {
            var searchFilters = [];
            for (var i in this.filters) {
                var value = this.filters[i].value;
                for (var fieldName in value) {
                    var fieldFilter = value[fieldName];
                    var parseFilters;
                    switch (fieldFilter.type) {
                        case 'range':
                            parseFilters = this.parseRangeFilter(fieldName, fieldFilter);
                            break;
                        case 'select':
                            parseFilters = this.parseSelectFilter(fieldName, fieldFilter);
                            break;
                        default:
                            parseFilters = this.parseTextFilter(fieldName, fieldFilter);
                            break;
                    }
                    if (parseFilters && parseFilters.length > 0) {
                        for (var i = 0; i < parseFilters.length; i++) {
                            searchFilters.push(parseFilters[i]);
                        }
                    }
                }
            }
            return {
                'andFilters': searchFilters
            };
        },
        parseTextFilter: function(fieldName, fieldFilter) {
            var text = fieldFilter.input.value;
            if (!text || text === '') {
                return null;
            }
            var filters = [];
            var filter = {};
            filter['key'] = fieldName;
            filter['type'] = fieldFilter.input.type;
            filter['operator'] = '$eq';
            filter['value'] = text;
            var operator = fieldFilter.operator.selected.value;
            if (operator === 'fuzzy') {
                filter['operator'] = '$regex';
            } else if (operator === 'separator') {
                var textList = text.split(',');
                if (textList && textList.length > 1) {
                    filter['operator'] = '$in';
                    filter['value'] = textList;
                }
            }
            filters.push(filter);
	        return filters;
        },
        parseSelectFilter: function(fieldName, fieldFilter) {
            var selected = fieldFilter.select.selected;
            if (!selected || selected.length === 0) {
                return null;
            }
            var filters = [];
            var filter = {};
            filter['key'] = fieldName;
            filter['type'] = fieldFilter.select.type;
            if (fieldFilter.multiple) {
                filter['operator'] = '$in';
                var selectedValues = [];
                for (var i in selected) {
                    selectedValues.push(selected[i].value);
                }
                filter['value'] = selectedValues;
            } else {
                filter['value'] = selected.value;
                filter['operator'] = '$eq';
            }
            filters.push(filter);
	        return filters;
        },
        parseRangeFilter: function(fieldName, fieldFilter) {
            var fromText = fieldFilter.from.value;
            var toText = fieldFilter.to.value;
            var filters = [];
            if (fromText && fromText !== '') {
                var filter = {};
                if (fieldFilter.from.type === 'number') {
                    fromText = new Number(fromText);
                }
                filter['key'] = fieldName;
                filter['type'] = fieldFilter.from.type;
                filter['operator'] = '$gte';
                filter['value'] = fromText;
                filters.push(filter);
            }
            if (toText && toText !== '') {
                var filter = {};
                if (fieldFilter.to.type === 'number') {
                    toText = new Number(toText);
                }
                filter['key'] = fieldName;
                filter['type'] = fieldFilter.to.type;
                filter['operator'] = '$lte';
                filter['value'] = toText;
                filters.push(filter);
            }
            return filters;
        }
    }
}
</script>

<style>
.breadcrumb {
    padding: 0.75rem 1.25rem!important;
}
.card-header-title-container {
    height: 31px;
    line-height: 31px;
    font-size: 17px;
    font-weight: bold;
    color: #555;
}
.batch-action-btn {
    line-height: 30px;
    font-size: 14px;
}
.filter-container {
    margin-bottom: 10px;
}
.filter-switch-btn {
    width: 100%;
    color: #000!important;
}
.filter-toggle {
    padding: 10px;
    border: 1px solid #e8e8e8;
    border-radius: 0.25rem;
}
.filter-select-container {
    padding: 0!important;
    border: 0!important;
}
.filter-select {
    height: 40px!important;
}
.multiselect__tags {
    height: 40px!important;
}
.multiselect__content-wrapper {
    width: auto!important;
    min-width: 100%!important;
}
.filter-select-right .multiselect__content-wrapper {
    right: 0;
}
.filter-toggle .form-control {
    height: 40px!important;
    border: 1px solid #e8e8e8!important;
}
.selects-hint {
    font-size: 13px;
    font-weight: bold;
    line-height: 31px;
    color: #606266;
    padding-left: 0.5em;
}
.rows-div {
    font-size: 14px;
    line-height: 38px;
    height: 38px;
}
.vgt-selection-info-row {
    display: none!important;
}
.vgt-wrap__actions-footer {
    border: 0px!important;
    position: relative;
}
table.vgt-table {
    font-size: 14px!important;
}
table.vgt-table .vgt-left-align {
    vertical-align: inherit!important;
    padding: 0.5em!important;
}
table.vgt-table .sorting {
    padding-right: 1.5em!important;
}
table.vgt-table .vgt-right-align {
    vertical-align: inherit!important;
    padding: 0.5em;
}
table.vgt-table th {
    height: 40px;
}
.batch-action-btn {
    margin: 0px!important;
}
</style>
