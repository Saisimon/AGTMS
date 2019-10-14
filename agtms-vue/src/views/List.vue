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
                        class="base-btn float-right create-btn">
                        <i class="fa fa-fw fa-plus-circle"></i>
                        {{ $t("create") }}
                    </b-button>
                </b-col>
            </b-row>
            <!-- 筛选 -->
            <div class="filter-container" v-if="filters.length > 0">
                <b-btn href="javascript:void(0);" 
                    v-b-toggle.filter-toggle
                    variant="outline-light"
                    size="sm"
                    class="filter-switch-btn">
                    {{ $t('filter') }}
                    <i class="fa fa-caret-down ml-1"></i>
                </b-btn>
                <b-collapse id="filter-toggle" class="filter-toggle" :visible="showFilters">
                    <div class="mb-2 filter-div" v-for="(filter, key) in filters" :key="key">
                        <b-row class="m-0">
                            <b-col class="p-0 filter-select-container" >
                                <treeselect 
                                    class="filter-select" 
                                    v-model="filter.key.selected" 
                                    :options="filter.key.options"
                                    :disable-branch-nodes="true"
                                    :multiple="false" 
                                    :searchable="false"
                                    :clearable="false"
                                    :noChildrenText="$t('no_childrens')"
                                    :noOptionsText="$t('no_options')"
                                    :noResultsText="$t('no_result')"
                                    :placeholder="''" />
                            </b-col>
                            <b-col class="p-0">
                                <template v-for="option in filter.key.options">
                                    <search-filter-text v-if="filter.value[option.id].type == 'text' && filter.key.selected == option.id" 
                                        :key="option.id" 
                                        :filter="filter.value[option.id]"
                                        :field="option.id" />
                                    <search-filter-select v-if="filter.value[option.id].type == 'select' && filter.key.selected == option.id" 
                                        :key="option.id" 
                                        :filter="filter.value[option.id]"
                                        :field="option.id" />
                                    <search-filter-range v-if="filter.value[option.id].type == 'range' && filter.key.selected == option.id" 
                                        :key="option.id" 
                                        :filter="filter.value[option.id]"
                                        :field="option.id" />
                                </template>
                            </b-col>
                        </b-row>
                    </div>
                    <div class="clearfix">
                        <!-- 搜索 -->
                        <b-button variant="primary" size="sm" 
                            @click="searchByFilters"
                            class="base-btn float-right ml-1 search-btn">
                            {{ $t("search") }}
                        </b-button>
                        <!-- 清除 -->
                        <b-button variant="secondary" size="sm" 
                            @click="clear"
                            class="base-btn float-right ml-1 clear-btn">
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
                    <b-col class="col-12 col-md-8 text-right pl-0">
                        <template v-for="(batch, index) in batches" >
                            <b-button :key="index" 
                                :size="'sm'" 
                                :variant="batch.variant" 
                                @click="batch.click(batch.func)"
                                v-b-tooltip.hover 
                                :title="batch.text"
                                :class="batch.class"
                                class="ml-1">
                                <i class="fa fa-fw" :class="batch.icon"></i>
                            </b-button>
                        </template>
                        <b-button
                            :size="'sm'" 
                            :variant="'outline-primary'" 
                            @click="searchByFilters"
                            v-b-tooltip.hover 
                            :title="$t('refresh')"
                            class="ml-1 refresh-btn">
                            <i class="fa fa-fw fa-refresh"></i>
                        </b-button>
                    </b-col>
                </b-row>
            </div>
            <div class="pb-1 page-index-container">
                <div class="float-left">
                    {{ $t('page_index', {'index': pageIndex}) }}
                </div>
                <div v-if="large" class="float-right">
                    {{ $t('large_table_sort_disabled') }}
                </div>
                <div class="clearfix"></div>
            </div>
            <!-- 列表 -->
            <div class="table-container">
                <vue-good-table
                    ref="listTable"
                    mode="remote"
                    styleClass="vgt-table striped bordered"
                    :is-loading="isLoading"
                    :columns="columns"
                    :rows="datas"
                    :sort-options="sortOptions"
                    :select-options="selectOptions"
                    :pagination-options="paginationOptions"
                    @on-select-all="selectAll"
                    @on-selected-rows-change="selectChange"
                    @on-sort-change="sortChange" >
                    <div slot="loadingContent" class="text-center">
                        <i class="fa fa-circle-o-notch fa-spin fa-2x fa-fw"></i>
                    </div>
                    <div slot="emptystate" class="text-center">
                        {{ $t('no_data') }}
                    </div>
                    <template slot="table-row" slot-scope="props">
                        <span v-if="props.column.field == 'action'">
                            <action-cell 
                                :rowData="props.row" 
                                :field="props.column.field" 
                                :index="props.row.originalIndex" 
                                :actions="actions" 
                                @succeed="searchByFilters" />
                        </span>
                        <span v-else-if="props.column.views == 'icon'">
                            <icon-cell 
                                :rowData="props.formattedRow" 
                                :field="props.column.field" 
                                :index="props.row.originalIndex"  />
                        </span>
                        <span v-else-if="props.column.views == 'link'">
                            <link-cell 
                                :rowData="props.formattedRow" 
                                :field="props.column.field" 
                                :index="props.row.originalIndex"  />
                        </span>
                        <span v-else-if="props.column.views == 'image'">
                            <image-cell 
                                :rowData="props.formattedRow" 
                                :field="props.column.field" 
                                :index="props.row.originalIndex"  />
                        </span>
                        <span v-else-if="props.column.views == 'textarea'">
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
                    <template slot="pagination-bottom">
                        <pagination 
                            :pageIndex="pageIndex"
                            :pageSize="pageSize"
                            @on-per-page-change="onPerPageChange"
                            @on-previous="onPrevious"
                            @on-next="onNext"></pagination>
                    </template>
                </vue-good-table>
            </div>
            <action-batch-operate 
                :modal="showBatchOperateModal"
                :batchOperate="batchOperate"
                :selects="selects"
                @succeed="searchByFilters"
                @failed="showAlert" />
            <action-batch-edit 
                :modal="showBatchEditModal"
                :batchEdit="batchEdit"
                :selects="selects"
                @succeed="searchByFilters"
                @failed="showAlert" />
            <action-export 
                :modal="showExportModal"
                :batchExport="batchExport"
                :selects="selects"
                :filter="searchFilters()"
                @showAlert="showAlert" />
            <action-import 
                :modal="showImportModal"
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
import ActionBatchOperate from '@/components/action/ActionBatchOperate.vue'
import ActionBatchEdit from '@/components/action/ActionBatchEdit.vue'
import ActionExport from '@/components/action/ActionExport.vue'
import ActionImport from '@/components/action/ActionImport.vue'
import Pagination from '@/components/Pagination.vue'

export default {
    name: 'list',
    beforeRouteUpdate: function(to, from, next) {
        this.$store.commit('initState');
        if (this.$store.state.base.user != null) {
            var vm = this;
            this.$store.dispatch('getMainGrid', to.path).then(resp => {
                if (resp.data.code === 0) {
                    vm.$store.commit('setFunctions', resp.data.data.functions);
                    vm.$store.commit('setShowFilters', resp.data.data.showFilters);
                    vm.$store.commit('setFilters', resp.data.data.filters);
                    vm.$store.commit('setHeader', resp.data.data.header);
                    vm.$store.commit('setBreadcrumbs', resp.data.data.breadcrumbs);
                    vm.$store.commit('setColumns', resp.data.data.columns);
                    vm.$store.commit('setActions', resp.data.data.actions);
                    vm.large = resp.data.data.large;
                    var pageable = resp.data.data.pageable;
                    vm.pageIndex = pageable.pageIndex;
                    vm.pageSize = pageable.pageSize;
                    vm.param = pageable.param;
                    vm.filterMap = vm.searchFilters();
                    vm.$store.dispatch('getDatas', {
                        url: to.path,
                        filters: vm.filterMap,
                        pageable: vm.searchPageable()
                    });
                }
                vm.$store.commit('clearProgress');
            });
        }
        next();
    },
    components: {
        'search-filter-text': SearchFilterText,
        'search-filter-select': SearchFilterSelect,
        'search-filter-range': SearchFilterRange,
        'action-batch-operate': ActionBatchOperate,
        'action-batch-edit': ActionBatchEdit,
        'action-export': ActionExport,
        'action-import': ActionImport,
        'action-cell': ActionCell,
        'icon-cell': IconCell,
        'link-cell': LinkCell,
        'image-cell': ImageCell,
        'html-cell': HtmlCell,
        'text-cell': TextCell,
        'pagination': Pagination
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
                            class: 'batch-edit-btn',
                            click: this.batchEditClick,
                            func: func
                        });
                        break;
                    case 'batchRemove':
                        batches.push({
                            variant:'outline-danger',
                            text: this.$t('batch_remove'),
                            icon: 'fa-trash',
                            class: 'batch-remove-btn',
                            click: this.batchOperateClick,
                            func: func
                        });
                        break;
                    case 'grant':
                        batches.push({
                            variant:'outline-danger',
                            text: this.$t('grant'),
                            icon: 'fa-certificate',
                            class: 'batch-remove-btn',
                            click: this.batchOperateClick,
                            func: func
                        });
                        break;
                    case 'export':
                        batches.push({
                            variant:'outline-secondary',
                            text: this.$t('export'),
                            icon: 'fa-download',
                            class: 'batch-export-btn',
                            click: this.exportClick,
                            func: func
                        });
                        break;
                    case 'import':
                        batches.push({
                            variant:'outline-secondary',
                            text: this.$t('import'),
                            icon: 'fa-upload',
                            class: 'batch-import-btn',
                            click: this.importClick,
                            func: func
                        });
                        break;
                    default:
                        break;
                }
            }
            return batches;
        },
        showFilters: function() {
            return this.$store.state.list.showFilters;
        },
        filters: function() {
            return this.$store.state.list.filters;
        },
        columns: function() {
            return this.$store.state.list.columns;
        },
        actions: function() {
            return this.$store.state.list.actions;
        },
        batchOperate: function() {
            return this.$store.state.list.batchOperate;
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
        }
    },
    watch: {
        batches() {
            if (this.batches.length > 0) {
                this.selectOptions.enabled = true;
            } else {
                this.selectOptions.enabled = false;
            }
        }
    },
    created: function() {
        var vm = this;
        this.$store.commit('initState');
        if (this.$store.state.base.user != null) {
            this.$store.dispatch('getMainGrid', this.$route.path).then(resp => {
                if (resp.data.code === 0) {
                    vm.$store.commit('setFunctions', resp.data.data.functions);
                    vm.$store.commit('setShowFilters', resp.data.data.showFilters);
                    vm.$store.commit('setFilters', resp.data.data.filters);
                    vm.$store.commit('setHeader', resp.data.data.header);
                    vm.$store.commit('setBreadcrumbs', resp.data.data.breadcrumbs);
                    vm.$store.commit('setColumns', resp.data.data.columns);
                    vm.$store.commit('setActions', resp.data.data.actions);
                    vm.large = resp.data.data.large;
                    var pageable = resp.data.data.pageable;
                    vm.pageIndex = pageable.pageIndex;
                    vm.pageSize = pageable.pageSize;
                    vm.param = pageable.param;
                    vm.filterMap = vm.searchFilters();
                    vm.searchByFilters();
                }
                vm.$store.commit('clearProgress');
            });
        } else {
            this.$store.commit('clearProgress');
        }
    },
    data: function() {
        return {
            showBatchOperateModal: {
                show: false
            },
            showBatchEditModal: {
                show: false
            },
            showExportModal: {
                show: false
            },
            showImportModal: {
                show: false
            },
            selectOptions: {
                enabled: false,
                selectionText: this.$t('rows_selected'),
                clearSelectionText: '',
            },
            sortOptions: {
                enabled: false
            },
            selects: [],
            large: false,
            pageSize: 10,
            pageIndex: 1,
            param: null,
            filterMap: null,
            paginationOptions: {
                enabled: true
            }
        }
    },
    methods: {
        batchOperateClick: function(func) {
            console.log(func);
            if (this.checkSelect()) {
                this.$store.dispatch('getBatchGrid', {
                    url: this.$route.path,
                    type: 'operate',
                    func: func
                }).then(resp => {
                    this.$store.commit('setBatchOperate', resp.data.data);
                });
                this.showBatchOperateModal.show = true;
            }
        },
        batchEditClick: function(func) {
            if (this.checkSelect()) {
                this.$store.dispatch('getBatchGrid', {
                    url: this.$route.path,
                    type: 'edit',
                    func: func
                }).then(resp => {
                    this.$store.commit('setBatchEdit', resp.data.data);
                });
                this.showBatchEditModal.show = true;
            }
        },
        exportClick: function(func) {
            this.$store.dispatch('getBatchGrid', {
                    url: this.$route.path,
                    type: 'export',
                    func: func
                }).then(resp => {
                this.$store.commit('setBatchExport', resp.data.data);
            });
            this.showExportModal.show = true;
        },
        importClick: function(func) {
            this.$store.dispatch('getBatchGrid', {
                    url: this.$route.path,
                    type: 'import',
                    func: func
                }).then(resp => {
                this.$store.commit('setBatchImport', resp.data.data);
            });
            this.showImportModal.show = true;
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
            this.$store.commit('showAlert', {
                message: message,
                variant: variant
            });
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
        columnCellClass: function(rowIndex, columnName) {
            if (columnName === 'action') {
                return 'table-actions-cell';
            }
        },
        onPerPageChange: function(params) {
            if (this.pageSize == params.currentPerPage) {
                return;
            }
            this.pageIndex = 1;
            this.pageSize = params.currentPerPage;
            this.param = null;
            this.searchByFilters();
        },
        onPrevious: function(param) {
            if (this.pageIndex < 2) {
                return;
            }
            this.pageIndex = this.pageIndex - 1;
            if (this.large) {
                this.param = param;
            }
            this.searchByFilters();
        },
        onNext: function(param) {
            this.pageIndex = this.pageIndex + 1;
            if (this.large) {
                this.param = param;
            }
            this.searchByFilters();
        },
        sortChange: function(params) {
            if (this.large) {
                this.$store.commit('clearSort');
                this.$set(this.$refs["listTable"].$refs["table-header-primary"]._data, "sorts", []);
            } else {
                this.$store.commit('setSort', params);
                this.pageIndex = 1;
                this.param = null;
                this.searchByFilters();
            }
        },
        clear: function() {
            this.$store.commit('clearFilters');
            this.$store.commit('clearSort');
            this.$set(this.$refs["listTable"].$refs["table-header-primary"]._data, "sorts", []);
            this.searchByFilters();
        },
        searchByFilters: function() {
            var newFilterMap = this.searchFilters();
            if (JSON.stringify(newFilterMap) != JSON.stringify(this.filterMap)) {
                this.pageIndex = 1;
                this.param = null;
            }
            this.filterMap = newFilterMap;
            this.$store.dispatch('getDatas', {
                url: this.$route.path,
                filters: this.filterMap,
                pageable: this.searchPageable()
            });
        },
        searchPageable: function() {
            var sorts = [];
            for (var i in this.columns) {
                var column = this.columns[i];
                if (column.orderBy && column.field) {
                    if (column.orderBy === 'asc') {
                        sorts.push('+' + column.field);
                    } else if (column.orderBy === 'desc') {
                        sorts.push('-' + column.field);
                    }
                }
            }
            return {
                'param': this.param,
                'index': this.pageIndex - 1,
                'size': this.pageSize,
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
                        for (var j = 0; j < parseFilters.length; j++) {
                            searchFilters.push(parseFilters[j]);
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
            var operator = fieldFilter.operator.selected;
            if (operator === 'fuzzy') {
                filter['operator'] = '$regex';
            } else if (operator === 'separator') {
                if (!Array.isArray(text)) {
                    var textList = text.split(',');
                    if (textList && textList.length > 1) {
                        filter['operator'] = '$in';
                        filter['value'] = textList;
                    }
                } else if (text.length > 1) {
                    filter['operator'] = '$in';
                }
            }
            filters.push(filter);
            return filters;
        },
        parseSelectFilter: function(fieldName, fieldFilter) {
            var selected = fieldFilter.select.selected;
            if (selected == null) {
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
                    selectedValues.push(i);
                }
                filter['value'] = selectedValues;
            } else {
                filter['value'] = selected;
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
                if (fieldFilter.from.type === 'number') {
                    fromText = new Number(fromText);
                }
                var fromFilter = {};
                fromFilter['key'] = fieldName;
                fromFilter['type'] = fieldFilter.from.type;
                fromFilter['operator'] = '$gte';
                fromFilter['value'] = fromText;
                filters.push(fromFilter);
            }
            if (toText && toText !== '') {
                if (fieldFilter.to.type === 'number') {
                    toText = new Number(toText);
                }
                var toFilter = {};
                toFilter['key'] = fieldName;
                toFilter['type'] = fieldFilter.to.type;
                toFilter['operator'] = '$lte';
                toFilter['value'] = toText;
                filters.push(toFilter);
            }
            return filters;
        }
    }
}
</script>

<style scoped>
.page-index-container {
    font-size: 14px;
    font-weight: bold;
    color: #606266;
}
.selects-hint {
    font-size: 14px;
    font-weight: bold;
    line-height: 31px;
    color: #606266;
}
.list-container >>> .vgt-left-align {
    min-width: 130px;
}
.list-container >>> .vgt-right-align {
    min-width: 130px;
}
</style>
