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
                            <EditFormTitleEdit v-model.trim="name" :label="$t('channelManagement.name')" color="var(--var-color-blue-sword)"></EditFormTitleEdit>
                        </EditFormRow>
                        <EditFormRow :title="$t('channelManagement.expirationDate')" :editable="true">
                            <EditFormDate
                                    v-model="date"
                                    :label="$t('channelManagement.date')"
                                    color="var(--var-color-blue-sword)"
                            />
                        </EditFormRow>
                        <EditFormRow :editable="true">
                            <IconButton color="var(--var-color-blue-sword)" @click="add" :disabled="name === '' || date === ''" leftIcon="block">
                                {{ $t('channelManagement.add') }}
                            </IconButton>
                        </EditFormRow>
                    </v-flex>
                    <CardTitle icon="list">{{ $t("channelManagement.all") }}</CardTitle>
                    <v-data-table
                            :items="tokens"
                            class="table-header"
                            :headers="headers"
                            :footer-props="footer"
                    >
                        <template v-slot:body="{items}">
                            <tbody>
                            <tr :key="token.id" v-for="token in items" :class="(token.revoked) ? 'outline_revoked' : 'outline'">
                                <td class="text-center">{{ token.name }}</td>
                                <td class="text-center">{{ now | formatDate}}</td>
                                <td class="text-center">{{token.expirationDate | formatDate}}</td>
                                <td class="text-center">
                                    <IconButton color="var(--var-color-blue-sword)" @click="revoke(token.id)" :disabled="token.revoked" leftIcon="block">
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
    import {Token, TokenCreateRequest, TokenPatch} from "@/api/tokenApi"
    import {tableFooter} from "@/plugins/i18n"
    import {EditFormRow, Card, CardTitle, EditFormTitleEdit} from "@/ui/components"
    import {Component, Vue} from "vue-property-decorator"

    @Component
    export default class ChannelManagement extends Vue {
        private name: string = ""
        private date: string = ""
        private now: Date = new Date()

        private get tokens() {
            return this.$modules.tokens.getTokens()
        }

        private get headers() {
            return [
                {text: this.$t("channelManagement.name"), align: "center", value: "name", width: "25%"},
                {text: this.$t("channelManagement.date"), align: "center", width: "25%"},
                {text: this.$t("channelManagement.expirationDate"), align: "center", value: "expirationDate", width: "25%"},
                {text: "", sortable: false, width: "25%"},
            ]
        }

        private get footer() {
            return tableFooter((key: string) => this.$t(key))
        }

        private revoke(tokenId: string) {
            const update: TokenPatch = {
                revoked: true,
            }
            this.$modules.tokens.updateToken(tokenId, update)
        }

        private add() {
            const token: TokenCreateRequest = {
                name: this.name,
                expirationDate: new Date(this.date),
            }

            this.$modules.tokens.createToken(token)
        }

    }
</script>

