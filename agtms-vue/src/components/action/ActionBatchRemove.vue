<template>
    <div class="batch-remove-container">
        <b-modal v-model="modal.show"
            centered 
            :cancel-title="$t('cancel')"
            :ok-title="$t('confirm')"
            @ok="batchRemove()"
            cancel-variant="outline-info"
            ok-variant="outline-danger"
            header-border-variant="light"
            footer-border-variant="light"
            button-size="sm">
            <div class="text-center">
                {{ $t('are_you_confirm' )}}
            </div>
        </b-modal>
    </div>
</template>

<script>
export default {
    name: 'action-batch-remove',
    methods: {
        batchRemove() {
            this.$store.dispatch('batchRemoveData', {
                url: this.$route.path,
                ids: this.selects
            }).then(resp => {
                var data = resp.data;
                if (data.code === 0) {
                    this.$emit('succeed');
                    if (this.$route.params.module === 'navigation' || this.$route.params.module === 'template') {
                        this.$store.dispatch('getTree');
                    }
                } else {
                    this.$emit('failed');
                }
            });
        }
    },
    props: [
        'modal',
        "selects",
    ]
}
</script>
