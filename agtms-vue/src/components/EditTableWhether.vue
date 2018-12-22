<template>
    <multiselect class="filter-select" 
        v-model="selected"
        label="text"
        track-by="value"
        select-label=""
        deselect-label=""
        selectedLabel=""
        :allow-empty="false"
        :searchable="false"
        :options="options"
        :placeholder="''" />
</template>

<script>
export default {
    name: "edit-table-whether",
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
        var options = [
            { value: 0, text: '否' },
            { value: 1, text: '是' }
        ]
        var selected = options[0];
        for (var i in options) {
            if (options[i].value === this.rowData[this.field]) {
                selected = options[i];
                break;
            }
        }
        return {
            selected: selected,
            options: options
        }
    }
}
</script>
<style>
</style>
