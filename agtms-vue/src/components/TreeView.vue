<template>
    <li class="treeview" :class="{'menu-open': hasMenuOpen}" :key=tree.id>
        <a href="javascript:void(0);" @click="openMenu" :title="tree.title">
            <i class="fa fa-fw text-light" :class="'fa-' + tree.icon"></i>
            <span class="text-light ml-1">{{tree.title}}</span>
            <span class="pull-right-container">
                <i class="fa fa-angle-left text-light pull-right"></i>
            </span>
        </a>
        <transition name="open-menu">
            <ul class="treeview-menu" v-if="hasMenuOpen">
                <tree-view 
                    v-for="children in tree.childrens"
                    :key="children.id"
                    :tree="children">
                </tree-view>
                <li v-for="(value, key) in tree.linkMap"
                    :key="key">
                    <router-link :to="key" v-b-toggle="'nav-bar'">
                        <i class="fa fa-fw fa-link text-light"></i>
                        <span class="text-light ml-1">{{value}}</span>
                    </router-link>
                </li>
            </ul>
        </transition>
    </li>
</template>

<script>
export default {
    name: 'tree-view',
    props: ['tree'],
    data: function() {
        return {
            menuOpen: false
        }
    },
    computed: {
        hasMenuOpen: function() {
            return this.menuOpen && this.$store.state.navigate.openTree;
        }
    },
    methods: {
        openMenu: function() {
            if (this.$store.state.navigate.openTree) {
                this.menuOpen = !this.menuOpen;
            }
        }
    }
}
</script>

<style>
@media (min-width: 576px) {
    .treeview-menu {
        background-color: #2c3b41;
    }
    .main-navbar-collapse .main-sidebar span {
        display: none;
    }
}
.sidebar-menu>li, .treeview-menu>li {
    position: relative;
    margin: 0;
    padding: 0;
}
.sidebar-menu>li>a, .treeview-menu>li>a {
    padding: 10px 5px 10px 15px;
    display: block;
    color: #fff;
}
.sidebar-menu>li>a:hover, .treeview-menu>li>a:hover {
    background: rgba(0,0,0,0.1);
    border-left: 4px solid #fff;
    outline: none;
    text-decoration: none;
}
.sidebar-menu .treeview-menu {
    display: none;
    list-style: none;
    padding: 0;
    margin: 0;
    padding-left: 10px;
}
.sidebar-menu .menu-open .treeview-menu {
    display: block;
}
.sidebar-menu li>a>.fa-angle-left, .sidebar-menu li>a>.pull-right-container>.fa-angle-left {
    width: auto;
    height: auto;
    padding: 0;
    margin-right: 10px;
    -webkit-transition: transform .5s ease;
    -o-transition: transform .5s ease;
    transition: transform .5s ease;
}
.sidebar-menu .menu-open>a>.fa-angle-left, .sidebar-menu .menu-open>a>.pull-right-container>.fa-angle-left {
    -webkit-transform: rotate(-90deg);
    -ms-transform: rotate(-90deg);
    -o-transform: rotate(-90deg);
    transform: rotate(-90deg);
}
.open-menu-enter-active, .open-menu-leave-active {
    transition: all .5s;
}
.open-menu-enter, .open-menu-leave-to{
    opacity: 0;
    transform: translateX(-100px);
}
</style>
