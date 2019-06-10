<template>
    <div :class="{'text-nowrap': nowrap, 'text-wrap': !nowrap}" @click.stop="nowrap=!nowrap">
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
    data: function() {
        return {
            nowrap: true
        }
    }
}
</script>

<style scoped>
.text-nowrap {
    max-width: 200px;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    cursor: pointer;
}
.text-wrap {
    word-wrap: break-word;
    word-break: normal;
    cursor: pointer;
}
</style>