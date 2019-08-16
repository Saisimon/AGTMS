<template>
    <div class="action-container text-right d-flex justify-content-end" v-if="actions">
        <template v-for="(action, idx) in actions">
            <template v-if="action.type == 'link' && (!rowData.disableActions || !rowData.disableActions[idx])">
                <b-button :key="idx"
                    :size="'sm'" 
                    :variant="action.variant" 
                    :to="action.to + rowData.id"
                    v-b-tooltip.hover 
                    :title="action.text"
                    class="ml-1" >
                    <i class="fa fa-fw" :class="'fa-' + action.icon"></i>
                </b-button>
            </template>
            <template v-else-if="action.type == 'download' && (!rowData.disableActions || !rowData.disableActions[idx])">
                <b-button :key="idx"
                    :size="'sm'" 
                    :variant="action.variant"
                    v-b-tooltip.hover 
                    :title="action.text"
                    :href="$store.state.base.urlPrefix + action.to + '?id=' + rowData.id + '&uuid=' + rowData.uuid" 
                    target="_blank"
                    class="ml-1" >
                    <i class="fa fa-fw" :class="'fa-' + action.icon"></i>
                </b-button>
            </template>
            <template v-else-if="action.type == 'modal' && (!rowData.disableActions || !rowData.disableActions[idx])">
                <b-button :key="idx"
                    :size="'sm'" 
                    :variant="action.variant" 
                    v-b-modal="'modal-' + idx + '-' + index" 
                    v-b-tooltip.hover 
                    :title="action.text"
                    href="javascript:void(0);" 
                    class="ml-1" >
                    <i class="fa fa-fw" :class="'fa-' + action.icon"></i>
                </b-button>
                <b-modal :key="'modal-' + idx"
                    :id="'modal-' + idx + '-' + index" 
                    centered 
                    :cancel-title="$t('cancel')"
                    :ok-title="$t('confirm')"
                    @ok="modal(action.to)"
                    cancel-variant="outline-info"
                    ok-variant="outline-danger"
                    header-border-variant="light"
                    footer-border-variant="light"
                    button-size="sm">
                    <div class="text-center">
                        {{ $t('are_you_confirm' )}}
                    </div>
                </b-modal>
            </template>
        </template>
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
        },
        actions: {
            type: Array
        }
    },
    methods: {
        modal: function(link) {
            this.$store.dispatch('requestUrl', link + "?id=" + this.rowData.id).then(() => {
                if (this.$route.params.module === 'navigation' || this.$route.params.module === 'template') {
                    this.$store.dispatch('getTree');
                }
                this.$emit('succeed');
            });
        }
    }
}
</script>

<style scoped>
.action-container {
    min-width: 150px;
}
.actions-btn {
    vertical-align: baseline!important;
}
.action-btn {
    line-height: 30px;
    font-size: 14px;
}
</style>
