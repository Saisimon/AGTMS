<template>
    <div class="action-container text-right" v-if="actions">
        <template v-for="(action, idx) in actions">
            <template v-if="action.type == 'link'">
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
            <template v-else-if="action.type == 'download'">
                <b-button :key="idx"
                    :size="'sm'" 
                    :variant="action.variant"
                    v-b-tooltip.hover 
                    :title="action.text"
                    @click="download(action.to)"
                    href="javascript:void(0);" 
                    class="ml-1" >
                    <i class="fa fa-fw" :class="'fa-' + action.icon"></i>
                </b-button>
            </template>
            <template v-else-if="action.type == 'remove'">
                <b-button :key="idx"
                    :size="'sm'" 
                    :variant="action.variant" 
                    v-b-modal="'remove-model-' + index" 
                    v-b-tooltip.hover 
                    :title="action.text"
                    href="javascript:void(0);" 
                    class="ml-1" >
                    <i class="fa fa-fw" :class="'fa-' + action.icon"></i>
                </b-button>
                <b-modal :key="'model-' + idx"
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
        remove: function() {
            this.$store.dispatch('removeData', {
                url: this.$route.path,
                id: this.rowData.id
            }).then(() => {
                this.$emit('succeed');
                if (this.$route.params.module === 'navigation') {
                    this.$store.dispatch('getTrees');
                }
            });
        },
        download: function() {
            this.$store.dispatch('downloadData', {
                url: this.$route.path,
                id: this.rowData.id
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
