<template>
    <div class="user-container">
        <b-card header-tag="header">
            <b-row slot="header">
                <b-col>
                    <div class="card-header-title-container text-truncate">
                        {{ $t('edit_profile') }}
                    </div>
                </b-col>
            </b-row>
            <b-row class="mb-3">
                <b-col class="col-12 col-md-8">
                    <b-row class="mb-3">
                        <b-col>
                            <label for="username" class="form-label font-weight-bold">
                                {{ $t('username') }}
                            </label>
                            <b-form-input class="border-top-0 border-right-0 border-left-0 rounded-0" 
                                :value="loginName"
                                :disabled="true"
                                id="username-input" 
                                name="username" 
                                type="text" />
                        </b-col>
                    </b-row>
                    <b-row class="mb-3">
                        <b-col>
                            <label for="cellphone" class="form-label font-weight-bold">
                                {{ $t('cellphone') }}
                            </label>
                            <b-form-input class="border-top-0 border-right-0 border-left-0 rounded-0" 
                                :value="cellphone"
                                :disabled="true"
                                id="cellphone-input" 
                                name="cellphone" 
                                type="text" />
                        </b-col>
                    </b-row>
                    <b-row class="mb-3">
                        <b-col>
                            <label for="email" class="form-label font-weight-bold">
                                {{ $t('email') }}
                            </label>
                            <b-form-input class="border-top-0 border-right-0 border-left-0 rounded-0" 
                                :value="email"
                                :disabled="true"
                                id="email-input" 
                                name="email" 
                                type="text" />
                        </b-col>
                    </b-row>
                    <b-row class="mb-3">
                        <b-col>
                            <label for="nickname" class="form-label font-weight-bold">
                                {{ $t('nickname') }}
                            </label>
                            <b-form-input class="border-top-0 border-right-0 border-left-0 rounded-0" 
                                v-model="nickname.value"
                                id="nickname-input" 
                                name="nickname" 
                                type="text" />
                            <b-form-invalid-feedback id="nickname-input-feedback" :class="{'d-block': nickname.state == false}">
                                {{ $t(nickname.message) }}
                            </b-form-invalid-feedback>
                        </b-col>
                    </b-row>
                    <b-row class="mb-3">
                        <b-col>
                            <label for="remark" class="form-label font-weight-bold">
                                {{ $t('remark') }}
                            </label>
                            <b-form-textarea id="remark-input" 
                                v-model="remark.value"
                                no-resize 
                                :rows="6" />
                            <b-form-invalid-feedback id="remark-input-feedback" :class="{'d-block': remark.state == false}">
                                {{ $t(remark.message) }}
                            </b-form-invalid-feedback>
                        </b-col>
                    </b-row>
                </b-col>
                <b-col>
                    <label for="avatar" class="form-label font-weight-bold">
                        {{ $t('avatar') }}
                    </label>
                    <b-form-input class="border-top-0 border-right-0 border-left-0 rounded-0" 
                        v-model="avatar.value"
                        v-show="false"
                        id="avatar-input" 
                        name="avatar" 
                        type="text" />
                    <div class="avatar-div">
                        <img class="avatar-image" :src="$store.state.base.urlPrefix + avatar.value" v-if="avatar.value">
                        <avatar class="avatar-image" v-else :username="$store.state.base.user.loginName" :size="200" :rounded="false"></avatar>
                    </div>
                    <b-button variant="dark" id="pick-avatar" size="sm" >
                        <i class="fa fa-fw fa-upload"></i>
                        {{ $t('upload_image') }}
                    </b-button>
                    <avatar-cropper
                        @uploaded="handleUploaded"
                        trigger="#pick-avatar"
                        mimes="image/png, image/gif, image/jpeg, image/bmp"
                        upload-form-name="image"
                        :labels="{ submit: $t('confirm'), cancel: $t('cancel')}"
                        :upload-headers="{'X-TOKEN': $store.state.base.user.token, 'X-UID': $store.state.base.user.userId}"
                        :upload-url="$store.state.base.urlPrefix + '/image/upload'" />
                    <b-form-invalid-feedback id="avatar-input-feedback" :class="{'d-block': avatar.state == false}">
                        {{ $t(avatar.message) }}
                    </b-form-invalid-feedback>
                </b-col>
            </b-row>
            <b-row>
                <b-col>
                    <b-button variant="outline-primary" size="sm" @click="saveProfile" >
                        <i class="fa fa-fw fa-save"></i>
                        {{ $t('update_profile') }}
                    </b-button>
                </b-col>
            </b-row>
        </b-card>
    </div>
</template>

<script>
import AvatarCropper from "vue-avatar-cropper"
import Avatar from 'vue-avatar'

export default {
    name: 'profile',
    created: function() {
        this.$store.commit('setBreadcrumbs', []);
        var vm = this;
        this.$store.dispatch('profile').then(resp => {
            if (resp.data.code === 0) {
                var profile = resp.data.data;
                this.loginName = profile.loginName;
                this.email = profile.email;
                this.cellphone = profile.cellphone;
                this.avatar.value = profile.avatar;
                this.nickname.value = profile.nickname;
                this.remark.value = profile.remark;
            }
            vm.$store.commit('clearProgress');
        });
    },
    components: { 
        'avatar-cropper': AvatarCropper,
        'avatar': Avatar
    },
    data() {
        return {
            loginName: null,
            email: null,
            cellphone: null,
            avatar: {
                value: '',
                state: null,
                message: ''
            },
            nickname: {
                value: '',
                state: null,
                message: ''
            },
            remark: {
                value: '',
                state: null,
                message: ''
            }
        }
    },
    methods: {
        handleUploaded(resp) {
            this.avatar.value = resp.data;
        },
        saveProfile() {
            var avatarValue = this.avatar.value;
            var nicknameValue = this.nickname.value;
            var remarkValue = this.remark.value;
            var payload = {
                avatar: avatarValue,
                nickname: nicknameValue,
                remark: remarkValue,
            }
            var vm = this;
            this.$store.dispatch('saveProfile', payload).then(resp => {
                if (resp.data.code === 0) {
                    vm.$store.commit('showAlert', {
                        variant: 'success',
                        message: vm.$t('save_success')
                    });
                    vm.$store.commit('setAvatar', avatarValue);
                }
            });
        }
    }
}
</script>
<style scoped>
.avatar-image {
    width: 200px;
    height: 200px;
    border-radius: 6px!important;
}
#pick-avatar {
    position: relative;
    top: -40px;
    left: 10px;
    float: left;
}
</style>
