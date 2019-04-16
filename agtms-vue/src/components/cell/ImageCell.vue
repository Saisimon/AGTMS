<template>
    <div class="marquee" style="width: 150px">
        <b-img fluid thumbnail :src="$store.state.base.urlPrefix + rowData[field]" :alt="rowData[field]" style="cursor: pointer;" @click="modalShow = true" />
        <b-modal v-model="modalShow" 
            centered 
            size="lg" 
            hide-footer 
            hide-header >
            <b-img center thumbnail fluid :src="$store.state.base.urlPrefix + rowData[field]" />
        </b-modal>
    </div>
</template>

<script>
export default {
    name: "image-cell",
    props:{
        rowData:{
            type: Object
        },
        field:{
            type: String
        },
        index:{
            type: Number
        }
    },
    data: function() {
        return {
            modalShow: false
        }
    }
}
</script>
