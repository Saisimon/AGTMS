import { editGrid, save } from '@/api/edit'

const state = {
    reset: {},
    breadcrumbs: [],
    fields: []
};

const mutations = {
    initReset(state, reset) {
        if (reset) {
            state.reset = reset;
        }
    },
    setBreadcrumbs(state, breadcrumbs) {
        if (breadcrumbs) {
            state.breadcrumbs = breadcrumbs;
        }
    },
    setFields(state, fields) {
        if (fields) {
            state.fields = fields;
        }
    }
};

const actions = {
    getEditGrid(context, payload) {
        return editGrid(context.rootState.base.token, payload.url, payload.id);
    },
    saveData(context, payload) {
        return save(context.rootState.base.token, payload.url, payload.data);
    }
};

export default {
    state,
    mutations,
    actions
}