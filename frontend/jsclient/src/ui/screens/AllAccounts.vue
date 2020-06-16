<template>
    <v-container fluid>
        <v-row class="mx-3 mt-3" justify="space-between">
            <v-flex>
                <h1>{{ $t('account.list.title') }}</h1>
            </v-flex>
        </v-row>
        <v-row>
            <v-flex xs11>
                <Card>
                    <v-row wrap class="mb-3 mx-0">
                        <v-spacer />
                        <IconButton leftIcon="add_circle_outline" to="/create-account" color="var(--var-color-blue-sword)">
                            {{ $t('account.list.add') }}
                        </IconButton>
                    </v-row>
                    <v-data-table
                            :headers="headers"
                            :items="accounts"
                            :options.sync="pagination"
                            :sort-by="sortBy"
                            :sort-desc="sortDesc"
                            :footer-props="footer"
                    >
                        <template v-slot:body="{items}">
                            <tr v-for="item in items" :key="item.id">
                                <td :class="(item.isActive) ? 'active' : 'inactive'">{{ item.login }}</td>
                                <td :class="(item.isActive) ? 'active' : 'inactive'">{{ item.email }}</td>
                                <td :class="(item.isActive) ? 'active' : 'inactive'">{{ item.fullName }}</td>
                                <td :class="(item.isActive) ? 'active' : 'inactive'">
                                    <v-icon v-if="item.isAdmin">check</v-icon>
                                </td>
                                <td>
                                    <v-row wrap justify="center">
                                        <v-switch :disabled="me.id === item.id" v-model="item.isActive" @click.stop="confirmDisable(item)" color="var(--var-color-blue-sword)" />
                                    </v-row>
                                </td>
                                <td class="align-end">
                                    <v-btn v-if="item.isActive" icon :to="'/accounts/' + item.id">
                                        <v-icon>edit</v-icon>
                                    </v-btn>
                                    <v-btn v-else icon disabled>
                                        <v-icon>edit</v-icon>
                                    </v-btn>
                                </td>
                            </tr>
                        </template>
                    </v-data-table>
                </Card>
            </v-flex>
        </v-row>
        <Dialog title="disableConfirm.title"
                subTitle="disableConfirm.subTitle"
                content="disableConfirm.content"
                cancelButtonText="disableConfirm.cancelButton"
                cancelButtonIcon="exit_to_app"
                confirmButtonText="disableConfirm.confirmButton"
                confirmButtonIcon="thumb_up"
                :cancelAction="cancelDisable"
                :confirmAction="disable"
                :maxWidth="650"
                :display="displayDisableMessage"
        />

    </v-container>
</template>

<style lang="scss" scoped>
    .active {
        text-align: center;
    }

    .inactive {
        text-align: center;
        color: grey;
    }
</style>

<script lang="ts">
    import {AccountPatch} from '@/store/types'
    import {Component, Vue} from "vue-property-decorator"
    import { tableFooter } from "@/plugins/i18n"
    import {Account} from "@/api/accountApi"

    @Component({})
    export default class AllAccounts extends Vue {
        private pagination = {
            rowsPerPage: 10,
        }

        private sortBy = ['login']
        private sortDesc = [false, true]
        private displayDisableMessage = false
        private selectedAccount: Account | undefined = undefined

        private get headers() {
            return [
                {text: this.$t('account.list.login'), value: 'login', width: "25%", align: "center"},
                {text: this.$t('account.list.email'), value: 'email', width: "25%", align: "center"},
                {text: this.$t('account.list.fullName'), value: 'fullName', width: "25%", align: "center"},
                {text: this.$t('account.list.isAdmin'), value: 'isAdmin', width: "10%", align: "center"},
                {text: this.$t('account.list.status'), sortable: false, width: "10%", align: "center"},
                {text: '', value: 'actions', sortable: false, width: "5%"},
            ]
        }

        private get footer() {
            return tableFooter((key: string) => this.$t(key))
        }

        private get accounts(): Array<Account> {
            return this.$modules.accounts.allAccounts.map((account) => ({
                ...account
            }))
        }

        private get me() {
            return this.$modules.accounts.meAccount
        }

        private disable() {
            this.selectedAccount!.isActive = false
            this.updateAccount(this.selectedAccount!)
            this.cancelDisable()
        }

        private confirmDisable(account: Account) {
            if (account.isActive) {
                this.displayDisableMessage = true
                this.selectedAccount = account
            } else {
                account.isActive = true
                this.updateAccount(account)
            }
        }

        private cancelDisable() {
            this.displayDisableMessage = false
            this.selectedAccount = undefined
        }


        private async updateAccount(account: Account) {
            const patchRequest: AccountPatch = {
                pubKey: account.publicKey,
                company: account.company,
                email: account.email,
                password: null,
                fullName: account.fullName,
                isAdmin: account.isAdmin,
                isActive: account.isActive
            }

            await this.$modules.accounts.updateAccount(account.id, patchRequest)
        }

    }
</script>
