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
                        <IconButton leftIcon="add_circle_outline" to="/create-account">
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
                                    <v-checkbox :disabled="item.id === me.id" v-model="item.isAdmin" @click.stop="setAdmin(item)" ></v-checkbox>
                                </td>
                                <td>
                                    <v-switch :disabled="me.id === item.id" v-model="item.isActive" @click.stop="disable(item)" color="primary" />
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

        private get headers() {
            return [
                {text: this.$t('account.list.login'), value: 'login', width: "18%", align: "center"},
                {text: this.$t('account.list.email'), value: 'email', width: "18%", align: "center"},
                {text: this.$t('account.list.fullName'), value: 'fullName', width: "18%", align: "center"},
                {text: this.$t('account.list.isAdmin'), value: 'isAdmin', width: "18%", align: "center"},
                {text: this.$t('account.list.status'), value: 'status', width: "18%", align: "center"},
                {text: '', value: 'actions', sortable: false, width: "10%"},
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

        private async disable(account: Account) {
            await this.updateAccountActive(account).then(() => {
                account.isActive = !account.isActive
            })
        }

        private setAdmin(account: Account) {
            this.updateAccountAdmin(account)
        }

        private async updateAccountActive(account: Account) {
            const patchRequest: AccountPatch = {
                email: account.email,
                password: null,
                fullName: account.fullName,
                isAdmin: account.isAdmin,
                isActive: !account.isActive
            }

            await this.$modules.accounts.updateAccount(account.id, patchRequest)
        }

        private async updateAccountAdmin(account: Account) {
            const patchRequest: AccountPatch = {
                email: account.email,
                password: null,
                fullName: account.fullName,
                isAdmin: !account.isAdmin,
                isActive: account.isActive
            }

            await this.$modules.accounts.updateAccount(account.id, patchRequest)
        }

    }
</script>
