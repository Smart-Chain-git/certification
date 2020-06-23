<template>
    <v-flex>
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

            <ConfirmPassword v-if="unactivated"
                             @token="token"
                             :passwordTitleMessage=" $t('firstLogin.title')"
                             :passwordMessage1="$t('firstLogin.message1')"
                             :passwordMessage2="$t('firstLogin.message2')"
                             :passwordFieldMessage="$t('firstLogin.password')"
                             :passwordConfirmationFieldMessage="$t('firstLogin.passwordConfirmation')"
                             :passwordSaveButtonMessage="$t('firstLogin.activate')"
                             passwordSaveButtonIcon="check"/>

            <v-card-text v-else>
                <v-layout>
                    <v-flex>
                        <p class="error-frame text-center v-size--small">
                            {{ $t('firstLogin.alreadyActivated.title') }}<br><br>
                            {{ $t('firstLogin.alreadyActivated.message1') }}<br>
                            {{ $t('firstLogin.alreadyActivated.message2') }}<br>
                        </p>
                    </v-flex>
                </v-layout>
            </v-card-text>

            <div class="text-center" id="gcu">
                <a href="/#/GCU">{{ $t('login.gcu') }}</a>
            </div>
        </v-card>
    </v-flex>
</template>

<style type="text/css" scoped>

    img {
        margin: 40px 40px 60px 40px;
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

    #gcu {
        margin-top: 100px;
    }

</style>

<script lang="ts">
    import {Component, Prop, Vue} from "vue-property-decorator"

    @Component
    export default class Activation extends Vue {

        @Prop(String) private readonly token!: string
        private unactivated: boolean = true

        private mounted() {
            this.unactivated = this.$modules.accounts.currentActivationError !== 403
                && this.$modules.accounts.currentActivationAccount!.firstLogin
        }
    }
</script>
