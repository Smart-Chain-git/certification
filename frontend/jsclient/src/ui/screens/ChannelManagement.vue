<template>
    <v-container fluid>
        <v-row class="mx-3 mt-3" justify="space-between">
            <v-col class="pl-4" tag="h1">
                <template>{{$t('channelManagement.title') }}</template>
            </v-col>
        </v-row>
        <v-row>
            <v-flex lg8 md8 sm8 xs11>
                <Card>
                    <CardTitle icon="add">{{ $t("channelManagement.new")}}</CardTitle>
                    <v-flex xs5>
                        <EditFormRow :title="$t('channelManagement.name')" :editable="true">
                            <EditFormTitleEdit v-model.trim="name" :placeholder="$t('channelManagement.name')" color="var(--var-color-blue-sword)"></EditFormTitleEdit>
                        </EditFormRow>
                        <EditFormRow :title="$t('channelManagement.expirationDate')" :editable="true">
                            <EditFormDate
                                    v-model="date"
                                    :placeholder="$t('channelManagement.date')"
                                    color="var(--var-color-blue-sword)"
                                    :min="now"
                            />
                        </EditFormRow>
                        <EditFormRow :editable="true">
                            <IconButton color="var(--var-color-blue-sword)" @click="add" :disabled="name === '' || date === ''" leftIcon="add_circle_outline">
                                {{ $t('channelManagement.add') }}
                            </IconButton>
                        </EditFormRow>
                    </v-flex>
                    <CardTitle icon="list" class="mt-7">{{ $t("channelManagement.all") }}</CardTitle>
                    <v-data-table
                            :items="tokens"
                            class="table-header"
                            :headers="headers"
                            :footer-props="footer"
                            :custom-sort="customSort"
                    >
                        <template v-slot:body="{items}">
                            <tbody>
                            <tr :key="token.id" v-for="token in items" :class="!canRevoke(token) ? 'outline_revoked' : 'outline'">
                                <td class="text-center">
                                    <v-row>
                                        <v-col class="col-10 text-center">{{token.name }}</v-col>
                                        <v-col class="col-2 align-right">
                                            <CopyTooltip v-if="canRevoke(token)" :copy="token.jwtToken" :label="$t('channelManagement.tokenJwt')+' :'"
                                                         :actionText="$t('channelManagement.copyToken')"/>
                                        </v-col>
                                    </v-row>
                                </td>
                                <td class="text-center">{{token.creationDate | formatDate}}</td>
                                <td class="text-center">{{token.expirationDate | formatDate}}</td>
                                <td class="text-center">
                                    <IconButton color="var(--var-color-blue-sword)" @click="revoke(token.id)" v-if="canRevoke(token)" leftIcon="block">
                                        {{ $t("channelManagement.revoke") }}
                                    </IconButton>
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

<style lang="scss" scoped>
    .outline_revoked {
        color: gray;
    }
</style>

<script lang="ts">

    import {tableFooter} from "@/plugins/i18n"
    import {Token, TokenCreateRequest} from "@/api/types"
    import {Component, Vue} from "vue-property-decorator"
    import moment from "moment"

    @Component
    export default class ChannelManagement extends Vue {
        private static compareItem(a: Token, b: Token, index: string, isDesc: boolean): number {
            const order = (!isDesc ? 1 : -1)
            switch (index) {
                case "name":
                    return order * a.name.localeCompare(b.name)

                case "expirationDate":
                    const a2 = a["expirationDate"] ? Number(new Date(a.expirationDate)) : Number(new Date(0))
                    const b2 = b["expirationDate"] ? Number(new Date(b.expirationDate)) : Number(new Date(0))
                    return order * (b2 - a2)

                case "creationDate":
                    const a3 = a["creationDate"] ? Number(new Date(a.creationDate)) : Number(new Date(0))
                    const b3 = b["creationDate"] ? Number(new Date(b.creationDate)) : Number(new Date(0))
                    return order * (b3 - a3)

                case "revoked":
                    if (a.revoked === b.revoked) {
                        return 0
                    }
                    return (a.revoked > b.revoked) ? 1 : -1
                default:
                    return 0
            }
        }

        private name: string = ""
        private date: string = ""

        private get tokens() {
            return this.$modules.tokens.getTokens()
        }

        private customSort(items: Array<Token>, sortBy: Array<string>, sortDesc: Array<boolean>) {
            sortBy = ["revoked", ...sortBy]
            sortDesc = [false, ...sortDesc]

            return items.sort((a, b) => {
                for (let cpt = 0; cpt < sortBy.length; cpt++) {
                    const intermediate = ChannelManagement.compareItem(a, b, sortBy[cpt], sortDesc[cpt])
                    if (intermediate !== 0) {
                        return intermediate
                    }
                }
                return 0
            })
        }

        private get headers() {
            return [
                {text: this.$t("channelManagement.name"), align: "center", value: "name", width: "25%"},
                {text: this.$t("channelManagement.creationDate"), align: "center", value: "creationDate", width: "25%"},
                {text: this.$t("channelManagement.expirationDate"), align: "center", value: "expirationDate", width: "25%"},
                {text: "", sortable: false, width: "25%"},
            ]
        }

        private get footer() {
            return tableFooter((key: string) => this.$t(key))
        }

        private revoke(tokenId: string) {
            this.$modules.tokens.revokeToken(tokenId)
        }

        private add() {
            const token: TokenCreateRequest = {
                name: this.name,
                expirationDate: new Date(this.date),
            }

            this.$modules.tokens.createToken(token).then(() => {
                this.name = ""
                this.date = ""
            })
        }

        private canRevoke(token: Token) {
            if (token.revoked) {
                return false
            }
            if (!token["expirationDate"]) {
                return true
            }
            return (new Date(token.expirationDate) > new Date(moment().format("YYYY-MM-DD")))
        }

    }
</script>

