<template>
    <v-container fluid>
        <v-row class="mx-3 mt-3" justify="space-between">
            <v-col class="pl-4" tag="h1">
                <template>{{$t('account.edit.title') }}</template>
            </v-col>
        </v-row>
        <v-row>
            <v-flex>
                <Card :maxWidth="590">
                    <v-card-text class="pa-0">
                        <EditFormRow :title="$t('account.edit.login')" :editable="false" :value="draft.id"/>

                        <EditFormRow :title="$t('account.edit.email')" :editable="false" :value="draft.email"/>

                        <EditFormRow :title="$t('account.edit.profile')" :editable="false" :value="draft.isAdmin ? $t('account.edit.admin') : $t('account.edit.noAdmin')"/>

                        <EditFormRow :title="$t('account.edit.TEZOSPubKey')" :editable="false" :value="draft.TEZOSPubKey"/>

                        <EditFormRow :title="$t('account.edit.TEZOSAccount')" :editable="false" :value="draft.TEZOSAccount"/>

                        <EditFormRow :title="$t('account.edit.fullName')" :editable="true">
                            <EditFormTitleEdit v-model.trim="draft.fullName"/>
                        </EditFormRow>

                        <EditFormRow :title="$t('account.edit.newPassword')"
                                     :editable="true">
                            <EditFormTitleEdit
                                    cssClass="edit-password"
                                    v-model.trim="draft.newPassword"
                                    type="password"
                                    placeholder="******"
                            />
                        </EditFormRow>
                        <EditFormRow
                                     :title="$t('account.edit.newPasswordConfirmation')"
                                     :editable="true"
                        >
                            <EditFormTitleEdit
                                    cssClass="edit-password"
                                    v-model.trim="draft.newPasswordConfirmation"
                                    type="password"
                                    placeholder="******"
                            />
                        </EditFormRow>

                        <v-card-actions class="navigation pt-8 pb-4">
                            <v-flex>
                                <div v-if="message.type !== 'none'" :class="message.type === 'success' ? 'success-frame' : 'error-frame'">
                                    {{ message.message }}
                                </div>
                           </v-flex>
                            <v-flex class="align-right">
                                <IconButton color="primary" @click="save" :disabled="!canSave" leftIcon="save">
                                    {{ $t('account.edit.save') }}
                                </IconButton>
                            </v-flex>
                        </v-card-actions>
                    </v-card-text>
                </Card>
            </v-flex>
        </v-row>
    </v-container>
</template>

<style lang="css">

    .edit-password ::placeholder {
        color: black !important;
        opacity: 1.0 !important;
        font-size: 1.2rem;
    }

    .align-right {
        text-align: right;
    }

    .error-frame {
        color: red;
    }

    .success-frame {
        color: green;
    }
</style>

<script lang="ts">

    import {AccountPatch} from "@/store/types"
    import {Component, Prop, Vue, Watch} from "vue-property-decorator"

    interface DraftAccount {
        id: string,
        newPassword: string,
        newPasswordConfirmation: string,
        fullName: string | undefined,
        email: string | undefined,
        isAdmin: boolean | undefined,
        TEZOSPubKey: string | undefined | null,
        TEZOSAccount: string
    }

    interface Message {
        message: string,
        type: string
    }

    @Component
    export default class EditAccount extends Vue {
        @Prop({default: ""}) private readonly id!: string

        private message: Message = {message: "", type: "none"}

        private draft: DraftAccount = {
            id: this.$modules.accounts.meAccount!.id,
            fullName: this.$modules.accounts.meAccount?.fullName,
            newPassword: "",
            newPasswordConfirmation: "",
            email: this.$modules.accounts.meAccount?.email,
            isAdmin: this.$modules.accounts.meAccount?.isAdmin,
            TEZOSPubKey: this.$modules.accounts.meAccount?.pubKey,
            TEZOSAccount: "",
        }

        @Watch('draft.newPassword')
        @Watch('draft.newPasswordConfirmation')
        private updatePassword() {
            if (this.draft.newPassword !== "" || this.draft.newPasswordConfirmation !== "")
            {
                if (this.draft.newPassword !== this.draft.newPasswordConfirmation)
                {
                    this.fail("errors.password.differents")
                }
                else if (!this.isPasswordStrong) {
                    this.fail("errors.password.weak")
                } else {
                    this.message.type = "none"
                }
            } else {
                this.message.type = "none"
            }
        }

        private get canSave() {
            if (this.draft.newPassword !== "" || this.draft.newPasswordConfirmation !== "") {
                return this.isPasswordStrong && this.draft.newPassword === this.draft.newPasswordConfirmation
            } else {
                return this.draft.fullName !== this.$modules.accounts.meAccount?.fullName
            }
        }

        private get isPasswordStrong() {
            return this.draft.newPassword!.length >= 8 &&
                /[a-z]/.test(this.draft.newPassword!) &&
                /[A-Z]/.test(this.draft.newPassword!) &&
                /[0-9]/.test(this.draft.newPassword!) &&
                /[ !"#$%&'()*+,-./:;<=>?@^_`{|}~]/.test(this.draft.newPassword!)
        }

        private fail(msg: string) {
            this.message.message = this.$t(msg).toString()
            this.message.type = "failure"
        }

        private success(msg: string) {
            this.message.message = this.$t(msg).toString()
            this.message.type = "success"
        }

        private save() {
            const patch: AccountPatch = {
                email: this.draft.email,
                fullName : this.draft.fullName,
                isAdmin : this.draft.isAdmin,
                password : null,
            }
            if (this.isPasswordStrong) {
                patch.password = this.draft.newPassword
            }
            this.$modules.accounts.updateAccount(this.draft.id, patch).then(() => {
                this.success("account.edit.updated")
            }).catch(() => {
                this.fail("errors.back.generic")
            })
        }
    }
</script>
