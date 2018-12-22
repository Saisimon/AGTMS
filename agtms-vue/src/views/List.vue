<template>
    <div class="list-container">
        <!-- 面包屑导航 -->
        <b-breadcrumb :items="breadcrumbs" />
        <b-card header-tag="header" footer-tag="footer">
            <!-- 头部 -->
            <b-row slot="header">
                <b-col>
                    <!-- 标题 -->
                    <div class="card-header-title-container text-truncate">
                        {{ header.title }}
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
            <div class="filter-container">
                <b-btn href="javascript:void(0);" 
                    v-b-toggle.filterToggle
                    variant="outline-light"
                    size="sm"
                    class="filter-switch-btn">
                    {{ $t('filter') }}
                    <i class="fa fa-caret-down ml-1"></i>
                </b-btn>
                <b-collapse id="filterToggle" class="filter-toggle">
                    <div class="mb-2" v-for="(filter, key) in filters" :key="key">
                        <b-input-group>
                            <b-input-group-text slot="prepend" class="filter-select-container">
                                <multiselect class="filter-select" 
                                    v-model="filter.key.selected"
                                    label="text"
                                    track-by="value"
                                    select-label=""
                                    deselect-label=""
                                    selectedLabel=""
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
            <!-- 列表 -->
            <div class="table-container">
                <v-table ref="easyTable"
                    is-horizontal-resize
                    multiple-sort
                    style="width:100%;margin-bottom:20px;"
                    even-bg-color="rgba(0,0,0,0.05)"
                    row-hover-color="rgba(0,0,0,0.1)"
                    @sort-change="sortChange"
                    :is-loading="isLoading"
                    :loading-content="'<i class=\'fa fa-circle-o-notch fa-spin fa-2x fa-fw\'></i>'"
                    :error-content="$t('no_data')"
                    :title-rows="titles"
                    :columns="columns"
                    :table-data="datas"
                    :column-cell-class-name="columnCellClass"
                    :select-all="selectAll"
                    :select-change="selectChange"
                    @on-custom-comp="searchByFilters" />
                <v-pagination :total="total"
                    :page-size="pageable.pageSize"
                    :page-index="pageable.pageIndex"
                    :page-size-option="[10, 20, 50]"
                    @page-change="pageChange" 
                    @page-size-change="pageSizeChange" />
            </div>
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
            <!-- 尾部 -->
            <b-row slot="footer" v-if="batches.length > 0">
                <b-col>
                    <!-- 批量操作 -->
                    <b-dropdown variant="primary" right size="sm" 
                        :text="$t('batch_action')"
                        class="float-right">
                        <b-dropdown-item href="javascript:void(0);" 
                            v-for="(batch, index) in batches" 
                            :key="index" 
                            :class="batch.class"
                            @click="batch.click"
                            class="batch-action-btn"  >
                            <i class="fa fa-fw" :class="batch.icon"></i>
                            {{ batch.text }}
                        </b-dropdown-item>
                    </b-dropdown>
                    <action-batch-remove 
                        v-if="functions.indexOf('batchRemove') !== -1" 
                        :model="showBatchRemoveModel"
                        @on-custom-comp="searchByFilters" />
                    <action-batch-edit 
                        v-if="functions.indexOf('batchEdit') !== -1"
                        :model="showBatchEditModel"
                        @on-custom-comp="searchByFilters" />
                    <action-export 
                        v-if="functions.indexOf('export') !== -1" 
                        :model="showExportModel" />
                    <action-import 
                        v-if="functions.indexOf('import') !== -1" 
                        :model="showImportModel" />
                </b-col>
            </b-row>
        </b-card>
    </div>
</template>

<script> 
import Vue from 'vue'
import SearchFilterText from '@/components/filter/SearchFilterText.vue'
import SearchFilterSelect from '@/components/filter/SearchFilterSelect.vue'
import SearchFilterRange from '@/components/filter/SearchFilterRange.vue'
import ActionCell from '@/components/cell/ActionCell.vue'
import TextCell from '@/components/cell/TextCell.vue'
import IconCell from '@/components/cell/IconCell.vue'
import ImageCell from '@/components/cell/ImageCell.vue'
import LinkCell from '@/components/cell/LinkCell.vue'
import ActionBatchRemove from '@/components/action/ActionBatchRemove.vue'
import ActionBatchEdit from '@/components/action/ActionBatchEdit.vue'
import ActionExport from '@/components/action/ActionExport.vue'
import ActionImport from '@/components/action/ActionImport.vue'

export default {
    name: 'list',
    beforeRouteUpdate: function(to, from, next) {
        this.$store.commit('initState');
        if (this.$store.state.base.token !== '') {
            var vm = this;
            this.$store.dispatch('getMainGrid', to.path).then(() => {
                vm.$refs.easyTable.resize();
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
        'action-import': ActionImport
    },
    computed: {
        header: function() {
            return this.$store.state.list.header;
        },
        breadcrumbs: function() {
            return this.$store.state.list.breadcrumbs;
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
                            class: '',
                            text: this.$t('batch_edit'),
                            icon: 'fa-edit',
                            click: this.batchEditClick
                        });
                        break;
                    case 'batchRemove':
                        batches.push({
                            class: 'text-danger',
                            text: this.$t('batch_remove'),
                            icon: 'fa-trash',
                            click: this.batchRemoveClick
                        });
                        break;
                    case 'export':
                        batches.push({
                            class: '',
                            text: this.$t('export'),
                            icon: 'fa-download',
                            click: this.exportClick
                        });
                        break;
                    case 'import':
                        batches.push({
                            class: '',
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
            return this.$store.state.list.filters;
        },
        titles: function() {
            var titles = this.$store.state.list.titles;
            if (titles != null && titles.length > 0) {
                var firstTitle = titles[0];
                if (this.batches.length > 0) {
                    var selectionTitle = {
                        fields: ["id"], 
                        title: '', 
                        titleAlign: 'center', 
                        rowspan: 2
                    };
                    firstTitle.splice(0, 0, selectionTitle);
                }
                var actionTitle = {
                    fields: ["actions"], 
                    title: this.$t('action'), 
                    titleAlign: 'right',
                    rowspan: 2
                }
                firstTitle.push(actionTitle);
            }
            return titles;
        },
        columns: function() {
            var columns = this.$store.state.list.columns;
            for (var i in columns) {
                var column = columns[i];
                column['isResize'] = true;
                switch (column['view']) {
                    case 'icon':
                        column['componentName'] = 'icon-cell';
                        break;
                    case 'image':
                        column['componentName'] = 'image-cell';
                        break;
                    case 'link':
                        column['componentName'] = 'link-cell';
                        break;
                    default:
                        column['componentName'] = 'text-cell';
                        break;
                }
            }
            if (this.batches.length > 0) {
                var selection = {
                    field: 'id',
                    width: 50, 
                    titleAlign: 'center', 
                    columnAlign: 'center', 
                    type: 'selection',
                    isFrozen: true
                };
                columns.splice(0, 0, selection);
            }
            var actions = {
                field: 'actions',
                title: this.$t('action'),
                width: 100,
                titleAlign: 'right',
                columnAlign: 'right',
                componentName: 'action-cell'
            };
            columns.push(actions);
            return columns;
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
        vm.$store.watch(function(state) {
            return state.navigate.openTree;
        }, function() {
            setTimeout(function() {
                if (vm.$refs.easyTable) {
                    vm.$refs.easyTable.resize();
                }
            }, 300);
        });
        window.onresize = function() {
            setTimeout(function() {
                if (vm.$refs.easyTable) {
                    vm.$refs.easyTable.resize();
                }
            }, 300);
        };
        this.$store.commit('initState');
        if (this.$store.state.base.token !== '') {
            this.$store.dispatch('getMainGrid', this.$route.path).then(() => {
                vm.$refs.easyTable.resize();
                vm.searchByFilters();
            });;
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
            }
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
                this.showBatchEditModel.show = true;
            }
        },
        exportClick: function() {
            if (this.checkSelect()) {
                this.showExportModel.show = true;
            }
        },
        importClick: function() {
            if (this.checkSelect()) {
                this.showImportModel.show = true;
            }
        },
        checkSelect: function() {
            if (this.$store.state.list.selects.length == 0) {
                this.alert.text = this.$t("notselected");
                this.alert.dismissCountDown = this.alert.dismissSecs;
                return false;
            } else {
                return true;
            }
        },
        selectAll: function(selection) {
            this.select(selection);
        },
        selectChange: function(selection, rowData) {
            this.select(selection);
        },
        select: function(selection) {
            var selects = [];
            for (var i in selection) {
                var sel = selection[i];
                if (sel.id) {
                    selects.push(sel.id);
                }
            }
            this.$store.commit('setSelects', selects);
        },
        columnCellClass: function(rowIndex, columnName, rowData) {
            if (columnName === 'actions') {
                return 'table-actions-cell';
            }
        },
        pageChange: function(pageIndex) {
            this.$store.commit('setPageIndex', pageIndex);
            this.searchByFilters();
        },
        pageSizeChange: function(pageSize) {
            this.$store.commit('setPageSize', pageSize);
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
            filter['type'] = fieldFilter.input.javaType;
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
            filter['type'] = fieldFilter.select.javaType;
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
                filter['type'] = fieldFilter.from.javaType;
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
                filter['type'] = fieldFilter.to.javaType;
                filter['operator'] = '$lte';
                filter['value'] = toText;
                filters.push(filter);
            }
            return filters;
        }
    }
}

Vue.component('action-cell', ActionCell);
Vue.component('icon-cell', IconCell);
Vue.component('link-cell', LinkCell);
Vue.component('image-cell', ImageCell);
Vue.component('text-cell', TextCell);
</script>

<style>
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
.v-table-class{
    overflow: visible !important;
    font-size: 14px;
}
.v-table-title-class {
    font-weight: 800;
    background-color: rgba(0, 0, 0, 0.05)!important;
}
.v-table-title-cell, .v-table-body-cell {
    overflow: visible !important;
    padding: 0 10px !important;
}
.v-table-empty-content {
    overflow: visible !important;
    border-bottom: 1px solid rgba(221, 221, 221, 1) !important;
    top: 78px!important;
}
.v-table-empty-scroll {
    top: 90px!important;
}
.v-checkbox-wrapper {
    margin: 0px !important;
}
.v-table-body-class {
    min-height: 200px;
}
.v-table-views {
    overflow: visible !important;
    border: 0px !important;
    border-top: 1px solid rgba(221, 221, 221, 1) !important;
    min-height: 280px;
}
.table-actions-cell .v-table-body-cell {
    overflow: visible !important;
}
.v-page-li-active a {
    color: #fff!important;
}
.v-page--middle .v-page-li i {
    font-size: 100%!important;
}
.v-select-selected-span {
    font-weight: 400;
}
.v-table-sort-icon {
    font-size: 16px!important;
    margin-left: 0px!important;
}
.v-page-li-active {
    background-color: #3c8dbc!important;
    border-color: #3c8dbc!important;
}
</style>
