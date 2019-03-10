<template>
    <div class="filter-input-group-container">
        <multiselect class="filter-select" 
            v-model="filter.select.selected" 
            label="text"
            track-by="value"
            select-label=""
            deselect-label=""
            selected-label=""
            :searchable="filter.searchable"
            :multiple="filter.multiple"
            :options="filter.select.options" 
            :loading="isLoading"
            :placeholder="''"
            @search-change="search" >
            <template slot="noResult">{{ $t("no_result") }}</template>
            <template slot="noOptions">{{ $t("no_options") }}</template>
        </multiselect>
    </div>
</template>

<script>
export default {
    name: 'search-filter-select',
    props: ['filter'],
    mounted: function() {
        if (this.filter.select.options == null || this.filter.select.options.length == 0) {
            this.search();
        }
    },
    data: function() {
        return {
            isLoading: false
        }
    },
    methods: {
        search: function(query) {
            if (this.filter.selectionId == null) {
                return;
            }
            this.isLoading = true;
            this.$store.dispatch('searchSelection', {
                id: this.filter.selectionId,
                keyword: query
            }).then(resp => {
                if (resp.data.code === 0) {
                    var options = resp.data.data;
                    this.filter.select.options = options;
                }
                this.isLoading = false;
            });
        }
    }
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
