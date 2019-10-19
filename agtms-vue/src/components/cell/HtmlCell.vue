<template>
    <div v-html="rowData[field]"></div>
</template>

<script>
export default {
    name: "html-cell",
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
