import { list, mainGrid, batchGrid, batchRemove, batchSave, batchExport, batchImport } from '@/api/list'
import request from '@/api/request'

const state = {
    isLoading: true,
    header: {
        title: ''
    },
    functions: [],
    showFilters: false,
    filters: [],
    columns: [],
    actions: null,
    batchEdit: null,
    batchExport: null,
    batchImport: null,
    datas: []
};

const mutations = {
    initState(state) {
        state.isLoading = true;
        state.header = {
            title: ''
        };
        state.functions = [];
        state.showFilters = false;
        state.filters = [];
        state.columns = [];
        state.actions = null;
        state.batchEdit = null;
        state.batchExport = null;
        state.batchImport = null;
        state.datas = [];
    },
    setHeader(state, header) {
        if (header) {
            state.header = header;
        }
    },
    setFunctions(state, functions) {
        if (functions) {
            state.functions = functions;
        }
    },
    setSort(state, params) {
        if (params) {
            for (var i in params) {
                var param = params[i];
                for (var j in state.columns) {
                    var column = state.columns[j];
                    if (column.field && column.field == param.field) {
                        column.orderBy = param.type;
                    }
                }
            }
        }
    },
    clearSort(state) {
        for (var i in state.columns) {
            var column = state.columns[i];
            column.orderBy = null;
        }
    },
    setShowFilters(state, showFilters) {
        if (showFilters) {
            state.showFilters = showFilters;
        }
    },
    setFilters(state, filters) {
        if (filters) {
            state.filters = filters;
        }
    },
    clearFilters(state) {
        for (var i in state.filters) {
            var filter = state.filters[i];
            var value = filter.value;
            for (var fieldName in value) {
                var fieldFilter = value[fieldName];
                switch (fieldFilter.type) {
                    case 'range':
                        fieldFilter.from.value = "";
                        fieldFilter.to.value = "";
                        break;
                    case 'select':
                        if (fieldFilter.multiple) {
                            fieldFilter.select.selected = [];
                        } else {
                            fieldFilter.select.selected = "";
                        }
                        break;
                    default:
                        fieldFilter.input.value = "";
                        break;
                }
            }
        }
    },
    setColumns(state, columns) {
        if (columns) {
            state.columns = columns;
        }
    },
    setActions(state, actions) {
        if (actions) {
            state.actions = actions;
        }
    },
    setBatchEdit(state, batchEdit) {
        if (batchEdit && batchEdit.editFieldOptions) {
            for (var i = 0; i < batchEdit.editFieldOptions.length; i++) {
                var editFieldOption = batchEdit.editFieldOptions[i];
                if (editFieldOption.disable) {
                    editFieldOption["$isDisabled"] = true;
                }
            }
            state.batchEdit = batchEdit;
        }
    },
    setBatchExport(state, batchExport) {
        if (batchExport && batchExport.exportFieldOptions) {
            state.batchExport = batchExport;
        }
    },
    setBatchImport(state, batchImport) {
        if (batchImport && batchImport.importFieldOptions) {
            state.batchImport = batchImport;
        }
    },
    setDatas(state, datas) {
        if (datas) {
            state.datas = datas.rows;
            state.selects = [];
        }
    },
    setIsLoading(state, isLoading) {
        if (isLoading && isLoading === true) {
            state.isLoading = true;
        } else {
            state.isLoading = false;
        }
    }
};

const actions = {
    getMainGrid(context, url) {
        return mainGrid(context.rootState.base.user, url);
    },
    getDatas(context, payload) {
        context.commit('setIsLoading', true);
        return list(context.rootState.base.user, payload).then(resp => {
            context.commit('setIsLoading', false);
            context.commit('setDatas', resp.data);
        });
    },
    requestUrl(context, url) {
        return request(context.rootState.base.user, url);
    },
    getBatchGrid(context, url) {
        return batchGrid(context.rootState.base.user, url).then(resp => {
            context.commit('setBatchEdit', resp.data.data.batchEdit);
            context.commit('setBatchExport', resp.data.data.batchExport);
            context.commit('setBatchImport', resp.data.data.batchImport);
        });
    },
    batchRemoveData(context, payload) {
        return batchRemove(context.rootState.base.user, payload.url, payload.ids);
    },
    batchEditData(context, payload) {
        return batchSave(context.rootState.base.user, payload.url, payload.data);
    },
    batchExportData(context, payload) {
        return batchExport(context.rootState.base.user, payload.url, payload.data);
    },
    batchImportData(context, payload) {
        return batchImport(context.rootState.base.user, payload.url, payload.data);
    }
};

export default {
    state,
    mutations,
    actions
}