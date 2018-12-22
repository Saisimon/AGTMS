<template>
    <div class="copyright-container">
        <span class="copyright">Copyright © saisimon.net {{year}}</span>
    </div>
</template>

<script>
export default {
    name: 'copy-right',
    computed: {
        year: function() {
            return new Date().getFullYear();
        }
    }
}
</script>

<style scoped>
.copyright-container {
    text-align: center;
    width: 100%;
    height: 30px;
    line-height: 30px;
    z-index: 1000px;
    font-size: 14px;
}
</style>
