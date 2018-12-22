import { list, mainGrid, remove, batchRemove, batchSave } from '@/api/list'

const state = {
    isLoading: true,
    header: {
        title: ''
    },
    breadcrumbs:[],
    selects:[],
    functions: [],
    filters: [],
    titles: [],
    columns: [],
    datas: [],
    total: 0,
    pageable: {
        pageIndex: 1,
        pageSize: 10
    }
};

const mutations = {
    initState(state) {
        state.isLoading = true;
        state.header = {
            title: ''
        };
        state.breadcrumbs = [];
        state.selects = [];
        state.functions = [];
        state.filters = [];
        state.titles = [];
        state.columns = [];
        state.datas = [];
        state.total = 0;
        state.pageable = {
            pageIndex: 1,
            pageSize: 10
        };
    },
    setHeader(state, header) {
        if (header) {
            state.header = header;
        }
    },
    setBreadcrumbs(state, breadcrumbs) {
        if (breadcrumbs) {
            state.breadcrumbs = breadcrumbs;
        }
    },
    setSelects(state, selects) {
        if (selects) {
            state.selects = selects;
        }
    },
    setFunctions(state, functions) {
        if (functions) {
            state.functions = functions;
        }
    },
    setPageIndex(state, pageIndex) {
        if (pageIndex) {
            state.pageable.pageIndex = pageIndex;
        }
    },
    setPageSize(state, pageSize) {
        if (pageSize) {
            state.pageable.pageSize = pageSize;
        }
    },
    setPageable(state, pageable) {
        if (pageable) {
            state.pageable = pageable;
        }
    },
    setSort(state, params) {
        if (params) {
            for (var i in state.columns) {
                var column = state.columns[i];
                if (column.field) {
                    column.orderBy = params[column.field];
                }
            }
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
    setTitles(state, titles) {
        if (titles) {
            state.titles = titles;
        }
    },
    setColumns(state, columns) {
        if (columns) {
            state.columns = columns;
        }
    },
    setDatas(state, datas) {
        if (datas) {
            state.datas = datas.rows;
            state.total = datas.total;
            state.selects = [];
        }
    },
    removeData(state, index) {
        state.datas.splice(index, 1);
        state.total = state.total - 1;
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
        return mainGrid(context.rootState.base.token, url).then(resp => {
            context.commit('setFunctions', resp.data.data.functions);
            context.commit('setPageable', resp.data.data.pageable);
            context.commit('setFilters', resp.data.data.filters);
            context.commit('setHeader', resp.data.data.header);
            context.commit('setBreadcrumbs', resp.data.data.breadcrumbs);
            context.commit('setTitles', resp.data.data.titles);
            context.commit('setColumns', resp.data.data.columns);
        });
    },
    getDatas(context, payload) {
        context.commit('setIsLoading', true);
        return list(context.rootState.base.token, payload).then(resp => {
            context.commit('setIsLoading', false);
            context.commit('setDatas', resp.data);
            context.commit('clearProgress');
        });
    },
    removeData(context, payload) {
        return remove(context.rootState.base.token, payload.url, payload.id);
    },
    batchRemoveData(context, payload) {
        return batchRemove(context.rootState.base.token, payload.url, payload.ids);
    },
    batchEditData(context, payload) {
        return batchSave(context.rootState.base.token, payload.url, payload.data);
    }
};

export default {
    state,
    mutations,
    actions
}