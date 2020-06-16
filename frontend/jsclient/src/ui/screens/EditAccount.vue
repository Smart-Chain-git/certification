<template>
    <v-container fluid>
        <v-row class="mx-3 mt-3" justify="space-between">
            <v-col class="pl-4" tag="h1">
                <h1 v-if="editing">{{ $t("account.edit.title.editing") }}</h1>
                <h1 v-else-if="creating"> {{ $t("account.edit.title.creating") }}</h1>
                <h1 v-else>{{ $t("account.edit.title.profile") }}</h1>
            </v-col>
        </v-row>
        <v-row>
            <v-flex lg6 md7 sm8 xs11>
                <Card>
                    <v-card-text class="pa-0" lg3>
                        <EditFormRow v-if="creating || editing" :title="$t('account.edit.login')+' *'" :editable="true">
                            <EditFormTitleEdit v-model.trim="draft.login"/>
                        </EditFormRow>
                        <EditFormRow v-else :title="$t('account.edit.login')" :editable="false" :value="draft.id"/>

                        <EditFormRow v-if="creating || editing" :title="$t('account.edit.email')+' *'" :editable="true" :value="draft.email">
                            <EditFormTitleEdit v-model.trim="draft.email"/>
                        </EditFormRow>
                        <EditFormRow v-else :title="$t('account.edit.email')" :editable="false" :value="draft.email"/>

                        <EditFormRow v-if="creating || editing" :title="$t('account.edit.profile')" :editable="true">
                            <v-checkbox v-model="draft.isAdmin"/>
                        </EditFormRow>
                        <EditFormRow v-else :title="$t('account.edit.profile')" :editable="false" :value="draft.isAdmin ? $t('account.edit.admin') : $t('account.edit.noAdmin')"/>

                        <EditFormRow v-if="creating || editing" :title="$t('account.edit.TEZOSPubKey')" :editable="true">
                            <EditFormTitleEdit v-model.trim="draft.publicKey"/>
                        </EditFormRow>
                        <EditFormRow v-else :title="$t('account.edit.TEZOSPubKey')" :editable="false" :value="draft.publicKey"/>

                        <EditFormRow v-if="creating || editing" :title="$t('account.edit.TEZOSAccount')" :editable="true">
                            <EditFormTitleEdit v-model.trim="draft.publicKey"/>
                        </EditFormRow>
                        <EditFormRow v-else :title="$t('account.edit.TEZOSAccount')" :editable="false" :value="draft.hash"/>

                        <EditFormRow :title="$t('account.edit.fullName')+' *'" :editable="true">
                            <EditFormTitleEdit v-model.trim="draft.fullName"/>
                        </EditFormRow>

                        <div v-if="!creating">
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
                        </div>

                        <v-card-actions class="navigation pt-8 pb-4">
                            <v-flex>
                                <div v-if="message.type !== 'none'" :class="message.type === 'success' ? 'success-frame' : 'error-frame'">
                                    {{ message.message }}
                                </div>
                           </v-flex>
                            <v-flex class="align-right">
                                <IconButton v-if="creating" color="var(--var-color-blue-sword)" @click="create" :disabled="!canCreate" leftIcon="save">
                                    {{ $t('account.edit.save') }}
                                </IconButton>
                                <IconButton v-else color="var(--var-color-blue-sword)" @click="save" :disabled="!canSave" leftIcon="save">
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

    import {AccountCreate, AccountPatch} from '@/store/types'
    import {Component, Prop, Vue, Watch} from "vue-property-decorator"

    interface DraftAccount {
        id: string | undefined
        login: string | undefined
        newPassword: string
        newPasswordConfirmation: string
        fullName: string | undefined
        email: string | undefined
        company: string | undefined
        country: string | undefined
        publicKey: string | undefined
        hash: string | undefined
        isAdmin: boolean | undefined
        isActive: boolean | undefined
    }

    interface Message {
        message: string,
        type: string
    }

    @Component
    export default class EditAccount extends Vue {
        @Prop({default: ""}) private readonly id!: string
        @Prop(Boolean) private readonly creating!: boolean
        @Prop(Boolean) private readonly editing!: boolean

        private message: Message = {message: "", type: "none"}

        private draft: DraftAccount = {
            id: undefined,
            login: undefined,
            fullName: undefined,
            newPassword: "",
            newPasswordConfirmation: "",
            email: undefined,
            company: undefined,
            country: undefined,
            publicKey: undefined,
            hash: undefined,
            isAdmin: undefined,
            isActive: undefined
        }

        private mounted() {
            console.log(this.editing)
            if (this.editing) {
                this.draft = {
                    id: this.$modules.accounts.getCurrentAccount()!.id,
                    login: this.$modules.accounts.getCurrentAccount()?.login,
                    fullName: this.$modules.accounts.getCurrentAccount()?.fullName,
                    newPassword: "",
                    newPasswordConfirmation: "",
                    email: this.$modules.accounts.getCurrentAccount()?.email,
                    company: this.$modules.accounts.getCurrentAccount()?.company,
                    country: this.$modules.accounts.getCurrentAccount()?.country,
                    publicKey: this.$modules.accounts.getCurrentAccount()?.publicKey,
                    hash: this.$modules.accounts.getCurrentAccount()!.hash,
                    isAdmin: this.$modules.accounts.getCurrentAccount()?.isAdmin,
                    isActive: this.$modules.accounts.getCurrentAccount()?.isActive
                }
            } else if (!this.creating) {
                this.draft = {
                    id: this.$modules.accounts.meAccount!.id,
                    login: this.$modules.accounts.meAccount!.login,
                    fullName: this.$modules.accounts.meAccount?.fullName,
                    newPassword: "",
                    newPasswordConfirmation: "",
                    email: this.$modules.accounts.meAccount?.email,
                    company: this.$modules.accounts.meAccount?.company,
                    country: this.$modules.accounts.meAccount?.country,
                    publicKey: this.$modules.accounts.meAccount?.publicKey,
                    hash: this.$modules.accounts.meAccount?.hash,
                    isAdmin: this.$modules.accounts.meAccount?.isAdmin,
                    isActive: this.$modules.accounts.meAccount?.isActive
                }
            }
        }

        @Watch("draft.newPassword")
        @Watch("draft.newPasswordConfirmation")
        private updatePassword() {
            if (this.draft.newPassword !== "" || this.draft.newPasswordConfirmation !== "") {
                if (this.draft.newPassword !== this.draft.newPasswordConfirmation) {
                    this.fail("errors.password.differents")
                } else if (!this.isPasswordStrong) {
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

        private get canCreate() {
            return (this.draft.email !== "" && this.draft.fullName !== "" && this.draft.login !== "" )
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
                isActive: this.draft.isActive,
                password : null,
                company: this.draft.company,
                pubKey: this.draft.publicKey
            }
            if (this.isPasswordStrong) {
                patch.password = this.draft.newPassword
            }
            this.$modules.accounts.updateAccount(this.draft.id!, patch).then(() => {
                this.success("account.edit.updated")
            }).catch(() => {
                this.fail("errors.back.generic")
            })
        }

        private create() {
            const create: AccountCreate = {
                login: this.draft.login!,
                password: "test",
                email: this.draft.email!,
                isActive: this.draft.isActive!,
                isAdmin: this.draft.isAdmin,
                pubKey: this.draft.publicKey,
                fullName: this.draft.fullName!,
                company: this.draft.company
            }

            this.$modules.accounts.createAccount(create).then(() => {
                this.$router.push("/settings")
            }).catch((error) => {

            })
        }

    }
</script>
