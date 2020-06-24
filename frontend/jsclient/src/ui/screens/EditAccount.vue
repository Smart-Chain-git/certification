<template>
    <v-container fluid>
        <v-row class="mx-3 mt-3" justify="space-between">
            <v-col class="pl-4" tag="h1">
                <h1 v-if="access === 'adminEditing' || access === 'creating'">{{ $t("account.edit.title.manage") }}</h1>
                <h1 v-else>{{ $t("account.edit.title.profile") }}</h1>
            </v-col>
        </v-row>
        <v-row>
            <v-flex lg6 md7 sm8 xs11>
                <Card>
                    <CardTitle v-if="access === 'adminEditing'" icon="person">{{ $t("account.edit.subTitle.edit")}}</CardTitle>
                    <CardTitle v-if="access === 'creating'" icon="person">{{ $t("account.edit.subTitle.create")}}</CardTitle>
                    <v-card-text class="pa-0" lg3>
                        <EditFormRow v-if="access === 'creating' || access === 'adminEditing'" :title="$t('account.edit.login')+' *'" :editable="true">
                            <EditFormTitleEdit :placeholder="$t('account.edit.login')" color="var(--var-color-blue-sword)" v-model.trim="draft.login"/>
                        </EditFormRow>
                        <EditFormRow v-else :title="$t('account.edit.login')" :editable="false" :value="draft.login"/>

                        <EditFormRow v-if="access === 'creating' || access === 'adminEditing'" :title="$t('account.edit.email')+' *'" :editable="true">
                            <EditFormTitleEdit :placeholder="$t('account.edit.email')" color="var(--var-color-blue-sword)" v-model.trim="draft.email"/>
                        </EditFormRow>
                        <EditFormRow v-else :title="$t('account.edit.email')" :editable="false" :value="draft.email"/>

                        <EditFormRow v-if="access === 'creating' || access === 'adminEditing'" :title="$t('account.edit.company')" :editable="true">
                            <EditFormTitleEdit :placeholder="$t('account.edit.company')" color="var(--var-color-blue-sword)" v-model.trim="draft.company"/>
                        </EditFormRow>
                        <EditFormRow v-else :title="$t('account.edit.company')" :editable="false" :value="draft.company"/>

                        <EditFormRow v-if="access === 'creating' || access === 'adminEditing'" :title="$t('account.edit.country')" :editable="true">
                            <EditFormTitleEdit :placeholder="$t('account.edit.country')" color="var(--var-color-blue-sword)" v-model.trim="draft.country"/>
                        </EditFormRow>
                        <EditFormRow v-else :title="$t('account.edit.country')" :editable="false" :value="draft.country"/>

                        <EditFormRow v-if="access === 'creating' || access === 'adminEditing'" :title="$t('account.edit.admin')" :editable="true">
                            <v-checkbox v-model="draft.isAdmin"/>
                        </EditFormRow>
                        <EditFormRow v-else :title="$t('account.edit.profile')" :editable="false" :value="draft.isAdmin ? $t('account.edit.admin') : $t('account.edit.noAdmin')"/>

                        <EditFormRow v-if="access === 'creating' || access === 'adminEditing'" :title="$t('account.edit.TEZOSPubKey')" :editable="true">
                            <EditFormTitleEdit :placeholder="$t('account.edit.TEZOSPubKey')" color="var(--var-color-blue-sword)" v-model.trim="draft.publicKey"/>
                        </EditFormRow>
                        <EditFormRow v-else :title="$t('account.edit.TEZOSPubKey')" :editable="false" :value="draft.publicKey"/>


                        <EditFormRow v-if="access === 'creating' || access === 'adminEditing'" :title="$t('account.edit.TEZOSAccount')" :editable="true">
                            <EditFormTitleEdit :placeholder="$t('account.edit.TEZOSAccount')" color="var(--var-color-blue-sword)" v-model.trim="draft.hash"/>
                        </EditFormRow>
                        <EditFormRow v-else :title="$t('account.edit.TEZOSAccount')" :editable="false" :value="draft.hash"/>

                        <EditFormRow :title="$t('account.edit.fullName') +' *'" :editable="true">
                            <EditFormTitleEdit :placeholder="$t('account.edit.fullName')" v-model.trim="draft.fullName" color="var(--var-color-blue-sword)"/>
                        </EditFormRow>

                        <div v-if="access !== 'creating' && access !== 'adminEditing'">
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
                        <span v-if="access === 'creating' || access === 'adminEditing'" class="mt-4">
                           {{ $t("account.edit.mandatory") }}
                        </span>
                        <v-card-actions class="navigation pt-8 pb-4">
                            <v-flex>
                                <div v-if="message.type !== 'none'"
                                     :class="message.type === 'success' ? 'success-frame' : 'error-frame'">
                                    {{ message.message }}
                                </div>
                            </v-flex>

                            <v-flex class="align-right">
                                <IconButton color="red" @click="cancel" :disabled="!canCancel" leftIcon="clear" class="mr-2">
                                    {{ $t("account.list.cancel") }}
                                </IconButton>
                                <IconButton v-if="access === 'creating'" color="var(--var-color-blue-sword)" @click="create" :disabled="!canCreate" leftIcon="add_circle_outline">
                                    {{ $t("account.list.add") }}
                                </IconButton>
                                <IconButton v-else color="var(--var-color-blue-sword)" @click="save" :disabled="!canSave" leftIcon="save">
                                    {{ $t("account.edit.save") }}
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
    import {AccountCreate, AccountPatch} from "@/api/types"
    import {Component, Prop, Vue, Watch} from "vue-property-decorator"

    interface DraftAccount {
        id: string | undefined
        login: string | undefined
        newPassword: string | undefined
        newPasswordConfirmation: string | undefined
        fullName: string | undefined
        email: string | undefined
        company: string | undefined
        country: string | undefined
        publicKey: string | undefined
        hash: string | undefined
        isAdmin: boolean | undefined
        disabled: boolean | undefined
    }

    interface Message {
        message: string,
        type: string
    }

    @Component
    export default class EditAccount extends Vue {
        @Prop({default: ""}) private readonly id!: string
        @Prop() private readonly access!: string

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
            disabled: undefined,
        }

        private mounted() {
            this.fillDraft()
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

        private get currentAccount() {
            if (this.access === "selfEditing") {
                return this.$modules.accounts.meAccount
            }
            return this.$modules.accounts.getCurrentAccount()
        }

        private get canCancel() {
            switch (this.access) {
                case "adminEditing":
                    return (
                        this.draft.login !== this.currentAccount?.login ||
                        this.draft.email !== this.currentAccount?.email ||
                        this.draft.publicKey !== this.currentAccount?.publicKey ||
                        this.draft.isAdmin !== this.currentAccount?.isAdmin ||
                        this.draft.hash !== this.currentAccount?.hash ||
                        this.draft.fullName !== this.currentAccount?.fullName ||
                        this.draft.company !== this.currentAccount?.company ||
                        this.draft.country !== this.currentAccount?.country
                    )
                case "creating":
                    return (
                        (this.draft.login !== "" && this.draft.login !== undefined) ||
                        (this.draft.publicKey !== "" && this.draft.publicKey !== undefined) ||
                        (this.draft.email !== "" && this.draft.email !== undefined) ||
                        (this.draft.fullName !== "" && this.draft.fullName !== undefined) ||
                        (this.draft.country !== "" && this.draft.country !== undefined) ||
                        (this.draft.company !== "" && this.draft.company !== undefined) ||
                        (this.draft.hash !== "" && this.draft.hash !== undefined) ||
                        (this.draft.isAdmin !== undefined && this.draft.disabled !== undefined)
                    )
                case "selfEditing":
                    return (
                        (this.draft.newPassword !== "" && this.draft.newPassword !== undefined) ||
                        (this.draft.newPasswordConfirmation !== "" && this.draft.newPasswordConfirmation !== undefined) ||
                        (this.draft.fullName !== this.currentAccount?.fullName)
                    )
            }
        }

        private get canSave() {
            switch (this.access) {
                case "creating":
                case "adminEditing":
                    return (
                        this.draft.isAdmin !== this.currentAccount?.isAdmin ||
                        this.draft.publicKey !== this.currentAccount?.publicKey ||
                        this.draft.hash !== this.currentAccount?.hash ||
                        this.draft.email !== this.currentAccount?.email ||
                        this.draft.country !== this.currentAccount?.country ||
                        this.draft.company !== this.currentAccount?.company ||
                        this.draft.fullName !== this.currentAccount?.fullName
                    ) && (
                        this.draft.email !== "" && this.draft.email !== undefined &&
                        this.draft.login !== "" && this.draft.login !== undefined &&
                        this.draft.fullName !== "" && this.draft.fullName !== undefined
                    )
                case "selfEditing":
                    if (this.draft.newPassword !== "" || this.draft.newPasswordConfirmation !== "") {
                        return this.draft.fullName !== "" &&
                            this.isPasswordStrong && this.draft.newPassword === this.draft.newPasswordConfirmation
                    } else {
                        return this.draft.fullName !== this.$modules.accounts.meAccount?.fullName && this.draft.fullName !== ""
                    }
            }
        }

        private get canCreate() {
            return (this.draft.email !== undefined && this.draft.email !== "" &&
                this.draft.fullName !== undefined && this.draft.fullName !== "" &&
                this.draft.login !== undefined &&  this.draft.login !== "")
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
                email: (this.draft.email !== this.currentAccount?.email) ? this.draft.email : undefined,
                password: undefined,
                fullName: (this.draft.fullName !== this.currentAccount?.fullName) ? this.draft.fullName : undefined,
                company: (this.draft.company !== this.currentAccount?.company) ? this.draft.company : undefined,
                country: (this.draft.country !== this.currentAccount?.country) ? this.draft.country : undefined,
                publicKey: (this.draft.publicKey !== this.currentAccount?.publicKey) ? this.draft.publicKey : undefined,
                hash: (this.draft.hash !== this.currentAccount?.hash) ? this.draft.hash : undefined,
                isAdmin: (this.draft.isAdmin !== this.currentAccount?.isAdmin) ? this.draft.isAdmin : undefined,
                disabled: (this.draft.disabled !== this.currentAccount?.disabled) ? this.draft.disabled : undefined,
            }

            if (this.isPasswordStrong) {
                patch.password = this.draft.newPassword
            }
            this.$modules.accounts.updateAccount(this.draft.id!, patch).then(() => {
                this.fillDraft()
                this.success("account.edit.updated")
            }).catch(() => {
                this.fail("errors.back.generic")
            })
        }

        private cancel() {
            this.fillDraft()
        }

        private fillDraft() {
            if (this.access === "creating") {
                this.draft = {
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
                    disabled: undefined,
                }
            } else {
                this.draft = {
                    id: this.currentAccount!.id,
                    login: this.currentAccount?.login,
                    fullName: this.currentAccount?.fullName,
                    newPassword: "",
                    newPasswordConfirmation: "",
                    email: this.currentAccount?.email,
                    company: this.currentAccount?.company,
                    country: this.currentAccount?.country,
                    publicKey: this.currentAccount?.publicKey,
                    hash: this.currentAccount!.hash,
                    isAdmin: this.currentAccount?.isAdmin,
                    disabled: this.currentAccount?.disabled,
                }
            }
        }

        private create() {
            const create: AccountCreate = {
                login: this.draft.login!,
                email: this.draft.email!,
                disabled: (this.draft.disabled) ? this.draft.disabled : false,
                isAdmin: (this.draft.isAdmin) ? this.draft.isAdmin : false,
                publicKey: this.draft.publicKey,
                fullName: this.draft.fullName!,
                company: this.draft.company,
                country: this.draft.country,
                hash: this.draft.hash,
            }

            this.$modules.accounts.createAccount(create).then(() => {
                if (this.$modules.accounts.getHttpStatus() !== 200) {
                    if (this.$modules.accounts.getHttpStatus() === 409) {
                        this.fail("errors.account.duplicate")
                    } else {
                        this.fail("errors.back.generic")
                    }
                } else {
                    this.$router.push("/settings")
                }
            })
        }
    }
</script>
