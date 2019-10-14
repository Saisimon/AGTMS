<template>
    <treeselect 
        class="filter-select"
        :class="field + '-select'" 
        v-model="filter.select.selected" 
        :options="filter.select.options" 
        :multiple="filter.multiple"
        :searchable="filter.searchable"
        :noChildrenText="$t('no_childrens')"
        :noOptionsText="$t('no_options')"
        :noResultsText="$t('no_result')"
        :placeholder="''" />
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