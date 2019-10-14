<template>
    <b-row class="m-0">
        <b-col class="p-0">
            <search-filter-datepicker :input="filter.input" v-if="filter.input.type == 'date'"/>
            <b-form-input :class="field + '-input'" v-model="filter.input.value" v-else-if="filter.input.type == 'long' || filter.input.type == 'double'" :type="'number'" />
            <b-form-input :class="field + '-input'" v-model="filter.input.value" v-else :type="'text'" />
        </b-col>
        <b-col lg="2" class="p-0 filter-select-operator-container">
            <treeselect 
                class="filter-select filter-select-right" 
                :class="field + '-operator-select'"
                v-model="filter.operator.selected" 
                :options="filter.operator.options"
                :multiple="false" 
                :searchable="false"
                :clearable="false"
                :noChildrenText="$t('no_childrens')"
                :noOptionsText="$t('no_options')"
                :noResultsText="$t('no_result')"
                :placeholder="''" />
        </b-col>
    </b-row>
</template>

<script>
import SearchFilterDatepicker from '@/components/filter/SearchFilterDatepicker.vue'

export default {
    name: 'search-filter-text',
    props: ['filter', 'field'],
    components: {
        'search-filter-datepicker': SearchFilterDatepicker,
    }
}
</script>

<style scoped>
.filter-select-operator-container {
    max-width: 150px!important;
}
</style>