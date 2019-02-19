import { editGrid, save } from '@/api/edit'

const state = {
    reset: {},
    fields: []
};

const mutations = {
    setFields(state, fields) {
        if (fields) {
            state.fields = fields;
        }
    }
};

const actions = {
    getEditGrid(context, payload) {
        return editGrid(context.rootState.base.user, payload.url, payload.id);
    },
    saveData(context, payload) {
        return save(context.rootState.base.user, payload.url, payload.data);
    }
};

export default {
    state,
    mutations,
    actions
}