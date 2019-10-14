<template>
    <b-row class="m-0">
        <b-col class="p-0" md="auto">
            <b-input-group-text>
                <span>{{ $t("from") }}</span>
            </b-input-group-text>
        </b-col>
        <b-col class="p-0">
            <search-filter-datepicker :class="field + '-from-input'" :input="filter.from" v-if="filter.from.type == 'date'" />
            <b-form-input :class="field + '-from-input'" v-model="filter.from.value" v-else-if="filter.from.type == 'long' || filter.from.type == 'double'" :type="'number'" />
            <b-form-input :class="field + '-from-input'" v-model="filter.from.value" v-else :type="'text'" />
        </b-col>
        <b-col class="p-0" md="auto">
            <b-input-group-text>
                <span>{{ $t("to") }}</span>
            </b-input-group-text>
        </b-col>
        <b-col class="p-0">
            <search-filter-datepicker :class="field + '-to-input'" :input="filter.to" v-if="filter.to.type == 'date'" :calendarClass="'filter-datepicker-calendar-right'" />
            <b-form-input :class="field + '-to-input'" v-model="filter.to.value" v-else-if="filter.to.type == 'long' || filter.to.type == 'double'" :type="'number'" />
            <b-form-input :class="field + '-to-input'" v-model="filter.to.value" v-else :type="'text'" />
        </b-col>
    </b-row>
</template>

<script>
import SearchFilterDatepicker from '@/components/filter/SearchFilterDatepicker.vue'

export default {
    name: 'search-filter-range',
    props: ['filter', 'field'],
    components: {
        'search-filter-datepicker': SearchFilterDatepicker,
    }
}
</script>