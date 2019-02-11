<template>
    
</template>

<script>
export default {
    name: 'home',
    created: function() {
        this.$store.commit('clearProgress');
    }
}
</script>
