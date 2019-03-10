<template>
    <div class="filter-input-group-container">
        <search-filter-datepicker :input="filter.input" v-if="filter.input.type == 'date'"/>
        <b-form-input v-model="filter.input.value" v-else-if="filter.input.type == 'long' || filter.input.type == 'double'" :type="'number'" />
        <b-form-input v-model="filter.input.value" v-else :type="'text'" />
        <b-input-group-text slot="append" class="filter-select-container">
            <multiselect class="filter-select filter-select-right" 
                v-model="filter.operator.selected" 
                :options="filter.operator.options"
                label="text"
                track-by="value"
                select-label=""
                deselect-label=""
                selected-label=""
                :allow-empty="false"
                :searchable="false"
                :placeholder="''" >
                <template slot="noResult">{{ $t("no_result") }}</template>
                <template slot="noOptions">{{ $t("no_options") }}</template>
            </multiselect>
        </b-input-group-text>
    </div>
</template>

<script>
import SearchFilterDatepicker from '@/components/filter/SearchFilterDatepicker.vue'

export default {
    name: 'search-filter-text',
    props: ['filter'],
    components: {
        'search-filter-datepicker': SearchFilterDatepicker,
    }
}
</script>

<style scoped>
.filter-select-container {
    padding: 0;
    border: 0;
}
.filter-input-group-container {
    display: -ms-flexbox;
    display: -webkit-box;
    display: flex;
    -ms-flex: 1 1 auto;
    -webkit-box-flex: 1;
    flex: 1 1 auto;
}
</style>
