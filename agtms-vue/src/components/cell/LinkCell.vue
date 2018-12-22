<template>
    <div class="marquee">
        <a :href="rowData[field]" target="_blank">{{ rowData[field] }}</a>
    </div>
</template>

<script>
export default {
    name: "text-cell",
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
