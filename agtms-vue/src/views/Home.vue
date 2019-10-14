<template>
    <div class="home-container">
        
    </div>
</template>

<script>
export default {
    name: 'home',
    created: function() {
        this.$store.commit('setBreadcrumbs', []);
        this.$store.commit('clearProgress');
    }
}
</script>
