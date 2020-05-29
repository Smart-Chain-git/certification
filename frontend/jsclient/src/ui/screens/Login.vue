<template>
    <v-flex>
        <v-card class="mx-auto padded" max-width="600">
            <div class="text-center">
                <img
                        class="white--text"
                        height="200px"
                        src="@/ui/assets/logo-KAMI-outside-RVB-WEB-transparent.png"
                        alt="Logo Kami Outside"
                />
            </div>

            <v-card-text>
                <v-layout>
                    <v-flex>
                        <v-text-field :label="$t('login.login')" v-model="username"
                                      @keyup.enter.native="handleLogin" id="username"
                        />
                        <v-text-field :label="$t('login.password')" type="password" v-model="password"
                                      @keyup.enter.native="handleLogin" id="password"
                        />
                        <div class="text-right"><a href="/#/forgotten-password">{{ $t('login.forgotten-password') }}</a>
                        </div>
                    </v-flex>
                </v-layout>
            </v-card-text>

            <v-card-actions class="justify-center">
                <v-btn color="success" @click="handleLogin" id="connexionButton">
                    {{ $t('login.connection') }}
                </v-btn>
            </v-card-actions>

            <v-card-text v-if="tErrorMessage">
                <p class="error-frame text-center v-size--small">
                    {{ $t(tErrorMessage) }}
                </p>
            </v-card-text>

            <v-card-text class="text-center">
                <v-btn text small>
                    {{ $t('login.gcu') }}
                </v-btn>
            </v-card-text>
        </v-card>
    </v-flex>
</template>

<style type="text/css" scoped>

    img {
        margin: 40px;
        /* We offset the image to make the design *more beautiful*. */
        transform: translate(-50px);
    }

    .v-size--small {
        font-size: 0.7em;
    }

    .padded {
        padding: 50px 100px;
    }

    .error-frame {
        outline: 1px solid #EE0000;
    }

</style>

<script lang="ts">
    import {fetchData, resetStore} from "@/store/actions/globalActions"
    import {Component, Vue} from "vue-property-decorator"
    import {AuthRequest} from "@/api/accountApi"

    @Component
    export default class Login extends Vue {
        private password: string = ""
        private username: string = ""
        private tErrorMessage: string = ""

        private mounted() {
            // Reset the token and store
            this.$modules.accounts.invalidateToken()
            resetStore(this.$modules)
        }

        private async handleLogin() {
            const authRequest: AuthRequest = {user: this.username, password: this.password}
            await this.$modules.accounts.loadToken(authRequest).then(async () => {
                this.tErrorMessage = ""
                await fetchData(this.$modules).then(() => this.$router.push("/"))
            }).catch((error) => {
                const exception = error.response.data.message
                if (exception === "Invalid user/password"
                    || exception === "The Account for ' + this.username + ' was not found.") {
                    this.tErrorMessage = "errors.login.invalid"
                } else if (exception === "Disabled account") {
                    this.tErrorMessage = "errors.login.disabled"
                } else {
                    this.tErrorMessage = "errors.login.default"
                }
            })
        }
    }
</script>
