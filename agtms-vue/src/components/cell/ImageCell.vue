<template>
    <div class="text-center">
        <div v-if="rowData[field]">
            <b-img fluid thumbnail 
                width="100" 
                :src="src" 
                style="cursor: zoom-in;" 
                @click="modalShow = true" />
            <b-modal v-model="modalShow" 
                centered 
                size="xl" 
                hide-footer 
                hide-header >
                <b-img center thumbnail fluid :src="src" />
            </b-modal>
        </div>
        <div v-else>-</div>
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
    computed: {
        src: function() {
            var src = this.rowData[this.field];
            if (src && !src.startsWith("http")) {
                src = this.$store.state.base.urlPrefix + src;
            }
            return src;
        }
    },
    data: function() {
        return {
            modalShow: false
        }
    }
}
</script>
