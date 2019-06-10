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
                                    :placeholder="''" >
                                <template slot="noResult">{{ $t("no_result") }}</template>
                                <template slot="noOptions">{{ $t("no_options") }}</template>
                            </multiselect>
                            </b-input-group-text>
                            <template v-for="option in filter.key.options">
                                <search-filter-text v-if="filter.value[option.value].type == 'text' && filter.key.selected.value == option.value" 
                                    :key="option.value" 
                                    :filter="filter.value[option.value]"
                                    :field="option.value" />
                                <search-filter-select v-if="filter.value[option.value].type == 'select' && filter.key.selected.value == option.value" 
                                    :key="option.value" 
                                    :filter="filter.value[option.value]"
                                    :field="option.value" />
                                <search-filter-range v-if="filter.value[option.value].type == 'range' && filter.key.selected.value == option.value" 
                                    :key="option.value" 
                                    :filter="filter.value[option.value]"
                                    :field="option.value" />
                            </template>
                        </b-input-group>
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
                            @click="clearFilters"
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
                    <b-col class="col-12 col-md-8 text-right">
                        <template v-for="(batch, index) in batches" >
                            <b-button :key="index" 
                                :size="'sm'" 
                                :variant="batch.variant" 
                                @click="batch.click"
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
            <!-- 列表 -->
            <div class="table-container">
                <vue-good-table
                    mode="remote"
                    styleClass="vgt-table striped bordered"
                    :is-loading="isLoading"
                    :columns="columns"
                    :rows="datas"
                    :totalRows="total"
                    :sort-options="sortOptions"
                    :select-options="selectOptions"
                    :pagination-options="paginationOptions"
                    @on-page-change="onPageChange"
                    @on-per-page-change="onPerPageChange"
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
                </vue-good-table>
            </div>
            <action-batch-remove 
                v-if="functions.indexOf('batchRemove') !== -1" 
                :modal="showBatchRemoveModal"
                :selects="selects"
                @succeed="searchByFilters"
                @failed="showAlert" />
            <action-batch-edit 
                v-if="functions.indexOf('batchEdit') !== -1"
                :modal="showBatchEditModal"
                :batchEdit="batchEdit"
                :selects="selects"
                @succeed="searchByFilters"
                @failed="showAlert" />
            <action-export 
                v-if="functions.indexOf('export') !== -1" 
                :modal="showExportModal"
                :batchExport="batchExport"
                :selects="selects"
                :filter="searchFilters()"
                @showAlert="showAlert" />
            <action-import 
                v-if="functions.indexOf('import') !== -1" 
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
            this.$store.dispatch('getMainGrid', to.path).then(resp => {
                if (resp.data.code === 0) {
                    vm.$store.commit('setFunctions', resp.data.data.functions);
                    vm.$store.commit('setShowFilters', resp.data.data.showFilters);
                    vm.$store.commit('setFilters', resp.data.data.filters);
                    vm.$store.commit('setHeader', resp.data.data.header);
                    vm.$store.commit('setBreadcrumbs', resp.data.data.breadcrumbs);
                    vm.$store.commit('setColumns', resp.data.data.columns);
                    vm.$store.commit('setActions', resp.data.data.actions);
                    var pageable = resp.data.data.pageable;
                    vm.pageIndex = pageable.pageIndex;
                    vm.pageSize = pageable.pageSize;
                    vm.paginationOptions.setCurrentPage = pageable.pageIndex;
                    vm.paginationOptions.perPage = pageable.pageSize;
                    vm.$store.dispatch('getDatas', {
                        url: to.path,
                        filters: vm.searchFilters(),
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
                            class: 'batch-edit-btn',
                            click: this.batchEditClick
                        });
                        break;
                    case 'batchRemove':
                        batches.push({
                            variant:'outline-danger',
                            text: this.$t('batch_remove'),
                            icon: 'fa-trash',
                            class: 'batch-remove-btn',
                            click: this.batchRemoveClick
                        });
                        break;
                    case 'export':
                        batches.push({
                            variant:'outline-secondary',
                            text: this.$t('export'),
                            icon: 'fa-download',
                            class: 'batch-export-btn',
                            click: this.exportClick
                        });
                        break;
                    case 'import':
                        batches.push({
                            variant:'outline-secondary',
                            text: this.$t('import'),
                            icon: 'fa-upload',
                            class: 'batch-import-btn',
                            click: this.importClick
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
        total: function() {
            return this.$store.state.list.total;
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
                    var pageable = resp.data.data.pageable;
                    vm.pageIndex = pageable.pageIndex;
                    vm.pageSize = pageable.pageSize;
                    vm.paginationOptions.setCurrentPage = pageable.pageIndex;
                    vm.paginationOptions.perPage = pageable.pageSize;
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
            showBatchRemoveModal: {
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
            sortOptions: {
                enabled: true
            },
            selectOptions: {
                enabled: false,
                selectionText: this.$t('rows_selected'),
                clearSelectionText: '',
            },
            selects: [],
            pageSize: 10,
            pageIndex: 1,
            paginationOptions: {
                enabled: true,
                mode: 'pages',
                perPageDropdown: [10, 20, 50],
                setCurrentPage: this.pageIndex,
                perPage: this.pageSize,
                dropdownAllowAll: false,
                nextLabel: this.$t('next_page'),
                prevLabel: this.$t('previous_page'),
                rowsPerPageLabel: this.$t('rows_pre_page'),
                ofLabel: "/",
                pageLabel: ''
            }
        }
    },
    methods: {
        batchRemoveClick: function() {
            if (this.checkSelect()) {
                this.showBatchRemoveModal.show = true;
            }
        },
        batchEditClick: function() {
            if (this.checkSelect()) {
                this.$store.dispatch('getBatchGrid', this.$route.path);
                this.showBatchEditModal.show = true;
            }
        },
        exportClick: function() {
            this.$store.dispatch('getBatchGrid', this.$route.path);
            this.showExportModal.show = true;
        },
        importClick: function() {
            this.$store.dispatch('getBatchGrid', this.$route.path);
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
        onPageChange: function(params) {
            this.pageIndex = params.currentPage;
            this.searchByFilters();
        },
        onPerPageChange: function(params) {
            if (this.pageSize == params.currentPerPage) {
                return;
            }
            this.pageIndex = 1;
            this.pageSize = params.currentPerPage;
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
                'index': (this.pageIndex - 1),
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
            var operator = fieldFilter.operator.selected.value;
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
.list-container >>> .vgt-left-align {
    min-width: 130px;
}
.list-container >>> .vgt-right-align {
    min-width: 130px;
}
</style>


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
    line-height: 35px;
    height: 35px;
}
.page-link {
    font-size: 14px;
}
.vgt-selection-info-row {
    display: none!important;
}
.vgt-wrap__actions-footer {
    border: 0px!important;
    position: relative;
}
.vgt-wrap__footer {
    border-top: 0px!important;
    padding: .75em!important;
}
.vgt-wrap__footer .footer__row-count__select {
    width: 50px!important;
    height: 30px!important;
    line-height: 30px!important;
}
.vgt-wrap__footer .footer__navigation__page-info__current-entry {
    width: 50px!important;
    margin: 0px!important;
    height: 30px!important;
    line-height: 30px!important;
}
table.vgt-table {
    font-size: 14px!important;
}
table.vgt-table .vgt-left-align {
    vertical-align: inherit!important;
}
table.vgt-table .sorting {
    padding-right: 1.5em!important;
}
table.vgt-table .vgt-right-align {
    vertical-align: inherit!important;
}
table.vgt-table th {
    height: 40px;
}
.batch-action-btn {
    margin: 0px!important;
}
</style>
