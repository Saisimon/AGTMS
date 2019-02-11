<template>
    <div class="marquee">
        <span v-html="rowData[field]"></span>
    </div>
</template>

<script>
export default {
    name: "icon-cell",
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
}
</script>

<style>

</style>
