import { editGrid, save } from '@/api/edit'

const state = {
    reset: {},
    templateGrid: {
        table: {
            idx: 0,
            columns: [],
            rows: []
        }
    },
    classOptions: [],
    viewOptions: [],
    whetherOptions: [],
    selectionOptions: []
};

const mutations = {
    setTemplateGrid(state, templateGrid) {
        if (templateGrid) {
            state.templateGrid = templateGrid;
        }
    },
    setClassOptions(state, classOptions) {
        if (classOptions) {
            state.classOptions = classOptions;
        }
    },
    setViewOptions(state, viewOptions) {
        if (viewOptions) {
            state.viewOptions = viewOptions;
        }
    },
    setWhetherOptions(state, whetherOptions) {
        if (whetherOptions) {
            state.whetherOptions = whetherOptions;
        }
    },
    setSelectionOptions(state, selectionOptions) {
        if (selectionOptions) {
            state.selectionOptions = selectionOptions;
        }
    }
};

const actions = {
    getTemplateGrid(context, id) {
        return editGrid(context.rootState.base.user, "/template/edit", id);
    },
    saveTemplate(context, template) {
        return save(context.rootState.base.user, "/template/edit", template);
    }
};

export default {
    state,
    mutations,
    actions
}