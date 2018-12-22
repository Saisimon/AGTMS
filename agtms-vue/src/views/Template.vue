<template>
    <div>
        <b-breadcrumb :items="breadcrumbs" />
        <b-card header-tag="header" footer-tag="footer">
            <!-- 头部 -->
            <b-row slot="header">
                <b-col sm="3" @click="showTitleInput=true">
                    <b-form-input size="sm" v-model="template.title" v-if="showTitleInput" @blur.native.capture="showTitleInput=false" />
                    <span v-else >{{ template.title }}</span>
                </b-col>
                <b-col sm="9" class="text-right">
                    <b-button variant="primary" size="sm">
                        <i class="fa fa-fw fa-plus-circle"></i>
                        {{ $t("add_column") }}
                    </b-button>
                </b-col>
            </b-row>
            <!-- 表单 -->
            <div class="form-container">
                <v-table id="templateEditTable"
                    is-horizontal-resize
                    style="width:100%;margin-bottom:20px;"
                    :columns="columns"
                    :table-data="tableData"
                    row-hover-color="#eee"
                    row-click-color="#edf7ff"
                    :cell-edit-done="cellEditDone" />
            </div>
            <!-- 尾部 -->
            <b-row slot="footer">
                <b-col class="text-right">
                    <!-- 返回 -->
                    <b-button 
                        :to="'/template/main'"
                        variant="secondary" 
                        size="sm" 
                        class="ml-2">
                        <i class="fa fa-fw fa-undo"></i>
                        {{ $t("back") }}
                    </b-button>
                    <!-- 重置 -->
                    <b-button variant="danger" size="sm" class="ml-2">
                        <i class="fa fa-fw fa-repeat"></i>
                        {{ $t("reset") }}
                    </b-button>
                    <!-- 保存 -->
                    <b-button variant="primary" size="sm" class="ml-2">
                        <i class="fa fa-fw fa-save"></i>
                        {{ $t("save") }}
                    </b-button>
                </b-col>
            </b-row>
        </b-card>
    </div>
</template>

<script> 
import Vue from 'vue'
import EditTableWhether from '@/components/EditTableWhether.vue'

export default {
    name: 'template-edit',
    created: function() {
        this.$store.commit('clearProgress');
    },
    data: function() {
        return {
            breadcrumbs: [
                {
                    to: '/',
                    text: this.$t('system_module')
                }, {
                    to: '/template/main',
                    text: this.$t('template_management'),
                }, {
                    text: this.$route.query.id ? this.$t('edit') : this.$t('create'),
                    active: true,
                }
            ],
            template: {
                title: this.$t('input_title')
            },
            showTitleInput: false,
            tableData: [
                { 
                    'id': 'column0field0', 
                    'columnName': '', 
                    'fieldName': '', 
                    'fieldType': 0, 
                    'showType': 0, 
                    'filter': 0, 
                    'sort': 0, 
                    'required': 0, 
                    'unique': 0, 
                    'default': '', 
                    'width': 0 
                },
                { 
                    'id': 'column1field0', 
                    'columnName': '', 
                    'fieldName': '', 
                    'fieldType': 0, 
                    'showType': 0, 
                    'filter': 0, 
                    'sort': 0, 
                    'required': 0, 
                    'unique': 0, 
                    'default': '', 
                    'width': 0 
                },
                { 
                    'id': 'column2field0', 
                    'columnName': '', 
                    'fieldName': '', 
                    'fieldType': 0, 
                    'showType': 0, 
                    'filter': 0, 
                    'sort': 0, 
                    'required': 0, 
                    'unique': 0, 
                    'default': '', 
                    'width': 0 
                },
            ],
            columns: [
                { field: 'id', title: 'ID', width: 100, columnAlign:'center',isResize:true },
                { field: 'columnName', title: '列名', width: 100, columnAlign:'center', isEdit:true, isResize:true },
                { field: 'fieldName', title: '字段名', width: 100, columnAlign:'center', isEdit:true, isResize:true },
                { field: 'fieldType', title: '字段类型', width: 100, columnAlign:'center', isResize:true },
                { field: 'showType', title: '显示类型', width: 100, columnAlign:'center', isResize:true },
                { field: 'filter', title: '筛选', width: 100, columnAlign:'center', isResize:true, componentName: 'edit-table-whether' },
                { field: 'sort', title: '排序', width: 100, columnAlign:'center', isResize:true, componentName: 'edit-table-whether' },
                { field: 'required', title: '必填', width: 100, columnAlign:'center', isResize:true, componentName: 'edit-table-whether' },
                { field: 'unique', title: '唯一', width: 100, columnAlign:'center', isResize:true, componentName: 'edit-table-whether' },
                { field: 'default', title: '默认值', width: 100, columnAlign:'center', isEdit:true, isResize:true },
                { field: 'width', title: '宽度', width: 100, columnAlign:'center', isResize:true },
                { field: 'remove', title: '删除', width: 100, columnAlign:'center', isResize:true }
            ]
        }
    },
    methods: {
        cellEditDone: function(newValue, oldValue, rowIndex, rowData, field) {
            this.tableData[rowIndex][field] = newValue;
        },
        fieldTypeFormatter: function(rowData, rowIndex, pagingIndex, field) {

        },
        showTypeFormatter: function(rowData, rowIndex, pagingIndex, field) {

        }
    }
}
Vue.component('edit-table-whether', EditTableWhether);
</script>

<style>
#templateEditTable .v-table-body-cell {
    padding: 0px !important;
}
</style>
