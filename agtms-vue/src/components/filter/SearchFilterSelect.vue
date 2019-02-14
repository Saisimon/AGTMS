<template>
    <div class="filter-input-group-container">
        <multiselect class="filter-select" 
            v-model="filter.select.selected" 
            label="text"
            track-by="value"
            select-label=""
            deselect-label=""
            selected-label=""
            :multiple="filter.multiple"
            :options="filter.select.options" 
            :placeholder="''" />
    </div>
</template>

<script>
export default {
    name: 'search-filter-select',
    props: ['filter']
}
</script>

<style scoped>
.filter-input-group-container {
    display: -ms-flexbox;
    display: -webkit-box;
    display: flex;
    -ms-flex: 1 1 auto;
    -webkit-box-flex: 1;
    flex: 1 1 auto;
}
</style>
