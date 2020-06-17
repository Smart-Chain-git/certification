<template>
    <v-flex id="rootLogin">
        <v-card class="mx-auto padded" max-width="600">
            <div class="text-center">
                <img
                        class="white--text"
                        height="70px"
                        width="auto"
                        src="@/ui/assets/logo_sword.png"
                        alt="Logo SWORD"
                />
            </div>

            <v-card-text>
                <v-layout>
                    <v-flex>
                        <v-text-field color="var(--var-color-blue-sword)" :label="$t('login.login')" v-model="username"
                                      @keyup.enter.native="handleLogin" id="username"
                        />
                        <v-text-field color="var(--var-color-blue-sword)" :label="$t('login.password')" type="password" v-model="password"
                                      @keyup.enter.native="handleLogin" id="password"
                        />
                        <div class="text-right"><a href="/#/forgotten-password">{{ $t('login.forgotten-password') }}</a>
                        </div>
                    </v-flex>
                </v-layout>
            </v-card-text>

            <v-card-actions class="justify-center">
                <v-btn @click="handleLogin" id="connexionButton">
                    <v-icon class="pr-1">double_arrow</v-icon> {{ $t('login.connection') }}
                </v-btn>
            </v-card-actions>

            <v-card-text id="error_container">
                <p class="error-frame text-center v-size--small" v-if="tErrorMessage">
                    {{ $t(tErrorMessage) }}
                </p>
            </v-card-text>

            <div class="text-center" id="gcu">
                <a href="/#/GCU">{{ $t('login.gcu') }}</a>
            </div>
        </v-card>
    </v-flex>
</template>

<style type="text/css" scoped>

    #rootLogin {
        --var-color-blue-sword: #225588;
    }

    img {
        margin: 40px 40px 60px 40px;
    }

    a {
        color: var(--var-color-blue-sword) !important;
        font-size: small;
    }

    #connexionButton {
        margin-top: 20px;
        background-color: var(--var-color-blue-sword);
        color: white;
    }

    #gcu {
        margin-top: 100px;
    }

    .v-size--small {
        font-size: 0.7em;
    }

    .padded {
        padding: 50px 100px;
    }

    #error_container {
        height: 20px;
    }

    .error-frame {
        outline: 1px solid #EE0000;
    }

</style>

<script lang="ts">
    import {fetchData, resetStore} from "@/store/actions/globalActions"
    import {AuthRequest} from "@/api/types"
    import {Component, Vue} from "vue-property-decorator"

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
                if (exception === "Invalid credentials") {
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
