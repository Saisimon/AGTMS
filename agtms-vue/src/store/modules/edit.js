import { editGrid, save } from '@/api/edit'

const state = {
    reset: {},
    groups: []
};

const mutations = {
    initState(state) {
        state.reset = {};
        state.groups = [];
    },
    setGroups(state, groups) {
        if (groups) {
            state.groups = groups;
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