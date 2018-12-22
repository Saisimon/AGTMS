<template>
    <div id="sidebar" class="main-sidebar">
        <div class="side-bar-search-container">
            <b-form-input type="text" :placeholder="$t('search')" class="side-bar-search" v-model="search" />
        </div>
        <ul class="sidebar-menu" @click="openBars">
            <tree-view
                v-for="(tree, index) in menuTrees"
                :key="index"
                :tree="tree">
            </tree-view>
        </ul>
    </div>
</template>

<script>
import TreeView from '@/components/TreeView.vue'

export default {
    name: 'side-bar',
    data: function() {
        return {
            search: ''
        }
    },
    computed: {
        menuTrees: function() {
            return this.filterTrees(this.search.toLowerCase(), this.$store.state.navigate.trees);
        }
    },
    methods: {
        filterTrees: function(search, trees) {
            var menuTrees = new Array();
            for (var index in trees) {
                var tree = trees[index];
                var menuTree = {};
                var needPush = false;
                for (var key in tree) {
                    var value = tree[key];
                    if (key == "childrens" && value && value.length > 0) {
                        var childrenMenuTrees = this.filterTrees(search, tree.childrens);
                        if (childrenMenuTrees.length > 0) {
                            needPush = true;
                        }
                        menuTree[key] = childrenMenuTrees;
                    } else if (key == "linkMap" && value) {
                        var menuLinkMap = {};
                        for (var link in value) {
                            if (value[link].toLowerCase().indexOf(search) !== -1) {
                                needPush = true;
                                menuLinkMap[link] = value[link];
                            }
                        }
                        menuTree[key] = menuLinkMap;
                    } else {
                        menuTree[key] = value;
                    }
                }
                if (tree.title.toLowerCase().indexOf(search) !== -1 || needPush) {
                    menuTrees.push(menuTree);
                }
            }
            return menuTrees;
        },
        openBars: function() {
            this.$store.commit('changeOpenTree', true);
        }
    },
    components: {
        'tree-view': TreeView
    }
}
</script>

<style scoped>
@media (min-width: 576px) {
    .main-sidebar {
        background-color: #222d32;
        position: fixed;
        top: 50px;
        bottom: 0px;
        left: 0px;
        width: 250px;
        z-index: 700;
        -webkit-transition: -webkit-transform .3s ease-in-out,width .3s ease-in-out;
        -moz-transition: -moz-transform .3s ease-in-out,width .3s ease-in-out;
        -o-transition: -o-transform .3s ease-in-out,width .3s ease-in-out;
        transition: transform .3s ease-in-out,width .3s ease-in-out;
    }
    .side-bar-search-container .side-bar-search {
        color: #fff;
        border-radius: 3px;
        box-shadow: none;
        background-color: #374850;
        border: 0px;
        border-right: 1px solid #374850;
    }
    .side-bar-search:focus {
        border: 0px;
        box-shadow: none;
    }
    .main-navbar-collapse .main-sidebar {
        width: 50px;
    }
    .main-navbar-collapse .main-sidebar .side-bar-search-container {
        display: none;
    }
}
.sidebar-menu {
    white-space: nowrap;
    list-style: none;
    margin: 0;
    padding: 0;
}
.main-sidebar {
    overflow-x: hidden;
    overflow-y: auto;
}
.main-sidebar::-webkit-scrollbar-thumb {
    background-color: #848f94;
}
.side-bar-search-container {
    padding: 10px;
}
</style>
