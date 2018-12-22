<template>
    <div class="row">
        <div class="col-md-12">
            <div class="error-template">
                <h1>Oops!</h1>
                <h2>404 Not Found</h2>
                <div class="error-actions">
                    <router-link to="/">
                        <span class="fa fa-fw fa-home mr-2"></span>
                        <span>{{ $t('index') }}</span>
                    </router-link>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'not-found',
    created: function() {
        this.$store.commit('clearProgress');
    }
}
</script>

<style scoped>
.error-template {
    padding: 40px 15px;
    text-align: center;
}
.error-actions {
    margin-top:15px;
    margin-bottom:15px;
}
</style>
