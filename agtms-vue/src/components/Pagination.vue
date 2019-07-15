<template>
    <div id="pagination" class="pagination-container">
        <b-row>
            <b-col class="pagination-label-container text-left pr-0" 
                :class="{'disabled': pageIndex < 2}"
                @click="previous">
                <i class="fa fa-angle-left" aria-hidden="true"></i>
                <span class="pl-1">{{ $t('previous_page') }}</span>
            </b-col>
            <b-col class="text-center p-0" cols="6">
                <span class="pagination-page-select-label">{{ $t('rows_pre_page') }}</span>
                <b-form-select class="pagination-page-select" 
                    v-model="pageSize" 
                    :options="options" 
                    @change="pageSizeChange"
                    size="sm"></b-form-select>
            </b-col>
            <b-col class="pagination-label-container text-right pl-0" 
                :class="{'disabled': datas == null || datas.length < pageSize}"
                @click="next">
                <span class="pr-1">{{ $t('next_page') }}</span>
                <i class="fa fa-angle-right" aria-hidden="true"></i>
            </b-col>
        </b-row>
    </div>
</template>

<script>
export default {
    name: 'pagination',
    props: {
        pageIndex: {
            type: Number,
            default: 1
        },
        pageSize: {
            type: Number,
            default: 10
        }
    },
    data: function() {
        return {
            isLoading: false,
            options: [
                { "value": 10, "text": 10 },
                { "value": 20, "text": 20 },
                { "value": 50, "text": 50 }
            ]
        }
    },
    computed: {
        datas: function() {
            return this.$store.state.list.datas;
        }
    },
    methods: {
        previous: function() {
            if (this.pageIndex <= 1) {
                return;
            }
            if (this.datas == null) {
                return;
            }
            var param = null;
            if (this.datas.length > 0) {
                param = {
                    key: 'id',
                    value: this.datas[0].id,
                    operator: '$gt'
                };
            }
            this.$emit('on-previous', param);
        },
        next: function() {
            if (this.datas == null || this.datas.length < this.pageSize) {
                return;
            }
            var last = this.datas[this.datas.length - 1];
            this.$emit('on-next', {
                key: 'id',
                value: last.id,
                operator: '$lt'
            });
        },
        pageSizeChange: function(pageSize) {
            this.$emit('on-per-page-change', {currentPerPage : pageSize});
        }
    }
}
</script>

<style scoped>
.pagination-container {
    width: 100%;
    height: 50px;
    line-height: 50px;
}
.pagination-label-container {
    cursor: pointer;
    color: #3c8dbc;
    font-size: 16px;
}
.pagination-label-container:hover {
    color: #2d688a;
}
.pagination-page-select {
    width: 60px;
}
.pagination-page-select-label {
    padding-right: 6px;
    font-size: 14px;
}
.disabled {
    cursor: not-allowed;
    color: #666;
}
.disabled:hover {
    cursor: not-allowed;
    color: #666;
}
</style>