import { side } from '@/api/navigation'

const state = {
    openTree: false,
    tree: {}
};

const mutations = {
    changeOpenTree(state, status) {
        if (status) {
            state.openTree = true;
        } else {
            state.openTree = false;
        }
    },
    setTree(state, tree) {
        if (tree) {
            state.tree = tree;
        }
    }
};

const actions = {
    changeOpenTree(context, status) {
        context.commit('changeOpenTree', status);
    },
    getTree(context) {
        side(context.rootState.base.user).then(resp => {
            return context.commit('setTree', resp.data.data);
        });
    }
};

export default {
    state,
    mutations,
    actions
}