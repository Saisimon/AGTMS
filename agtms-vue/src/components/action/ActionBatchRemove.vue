<template>
    <div class="batch-remove-container">
        <b-modal v-model="model.show"
            centered 
            :cancel-title="$t('cancel')"
            :ok-title="$t('confirm_remove')"
            @ok="batchRemove()"
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
    name: 'action-batch-remove',
    methods: {
        batchRemove() {
            var selects = this.$store.state.list.selects;
            this.$store.dispatch('batchRemoveData', {
                url: this.$route.path,
                ids: selects
            }).then(resp => {
                var data = resp.data;
                if (data.code === 0) {
                    this.$emit('succeed');
                    if (this.$route.params.module === 'navigation') {
                        this.$store.dispatch('getTrees');
                    }
                }
            });
        }
    },
    props: ['model']
}
</script>
