import { side } from '@/api/navigation'

const state = {
    openTree: false,
    trees: []
};

const mutations = {
    changeOpenTree(state, status) {
        if (status) {
            state.openTree = true;
        } else {
            state.openTree = false;
        }
    },
    setTrees(state, trees) {
        if (trees) {
            state.trees = trees;
        }
    }
};

const actions = {
    changeOpenTree(context, status) {
        context.commit('changeOpenTree', status);
    },
    getTrees(context) {
        side(context.rootState.base.user).then(resp => {
            return context.commit('setTrees', resp.data.data);
        });
    }
};

export default {
    state,
    mutations,
    actions
}