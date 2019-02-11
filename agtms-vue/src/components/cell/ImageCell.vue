<template>
    <div class="marquee">
        <img :src="rowData[field]" :alt="rowData[field]" />
    </div>
</template>

<script>
export default {
    name: "image-cell",
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
