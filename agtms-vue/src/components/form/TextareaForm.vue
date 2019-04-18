<template>
    <b-row class="mb-3">
        <b-col>
            <label :for="field.name + '-input'" class="form-label font-weight-bold">
                {{ field.text }}
                <span class="text-danger" v-if="field.required">*</span>
            </label>
            <quill-editor :id="field.name + '-input'" v-model="field.value" :options="editorOption" />
            <b-form-invalid-feedback :id="field.name + '-input-feedback'" v-if="field.required" :class="{'d-block': field.state == false}">
                {{ $t('please_input_valid') }}{{ field.text }}
            </b-form-invalid-feedback>
        </b-col>
    </b-row>
</template>

<script>
import {quillEditor, Quill} from 'vue-quill-editor'
import {container, ImageExtend, QuillWatch} from 'quill-image-extend-module'
import { ImageDrop } from 'quill-image-drop-module'
import ImageResize from 'quill-image-resize-module'

import 'quill/dist/quill.core.css'
import 'quill/dist/quill.snow.css'
import 'quill/dist/quill.bubble.css'

Quill.register('modules/ImageExtend', ImageExtend)
Quill.register('modules/imageDrop', ImageDrop)
Quill.register('modules/ImageResize', ImageResize)

export default {
    name: 'text-form',
    props: [ 'field' ],
    components: {
        "quill-editor": quillEditor
    },
    data: function() {
        var vm = this;
        return {
            editorOption: {
                placeholder: '',
                modules: {
                    imageDrop: true,
                    ImageResize: true,
                    ImageExtend: {
                        name: 'image',
                        size: 10,
                        action: this.$store.state.base.urlPrefix + '/image/upload',
                        headers: (xhr) => {
                            xhr.setRequestHeader('X-TOKEN', vm.$store.state.base.user.token);
                            xhr.setRequestHeader('X-UID', vm.$store.state.base.user.userId);
                        },
                        response: (res) => {
                            return vm.$store.state.base.urlPrefix + res.data;
                        },
                        sizeError: () => {
                            vm.$store.commit('showAlert', {
                                message: vm.$t('upload_file_max_size_limit')
                            });
                        },
                        error: () => {
                            vm.$store.commit('showAlert', {
                                message: vm.$t('upload_image_failed')
                            });
                        }
                    },
                    toolbar: {
                        container: [
                            [{ size: [ 'small', false, 'large' ]}],
                            [{ 'header': 1 }, { 'header': 2 }],
                            [{ 'list': 'ordered'}, { 'list': 'bullet' }],
                            ['bold', 'italic', 'underline', 'link', 'image', 'video'],
                            [{ 'color': [] }, { 'background': [] }],
                            [{ 'align': [] }],
                        ],
                        handlers: {
                            'image': function () {
                                QuillWatch.emit(this.quill.id)
                            }
                        }
                    }
                }
            }
        }
    }
}
</script>
<style>
.ql-editor {
    min-height: 200px;
}
</style>
