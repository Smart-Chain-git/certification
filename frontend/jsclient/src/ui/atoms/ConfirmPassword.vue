<template>
    <div>
        <v-card-text>
            <v-layout>
                <v-flex>
                    <div class="text--primary text-center">{{ passwordTitleMessage }}</div>
                    <div class="text--primary text-center">{{ passwordMessage1 }}</div>
                    <div class="text--primary text-center">{{ passwordMessage2 }}</div>
                </v-flex>
            </v-layout>
        </v-card-text>
        <v-card-text>
            <v-layout>
                <v-flex>
                    <v-text-field :label="passwordFieldMessage"
                                  :append-icon="show1 ? 'mdi-eye' : 'mdi-eye-off'"
                                  :type="show1 ? 'text' : 'password'"
                                  @click:append="show1 = !show1"
                                  v-model="password"
                                  keyup.native="matchingPasswords"
                                  @keyup.enter.native="handleSave"
                                  autocomplete="new-password"
                                  color="var(--var-color-blue-sword)"
                                  id="password"/>
                    <v-text-field :label="passwordConfirmationFieldMessage"
                                  :append-icon="show2 ? 'mdi-eye' : 'mdi-eye-off'"
                                  :type="show2 ? 'text' : 'password'"
                                  @click:append="show2 = !show2"
                                  v-model="passwordConfirmation"
                                  autocomplete="new-password"
                                  color="var(--var-color-blue-sword)"
                                  id="password-verification"/>
                </v-flex>
            </v-layout>
        </v-card-text>
        <v-card-actions class="justify-center">
            <IconButton color="success" :leftIcon="passwordSaveButtonIcon" :disabled="!canSave"
                        @click="handleSave">
                {{ passwordSaveButtonMessage }}
            </IconButton>
        </v-card-actions>

        <v-card-text v-if="tErrorMessage">
            <p class="error-frame text-center v-size--small">
                {{ $t('firstLogin.passwordError') }}
            </p>
        </v-card-text>
    </div>
</template>

<style type="text/css" scoped>

    .v-size--small {
        font-size: 0.7em;
    }

    .error-frame {
        outline: 1px solid #EE0000;
    }

</style>

<script lang="ts">
    import {AccountValidation} from "@/api/types"
    import {Component, Prop, Vue, Watch} from "vue-property-decorator"


    @Component
    export default class CreatePassword extends Vue {

        @Prop(String) private readonly token!: string
        @Prop(String) private readonly passwordFieldMessage!: string
        @Prop(String) private readonly passwordConfirmationFieldMessage!: string
        @Prop(String) private readonly passwordSaveButtonMessage!: string
        @Prop(String) private readonly passwordTitleMessage!: string
        @Prop(String) private readonly passwordMessage1!: string
        @Prop(String) private readonly passwordMessage2!: string
        @Prop(String) private readonly passwordSaveButtonIcon!: string

        private password: string = ""
        private passwordConfirmation: string = ""
        private show1: boolean = false
        private show2: boolean = false
        private tErrorMessage: string = ""

        @Watch("password")
        @Watch("passwordConfirmation")
        private updatePassword() {
            if (this.password !== "" || this.passwordConfirmation !== "") {
                if (this.password !== this.passwordConfirmation) {
                    this.tErrorMessage = "errors.password.differents"
                } else if (!this.isPasswordStrong) {
                    this.tErrorMessage = "errors.password.weak"
                } else {
                    this.tErrorMessage = ""
                }
            } else {
                this.tErrorMessage = ""
            }
        }

        private get isPasswordStrong() {
            return this.password!.length >= 8 &&
                /[a-z]/.test(this.password!) &&
                /[A-Z]/.test(this.password!) &&
                /[0-9]/.test(this.password!) &&
                /[ !"#$%&'()*+,-./:;<=>?@^_`{|}~]/.test(this.password!)
        }

        private async handleSave() {
            const patch: AccountValidation = {
                password: this.password,
            }
            await this.$modules.accounts.validateAccount(patch)
                .then(async () => this.$router.push("/login"))
        }

        private get canSave() {
            if (this.password !== "" || this.passwordConfirmation !== "") {
                return this.isPasswordStrong && this.password === this.passwordConfirmation
            } else {
                return false
            }
        }
    }
</script>
