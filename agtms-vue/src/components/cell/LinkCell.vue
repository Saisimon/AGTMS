<template>
    <div>
        <a :href="rowData[field]" target="_blank">{{ rowData[field] }}</a>
    </div>
</template>

<script>
export default {
    name: "link-cell",
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
