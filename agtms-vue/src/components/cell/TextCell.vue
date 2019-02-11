<template>
    <div class="marquee">
        <span>{{ rowData[field] }}</span>
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
