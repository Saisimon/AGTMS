<template>
    <div class="action-container">
        <b-dropdown variant="primary" right size="sm" 
            :text="$t('action')"
            class="actions-btn">
            <b-dropdown-item 
                v-if="functions.indexOf('view') !== -1"
                href="javascript:void(0);"
                :to="'/manage/main/' + rowData.id"
                class="action-btn" 
                size="sm" >
                <i class="fa fa-fw fa-eye"></i>
                {{ $t('view' )}}
            </b-dropdown-item>
            <b-dropdown-item 
                v-if="functions.indexOf('edit') !== -1"
                href="javascript:void(0);" 
                :to="editUrl" 
                class="action-btn" 
                size="sm" >
                <i class="fa fa-fw fa-edit"></i>
                {{ $t('edit' )}}
            </b-dropdown-item>
            <b-dropdown-item 
                v-if="functions.indexOf('remove') !== -1"
                href="javascript:void(0);" 
                v-b-modal="'remove-model-' + index" 
                class="action-btn text-danger" 
                size="sm" >
                <i class="fa fa-fw fa-trash"></i>
                {{ $t('remove' )}}
            </b-dropdown-item>
        </b-dropdown>
        <b-modal 
            v-if="functions.indexOf('remove') !== -1"
            :id="'remove-model-' + index" 
            centered 
            :cancel-title="$t('cancel')"
            :ok-title="$t('confirm_remove')"
            @ok="remove()"
            ok-variant="danger"
            button-size="sm">
            <div class="text-center font-weight-bold">
                {{ $t('confirm' )}}
            </div>
        </b-modal>
    </div>
</template>

<script>
export default {
    name: "action-cell",
    props:{
        rowData:{
            type: Object
        },
        field:{
            type: String
        },
        index:{
            type: Number
        }
    },
    computed: {
        functions: function() {
            return this.$store.state.list.functions;
        },
        editUrl: function() {
            var url = '/' + this.$route.params.module + '/edit';
            if (this.$route.params.id) {
                url += '/' + this.$route.params.id;
            }
            url += '?id=' + this.rowData.id;
            return url;
        }
    },
    methods: {
        remove: function() {
            this.$store.dispatch('removeData', {
                url: this.$route.path,
                id: this.rowData.id
            }).then(() => {
                this.$emit('on-custom-comp');
                if (this.$route.params.module === 'navigate') {
                    this.$store.dispatch('getTrees');
                }
            });
        }
    }
}
</script>

<style scoped>
.actions-btn {
    vertical-align: baseline!important;
}
.action-btn {
    line-height: 30px;
    font-size: 14px;
}
</style>
