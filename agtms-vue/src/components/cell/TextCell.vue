<template>
    <div class="ellipsis-text" v-b-tooltip :title="rowData[field]" >
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
