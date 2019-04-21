<template>
    <div class="marquee">
        <i class="fa" :class="'fa-' + rowData[field]"></i>
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
