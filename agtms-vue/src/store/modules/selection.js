import { editGrid, save } from '@/api/edit'
import request from '@/api/request'

const state = {
    reset: {},
    selectionGrid: {

    }
};

const mutations = {
    setSelectionGrid(state, selectionGrid) {
        if (selectionGrid) {
            state.selectionGrid = selectionGrid;
        }
    },
};

const actions = {
    getSelectionGrid(context, id) {
        return editGrid(context.rootState.base.user, "/selection/edit", id);
    },
    getSelectionTemplate(context, id) {
        var url = "/selection/edit/template?id=" + id;
        return request(context.rootState.base.user, url, null);
    },
    searchSelection(context, search) {
        if (search == null || search.sign == null) {
            return;
        }
        var url = "/selection/edit/search?sign=" + search.sign;
        if (search.keyword) {
            url += "&keyword=" + search.keyword;
        }
        return request(context.rootState.base.user, url);
    },
    saveSelection(context, selection) {
        return save(context.rootState.base.user, "/selection/edit", selection);
    }
};

export default {
    state,
    mutations,
    actions
}