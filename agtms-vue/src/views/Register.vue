<template>
    <b-container class="register-container">
        <b-card class="w-100 h-100 rounded-0">
            <div class="register-title">{{ $t("register") }}</div>
            <b-form-input id="username" 
                v-model.trim="form.username" 
                type="text" 
                class="input-filter"
                :class="{'input-error': error.username}"
                :placeholder="$t('username')" />
            <b-form-input id="email" 
                v-model.trim="form.email" 
                type="email" 
                class="input-filter"
                :class="{'input-error': error.email}"
                :placeholder="$t('email')" />
            <b-form-input id="password" 
                v-model.trim="form.password" 
                type="password" 
                class="input-filter" 
                :class="{'input-error': error.password}"
                :placeholder="$t('password')"
                @keyup.enter.native="register" />
            <div class="input-tip-container">
                <span>{{error.message}}</span>
            </div>
            <b-button @click.stop="register" class="input-filter input-btn mt-1 register-btn">{{ $t("register") }}</b-button>
            <div class="signin-link">
                {{ $t("have_account") }}<router-link to="/signin">{{ $t("sign_in") }}</router-link>
            </div>
        </b-card>
    </b-container>
</template>

<script>
export default {
    name: 'register',
    created: function() {
        this.$store.commit('clearProgress');
    },
    data: function() {
        return {
            form: {
                username: '',
                email: '',
                password: ''
            },
            error: {
                username: false,
                email: false,
                password: false,
                message: ''
            }
        }
    },
    methods: {
        register: function() {
            var missing = [];
            if (this.form.username == '') {
                missing.push('username');
            } else {
                this.error.username = false;
            }
            if (this.form.email == '') {
                missing.push('email');
            } else {
                this.error.email = false;
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
            this.$store.dispatch('reg', this.form).then(resp => {
                var data = resp.data;
                if (data) {
                    if (data.code === 0) {
                        this.$store.commit('setUser', data.data);
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
                    } else {
                        this.error.username = true;
                        this.error.email = true;
                        this.error.password = true;
                        this.error.message = data.message;
                    }
                }
            });
        }
    }
}
</script>
<style scoped>
@media (max-height: 576px) {
    .register-container {
        margin: 20px auto 200px auto!important;
    }
}
.register-container {
    height: 400px;
    margin: calc((100vh - 500px) / 2) auto;
    max-width: 400px;
}
.register-title {
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
.signin-link {
    margin: 5px;
    text-align: center;
    font-size: 12px;
}
.signin-link a {
    color: #367fa9!important;
}
</style>