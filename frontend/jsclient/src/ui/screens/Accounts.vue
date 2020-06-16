<template>
    <v-container fluid>
        <v-row class="mx-3 mt-3" justify="space-between">
            <v-col class="pl-4" tag="h1">
                <template>{{$t("job.list.title") }}</template>
            </v-col>
        </v-row>
        <v-row>
            <v-flex>
                <Card width="98%">
                    <v-data-table class="table-header"
                                  :headers="headers"
                                  :items="accounts"
                                  :footer-props="footer"
                                  :loading="loading"
                                  must-sort
                    >
                        <template v-slot:body="{items}">
                            <tbody>
                            <tr :key="account.id" v-for="account in items" class="outlined">
                                <td class="text-center">
                                    <v-row>
                                        <v-col class="col-8 text-center">{{account.login}}</v-col>
                                        <v-col class="col-1 align-right">
                                            <CopyTooltip :copy="account.id" :label="$t('account.list.id')+' :'"
                                                         :actionText="$t('account.list.copyId')"/>
                                        </v-col>
                                    </v-row>
                                </td>
                                <td class="text-center">{{account.email}}</td>
                                <td class="text-center">{{account.fullName}}</td>
                                <td class="text-center">{{job.docsNumber}}</td>
                                <td class="text-center">{{job.channelName}}</td>v
                                <td class="text-center">
                                    <v-btn icon @click="gotoDocument(job.id)">
                                        <v-icon>description</v-icon>
                                    </v-btn>
                                    <v-btn icon @click="gotoDetail(job.id)">
                                        <v-icon>zoom_in</v-icon>
                                    </v-btn>
                                </td>
                            </tr>
                            </tbody>
                        </template>
                    </v-data-table>
                </Card>
            </v-flex>
        </v-row>
    </v-container>
</template>

<script lang="ts">

    import {Component, Vue} from "vue-property-decorator"
    import {Account} from "@/api/accountApi"
    import {tableFooter} from "@/plugins/i18n"

    @Component
    export default class Accounts extends Vue {

        private get loading(): boolean {
            return this.$modules.accounts.getLoading
        }

        private get accounts(): Array<Account> {
            return this.$modules.accounts.allAccounts
        }

        private get headers() {
            return [
                {text: this.$t("account.list.login"), align: "center", value: "login"},
                {text: this.$t("account.list.email"), align: "center", value: "email"},
                {text: this.$t("account.list.fullname"), align: "center", value: "fullname"},
                {text: this.$t("account.list.isAdmin"), align: "center", value: "isAdmin"},
                {text: this.$t("account.list.status"), align: "center", value: "status"},
            ]
        }

        private get footer() {
            return tableFooter((key: string) => this.$t(key))
        }
    }
</script>

<style scoped>

</style>