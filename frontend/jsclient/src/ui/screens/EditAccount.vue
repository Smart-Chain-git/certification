<template>
    <v-container fluid>
        <v-row class="mx-3 mt-3" justify="space-between">
            <v-col class="pl-4" tag="h1">
                <template>{{$t('account.edit.title') }}</template>
            </v-col>
        </v-row>
        <v-row>
            <v-flex>
                <Card :width="'55%'">
                    <v-card-text class="pa-0">
                        <EditFormRow :title="$t('account.edit.login')" :editable="false" :value="draft.id"/>

                        <EditFormRow :title="$t('account.edit.email')" :editable="false" :value="draft.email"/>

                        <EditFormRow :title="$t('account.edit.profile')" :editable="false" :value="profile"/>

                        <EditFormRow :title="$t('account.edit.TEZOSPubKey')" :editable="false" :value="draft.TEZOSPubKey"/>

                        <EditFormRow :title="$t('account.edit.TEZOSAccount')" :editable="false" :value="draft.TEZOSAccount"/>

                        <EditFormRow :title="$t('account.edit.fullName')" :editable="true">
                            <EditFormTitleEdit v-model.trim="draft.fullName"/>
                        </EditFormRow>

                        <EditFormRow :title="$t('account.edit.password')"
                                     :editable="true">
                            <EditFormTitleEdit
                                    cssClass="edit-password"
                                    v-model.trim="draft.password"
                                    type="password"
                                    placeholder="******"
                            />
                        </EditFormRow>
                        <EditFormRow
                                     :title="$t('account.edit.passwordConfirmation')"
                                     :editable="true"
                        >
                            <EditFormTitleEdit
                                    cssClass="edit-password"
                                    v-model.trim="draft.passwordConfirmation"
                                    type="password"
                                    placeholder="******"
                            />
                        </EditFormRow>
                        <v-spacer/>
                        <div v-if="draft.password !== '' || draft.passwordConfirmation !== ''">
                            <div v-if="draft.password !== draft.passwordConfirmation" class="error-frame mt-8">
                                {{ $t('errors.password.differents')}}
                            </div>
                            <div v-else-if="!isPasswordValid" class="error-frame mt-8">
                                {{ $t('errors.password.weak') }}
                            </div>
                        </div>
                        <v-card-actions class="navigation pt-4 pb-4">
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
</style>

<script lang="ts">

    import {Account} from '@/api/accountApi'
    import modules from "@/store/modules"
    import {AccountPatch} from '@/store/types'
    import {Component, Prop, Vue} from "vue-property-decorator"

    interface draftAccount {
        id: string,
        password: string,
        passwordConfirmation: string,
        fullName: string | undefined,
        email: string | undefined,
        isAdmin: boolean | undefined,
        TEZOSPubKey: string | undefined | null,
        TEZOSAccount: string
    }

    @Component
    export default class EditAccount extends Vue {
        @Prop({default: ''}) private readonly id!: string
        @Prop({default: false}) private readonly selfEditing!: boolean
        @Prop({default: false}) private readonly creating!: boolean


        private accountId: string = this.$modules.accounts.meAccount!.id

        private draft: draftAccount = {
            id: this.$modules.accounts.meAccount!.id,
            fullName : this.$modules.accounts.meAccount?.fullName,
            password : '',
            passwordConfirmation : '',
            email : this.$modules.accounts.meAccount?.email,
            isAdmin : this.$modules.accounts.meAccount?.isAdmin,
            TEZOSPubKey : this.$modules.accounts.meAccount?.pubKey,
            TEZOSAccount : ''
        }


        private get profile() {
            return this.draft.isAdmin ? "admin" : "pas admin"
        }


        private get canSave() {
            if (this.draft.password !== '' || this.draft.passwordConfirmation !== '') {
                return this.isPasswordValid && this.draft.password === this.draft.passwordConfirmation
            } else {
                return this.draft.fullName !== this.$modules.accounts.meAccount?.fullName
            }
        }

        private get isPasswordValid() {
            return this.draft.password.length >= 8 &&
                /[a-z]/.test(this.draft.password) &&
                /[A-Z]/.test(this.draft.password) &&
                /[0-9]/.test(this.draft.password) &&
                /[ !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~]/.test(this.draft.password)
        }

        private mounted() {
            console.log(this.$modules.accounts.meAccount)
        }

        private save() {
            const patch: AccountPatch = {
                email: this.draft.email,
                fullName : this.draft.fullName,
                isAdmin : this.draft.isAdmin,
                password : this.draft.password
            }
            return this.$modules.accounts.updateAccount(this.draft.id, patch)
        }

       /* private saveCancelDialog: SaveCancelDialogInterface | undefined = undefined

        private get me() {
            return this.$modules.accounts.meAccount
        }

        private get account(): Account | undefined {
            if (this.creating) {
                return undefined
            }
            return this.$modules.accounts.getAccountById(this.accountId)
        }

        private get activationMailBtnAvailable(): boolean {
            if (this.account !== undefined) {
                return this.account.firstLogin && userHasRightToEditUser(modules, this.id)
            }
            return false
        }

        private get firstName(): string {
            if (this.draft.firstName !== undefined) {
                return this.draft.firstName
            } else if (this.account !== undefined) {
                return this.account.firstName
            } else {
                return ''
            }
        }

        private set firstName(firstName: string) {
            Vue.set(this.draft, 'firstName', firstName)
        }

        private get lastName(): string {
            if (this.draft.lastName !== undefined) {
                return this.draft.lastName
            } else if (this.account !== undefined) {
                return this.account.lastName
            } else {
                return ''
            }
        }

        private set lastName(lastName: string) {
            Vue.set(this.draft, 'lastName', lastName)
        }

        private get login() {
            return this.account !== undefined ? this.account.login : ''
        }

        private get email() {
            if (this.draft.email !== undefined) {
                return this.draft.email
            } else if (this.account !== undefined) {
                return this.account.email
            } else {
                return ''
            }
        }

        private set email(email: string) {
            Vue.set(this.draft, 'email', email)
        }

        private get isAdmin(): boolean {
            if (this.draft.isAdmin !== undefined) {
                return this.draft.isAdmin
            } else if (this.account !== undefined) {
                return this.account.isAdmin
            } else {
                return false
            }
        }

        private set isAdmin(isAdmin: boolean) {
            Vue.set(this.draft, 'isAdmin', isAdmin)
        }

        private get allDocuments() {
            return this.$modules.documents.all
        }

        private get allRoles() {
            return this.$modules.accounts.allRoles
        }

        private get role(): Role | undefined {
            if (this.draft.role !== undefined) {
                return this.draft.role
            } else if (this.account !== undefined && this.account.roles.length > 0) {
                // POC : The account has the same role on every document.
                return this.$modules.accounts.getRoleById(this.account.roles[0].roleId)
            } else {
                return undefined
            }
        }

        private set role(role: Role | undefined) {
            Vue.set(this.draft, 'role', role)
        }

        private get documents(): Array<Document> | undefined {
            if (this.draft.documents !== undefined) {
                return this.draft.documents
            } else if (this.account !== undefined) {
                return this.account.roles.map((dr) => this.$modules.documents.getById(dr.documentId)!)
            } else {
                return []
            }
        }

        private set documents(documents: Array<Document> | undefined) {
            Vue.set(this.draft, 'documents', documents)
        }

        private get canSave(): boolean {
            if (!this.canCancel) {
                return false
            }

            return (
                (!this.creating || this.accountId !== '')
                && (this.firstName !== '')
                && (this.lastName !== '')
                && (this.email !== '')
                && (
                    // in editing mode we must match password and password confirmation
                    (!this.creating && this.draft.password.trim() === this.draft.passwordConfirmation) || this.creating
                )
                && (this.isAdmin ||
                    (this.documents !== undefined && this.documents.length !== 0)
                    && (this.role !== undefined))
            )
        }

        private get canCancel(): boolean {
            if (this.account === undefined) {
                return (
                    (this.accountId !== '') ||
                    (this.draft.email !== undefined && this.draft.email !== '') ||
                    (this.draft.firstName !== undefined && this.draft.firstName !== '') ||
                    (this.draft.lastName !== undefined && this.draft.lastName !== '') ||
                    (this.draft.isAdmin !== undefined) ||
                    (this.draft.documents !== undefined && this.draft.documents.length !== 0) ||
                    (this.draft.role !== undefined)
                )
            } else {
                const docs = this.account.roles.map((dr) => this.$modules.documents.getById(dr.documentId)!)
                let rights

                if (this.account.roles.length === 0) {
                    rights = []
                } else {
                    rights = this.$modules.accounts.getRoleById(this.account.roles[0].roleId)
                }
                return (
                    (this.draft.email !== undefined && this.draft.email !== this.account.email) ||
                    (this.draft.firstName !== undefined && this.draft.firstName !== this.account.firstName) ||
                    (this.draft.lastName !== undefined && this.draft.lastName !== this.account.lastName) ||
                    (this.draft.isAdmin !== undefined && this.draft.isAdmin !== this.account.isAdmin) ||
                    (this.draft.password !== undefined && this.draft.password !== '') ||
                    (this.draft.passwordConfirmation !== undefined && this.draft.passwordConfirmation !== '') ||
                    (this.draft.documents !== undefined && JSON.stringify(this.draft.documents) !== JSON.stringify(docs)) ||
                    (this.draft.role !== undefined && this.draft.role !== rights)
                )
            }
        }

        private resetForm() {
            // Reset the login as well while creating
            if (this.creating) {
                this.accountId = ''
            }
            // Reset the draft
            this.draft = {
                firstName: undefined,
                lastName: undefined,
                isAdmin: undefined,
                email: undefined,
                password: '',
                passwordConfirmation: '',
                documents: undefined,
                role: undefined,
            }
        }

        private save() {
            if (this.creating) {
                const accountCreation: AccountCreation = {
                    login: this.accountId,
                    firstName: this.firstName,
                    lastName: this.lastName,
                    isAdmin: this.isAdmin,
                    email: this.email,
                    roles: [],
                }
                if (this.draft.role !== undefined || this.draft.documents !== undefined) {
                    accountCreation.roles = this.documents!!.map((doc) => {
                        return {
                            documentId: doc.id,
                            roleId: this.role!!.id,
                        }
                    })
                }
                return this.$modules.accounts.createAccount(accountCreation)
            } else {

                const patchRequest: AccountPatchRequest = {
                    id: this.accountId,
                    patch: {
                        firstName: this.draft.firstName,
                        lastName: this.draft.lastName,
                        isAdmin: this.draft.isAdmin,
                        email: this.draft.email,
                        password: this.draft.password,
                    },
                }
                if (this.draft.role !== undefined || this.draft.documents !== undefined) {
                    patchRequest.patch.roles = this.documents!!.map((doc) => {
                        return {
                            documentId: doc.id,
                            roleId: this.role!!.id,
                        }
                    })
                }

                return this.$modules.accounts.updateAccount(patchRequest).then(() => {
                        this.resetForm()
                    },
                )
            }

        }

        private askForMail() {
            return this.$modules.accounts.askForActivationMail(this.accountId).then(() => {
                    this.resetForm()
                },
            )
        }

        private async sendActivationMail() {
            await this.askForMail()
        }

        private async saveAndPush() {
            await this.save().then(() => {
                this.saveCancelDialog!.forceRedirection()
                if (!this.selfEditing) {
                    this.$router.push('/accounts')
                } else {
                    this.$router.push('/messages')
                }
            })
        }

        private radio(selected: Role) {
            this.role = selected
        }

        private checkbox(selectedItems: Array<Document>) {
            this.documents = selectedItems
        }

        private mounted() {
            this.$modules.accounts.loadAccount(this.accountId)
            this.saveCancelDialog = this.$refs.saveCancelDialog as unknown as SaveCancelDialogInterface
        }

        private beforeRouteUpdate(to: any, from: any, next: any) {
            if (userHasRightToEditUser(modules, to.params.id)) {
                return next()
            } else {
                return next('/')
            }
        }

        private beforeRouteLeave(to: any, from: any, next: any) {
            this.saveCancelDialog!.popUp(to, next)
        }

        private listDocuments(): string {
            if (this.documents === undefined || this.documents.length === 0) {
                return ''
            } else {
                let documents = ''

                for (const document of this.documents) {
                    documents += ', ' + document.name
                }
                return documents.substr(2)
            }
        }*/
    }
</script>
