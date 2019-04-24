<template>
    <div class="filter-input-group-container">
        <multiselect class="filter-select"
            :class="field + '-select'" 
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
export function debounce(fn, t) {
    var delay = t || 500;
    var timer;
    return function() {
        var args = arguments;
        if (timer) {
            clearTimeout(timer);
        }
        timer = setTimeout(() => {
            timer = null;
            fn.apply(this, args);
        }, delay);
    }
}

export default {
    name: 'search-filter-select',
    props: ['filter', 'field'],
    data: function() {
        return {
            isLoading: false
        }
    },
    methods: {
        search: debounce(function(query) {
            if (this.filter.sign == null) {
                return;
            }
            this.isLoading = true;
            this.$store.dispatch('searchSelection', {
                sign: this.filter.sign,
                keyword: query
            }).then(resp => {
                if (resp.data.code === 0) {
                    var options = resp.data.data;
                    this.filter.select.options = options;
                }
                this.isLoading = false;
            });
        }, 500)
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
