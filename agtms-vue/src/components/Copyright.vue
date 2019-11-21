<template>
    <div class="copyright-container">
        <span class="copyright">
            Copyright © 2018-{{year}} <b-link href="https://github.com/Saisimon" target="_blank">Saisimon</b-link>
        </span>
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
    z-index: 699;
    font-size: 12px;
    font-weight: 100;
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background-color: rgba(255, 255, 255, 0.85);
    border-top: 1px solid #ddd;
}
</style>
