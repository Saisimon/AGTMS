<template>
    <div class="home-container">
        <v-chart :options="chart" v-if="Object.keys(statistics).length > 0" />
    </div>
</template>

<script>
import ECharts from 'vue-echarts';

import 'echarts/lib/chart/bar';
import 'echarts/lib/component/tooltip';
import 'echarts/lib/component/title';

export default {
    name: 'home',
    components: {
        'v-chart': ECharts
    },
    created: function() {
        this.$store.commit('setBreadcrumbs', []);
        if (this.$store.state.base.user != null) {
            this.$store.dispatch('getStatistics');
        }
        this.$store.commit('clearProgress');
    },
    computed: {
        statistics: function() {
            return this.$store.state.base.statistics;
        },
        chart: function() {
            var xData = [];
            var data = [];
            var statistics = this.$store.state.base.statistics;
            if (statistics != null) {
                if (statistics.userCount != null) {
                    xData.push(this.$t('user_count'));
                    data.push(statistics.userCount);
                }
                if (statistics.navigationCount != null) {
                    xData.push(this.$t('navigation_count'));
                    data.push(statistics.navigationCount);
                }
                if (statistics.templateCount != null) {
                    xData.push(this.$t('template_count'));
                    data.push(statistics.templateCount);
                }
                if (statistics.selectionCount != null) {
                    xData.push(this.$t('selection_count'));
                    data.push(statistics.selectionCount);
                }
                if (statistics.taskCount != null) {
                    xData.push(this.$t('task_count'));
                    data.push(statistics.taskCount);
                }
                if (statistics.notificationCount != null) {
                    xData.push(this.$t('notification_count'));
                    data.push(statistics.notificationCount);
                }
            }
            return {
                title: {
                    text: this.$t('summary')
                },
                tooltip: {
                    trigger: 'axis'
                },
                xAxis: {
                    data: xData
                },
                yAxis: {},
                series: [{
                    name: this.$t('count'),
                    type: 'bar',
                    data: data
                }],
                color: ['#3c8dbc']
            }
        }
    }
}
</script>

<style>
.echarts {
    max-width: 1000px;
    width: 100%;
    margin: 0 auto;
}
</style>