<template>
    <b-row class="mb-3">
        <b-col>
            <label :for="field.name + '-input'" class="form-label font-weight-bold">
                {{ field.text }}
                <span class="text-danger" v-if="field.required">*</span>
            </label>
            <b-input-group>
                <b-form-file name="image" v-model="image" :accept="'.jpg,.png,.jpeg,.gif,.bmp'" :disabled="field.disabled" />
                <b-input-group-append>
                    <b-button variant="outline-secondary" v-b-tooltip.hover :title="$t('upload')" @click="uploadImage">
                        <i class="fa fa-fw fa-upload"></i>
                    </b-button>
                </b-input-group-append>
            </b-input-group>
            <b-form-input class="border-top-0 border-right-0 border-left-0 rounded-0" 
                v-show="false"
                :id="field.name + '-input'" 
                :name="field.name" 
                v-model.trim="field.value" 
                :state="field.state" 
                :disabled="field.disabled" 
                type="url" />
            <b-form-invalid-feedback :id="field.name + '-input-feedback'" v-if="field.required" :class="{'d-block': field.state == false}">
                {{ $t('please_input_valid') }}{{ field.text }}
            </b-form-invalid-feedback>
            <b-img thumbnail fluid
                class="mt-2" 
                v-if="field.value" 
                :src="$store.state.base.urlPrefix + field.value" 
                style="cursor: zoom-in;" 
                :width="200" 
                @click="modalShow = true" />
            <div class="image-cancel-div mt-2" v-if="field.value" @click.stop="cancelImage">
                <i class="fa fa-fw fa-lg fa-ban" ></i>
            </div>
            <b-modal v-model="modalShow" 
                v-if="field.value"
                centered 
                size="lg" 
                hide-footer 
                hide-header >
                <b-img center thumbnail fluid :src="$store.state.base.urlPrefix + field.value" />
            </b-modal>
        </b-col>
    </b-row>
</template>

<script>
export default {
    name: 'image-form',
    props: [ 'field' ],
    data: function() {
        return {
            image: null,
            modalShow: false
        }
    },
    methods: {
        uploadImage() {
            if (!this.image) {
                this.$store.commit('showAlert', {
                    message: this.$t('upload_image') + this.$t('not_blank')
                });
                return;
            }
            if (this.image.size > 10 * 1024 * 1024) {
                this.$store.commit('showAlert', {
                    message: this.$t('upload_file_max_size_limit')
                });
                return;
            }
            var formData = new FormData();
            formData.append("image", this.image);
            this.$store.dispatch('uploadImg', formData).then(resp => {
                var data = resp.data;
                if (data.code === 0) {
                    this.field.value = data.data;
                }
            });
        },
        cancelImage() {
            this.field.value = "";
        }
    }
}
</script>

<style>
input[type=file] {
    cursor: pointer;
}
</style>
<style scoped>
.image-cancel-div {
    cursor: pointer;
    color: red;
    vertical-align: top;
    display: inline-block;
    left: -32px;
    top: 0.35rem;
    position: relative;
}
</style>
