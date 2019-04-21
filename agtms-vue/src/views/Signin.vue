<template>
    <b-container class="signin-container">
        <b-card class="w-100 h-100 rounded-0">
            <div class="signin-title">{{ $t("sign_in") }}</div>
            <b-form-input id="username" 
                v-model.trim="form.username" 
                type="text" 
                class="input-filter"
                :class="{'input-error': error.username}"
                :placeholder="$t('username_or_email')" />
            <b-form-input id="password" 
                v-model.trim="form.password" 
                type="password" 
                class="input-filter" 
                :class="{'input-error': error.password}"
                :placeholder="$t('password')"
                @keyup.enter.native="signIn" />
            <div class="input-tip-container">
                <span>{{error.message}}</span>
            </div>
            <b-button @click.stop="signIn" class="input-filter input-btn mt-1 signin-btn">{{ $t("sign_in") }}</b-button>
        </b-card>
    </b-container>
</template>

<script>
export default {
    name: 'sign-in',
    created: function() {
        this.$store.commit('clearProgress');
    },
    beforeRouteUpdate: function(to, from, next) {
        this.$store.commit('clearProgress');
        next();
    },
    data: function() {
        return {
            form: {
                username: '',
                password: ''
            },
            error: {
                username: false,
                password: false,
                message: ''
            }
        }
    },
    methods: {
        signIn: function() {
            var missing = [];
            if (this.form.username == '') {
                missing.push('username');
            } else {
                this.error.username = false;
            }
            if (this.form.password == '') {
                missing.push('password');
            } else {
                this.error.password = false;
            }
            if (missing.length > 0) {
                var msg = '';
                for (var i = 0; i < missing.length; i++) {
                    if (msg !== '') {
                        msg += this.$t('or');
                    }
                    msg += this.$t(missing[i]);
                    this.error[missing[i]] = true;
                }
                msg += this.$t('not_blank');
                this.error.message = msg;
                return false;
            }
            this.error.message = '';
            this.$store.dispatch('login', this.form).then(resp => {
                var data = resp.data;
                if (data.code === 0) {
                    this.$store.commit('setUser', data.data);
                    if (data.data.status == -1) {
                        this.$store.commit('changePasswordModal', true);
                        this.$router.push({
                            path: '/'
                        });
                    } else {
                        this.$store.dispatch('getTree');
                        var reply = this.$route.query.reply;
                        if (reply && reply != '/') {
                            this.$router.push({
                                path: decodeURIComponent(reply)
                            });
                        } else {
                            this.$router.push({
                                path: '/'
                            });
                        }
                    }
                }
            });
        }
    }
}
</script>
<style scoped>
@media (max-height: 576px) {
    .signin-container {
        margin: 20px auto 200px auto!important;
    }
}
.signin-container {
    height: 300px;
    margin: calc((100vh - 500px) / 2) auto;
    max-width: 400px;
}
.signin-title {
    width: 100%;
    height: 42px;
    line-height: 42px;
    text-align: center;
    margin-bottom: 20px;
    font-size: 18px;
    font-weight: 400;
}
.input-filter {
    height: 42px;
    border-radius: 0px;
    margin-top: 20px;
    width: 100%;
    font-size: 14px;
}
.input-tip-container {
    color: red;
    font-size: 12px;
    width: 100%;
    height: 20px;
    line-height: 20px;
    text-align: center;
    margin-top: 5px;
}
.input-error {
    border-color: red;
}
.input-btn {
    border-color: #367fa9;
    background-color: #367fa9;
}
.input-filter:focus, .input-filter:hover {
    box-shadow: none;
    border-color: #367fa9;
}
</style>
