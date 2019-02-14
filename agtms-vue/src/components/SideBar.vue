<template>
    <div id="sidebar" class="main-sidebar">
        <div class="side-bar-search-container">
            <b-form-input type="text" :placeholder="$t('search')" class="side-bar-search" v-model="search" />
        </div>
        <ul class="sidebar-menu" @click="openBars">
            <tree-view
                v-for="(tree, index) in menuTree.childrens"
                :key="'tree' + index"
                :tree="tree">
            </tree-view>
            <li v-for="(link, index) in menuTree.links"
                :key="'link' + index">
                <router-link :to="link.link" v-b-toggle="'nav-bar'">
                    <i class="fa fa-fw fa-link text-light"></i>
                    <span class="text-light ml-1">{{link.name}}</span>
                </router-link>
            </li>
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
        menuTree: function() {
            return this.filterTree(this.search.toLowerCase(), this.$store.state.navigation.tree);
        }
    },
    methods: {
        filterTree: function(search, tree) {
            var menuTree = {};
            if (tree.id == undefined) {
                return menuTree;
            }
            var needPush = false;
            for (var key in tree) {
                var value = tree[key];
                if (key == "childrens" && value && value.length > 0) {
                    var childrenMenuTrees = new Array();
                    for (var i = 0; i < value.length; i++) {
                        var childrenMenuTree = this.filterTree(search, value[i]);
                        if (childrenMenuTree) {
                            childrenMenuTrees.push(childrenMenuTree);
                        }
                    }
                    menuTree[key] = childrenMenuTrees;
                } else if (key == "links" && value && value.length > 0) {
                    var menuLinks = new Array();
                    for (var i = 0; i < value.length; i++) {
                        var menuLink = {};
                        var link = value[i];
                        if (link["name"].toLowerCase().indexOf(search) !== -1) {
                            needPush = true;
                            menuLink["name"] = link["name"];
                            menuLink["link"] = link["link"];
                            menuLinks.push(menuLink);
                        }
                    }
                    menuTree[key] = menuLinks;
                } else {
                    menuTree[key] = value;
                }
            }
            if (tree.id == -1 || tree.title.toLowerCase().indexOf(search) !== -1 || needPush) {
                return menuTree;
            } else {
                return null;
            }
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
