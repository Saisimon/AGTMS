<template>
    <div class="filter-input-group-container">
        <b-input-group-text>
            <span>{{ $t("from") }}</span>
        </b-input-group-text>
        <search-filter-datepicker :input="filter.from" v-if="filter.from.type == 'date'" />
        <b-form-input v-model="filter.from.value" v-else :type="filter.from.type" />
        <b-input-group-text>
            <span>{{ $t("to") }}</span>
        </b-input-group-text>
        <search-filter-datepicker :input="filter.to" v-if="filter.to.type == 'date'" :calendarClass="'filter-datepicker-calendar-right'" />
        <b-form-input v-model="filter.to.value" v-else :type="filter.to.type" />
    </div>
</template>

<script>
import SearchFilterDatepicker from '@/components/filter/SearchFilterDatepicker.vue'

export default {
    name: 'search-filter-range',
    props: ['filter'],
    components: {
        'search-filter-datepicker': SearchFilterDatepicker,
    }
}
</script>

<style scoped>
.filter-input-container {
    position: relative;
    -ms-flex: 1 1 auto;
    -webkit-box-flex: 1;
    flex: 1 1 auto;
    width: 1%;
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
