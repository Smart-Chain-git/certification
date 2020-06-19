<template>
    <v-container fluid>
        <v-flex xs11>
            <v-row class="mx-3 mt-3" justify="space-between">
                <v-col class="pl-4" tag="h1">
                    <h1>{{$t("documents.title") }}</h1>
                </v-col>
                <v-col class="align-right">
                    <IconButton leftIcon="arrow_back" @click="back" color="var(--var-color-blue-sword)">{{ $t("documents.back") }}</IconButton>
                </v-col>
            </v-row>
            <v-row>
                <Card>
                    <v-flex>
                        <v-row>
                            <v-col class="col-2 filter-frame">
                                <v-text-field :label="$t('documents.jobId')"
                                              outlined
                                              dense
                                              v-model="filter.jobId"
                                              color="var(--var-color-blue-sword)"
                                              clearable/>
                            </v-col>
                            <v-col class="col-2 filter-frame">
                                <v-text-field :label="$t('documents.name')"
                                              outlined
                                              dense
                                              v-model="filter.name"
                                              color="var(--var-color-blue-sword)"
                                              clearable/>
                            </v-col>
                            <v-col class="col-2 filter-frame">
                                <EditFormDateRange
                                        v-model="filter.dates"
                                        :updateRange="updateRange"
                                        :label="$t('documents.dates')"
                                        color="var(--var-color-blue-sword)"
                                />
                            </v-col>
                            <v-col class="col-4">
                                <IconButton @click="search()"
                                            color="var(--var-color-blue-sword)"
                                            leftIcon="zoom_in">
                                    {{ $t("documents.search") }}
                                </IconButton>
                                <span class="ml-2 records">{{ documentCount }} {{ $t("documents.records")}}</span>
                            </v-col>
                        </v-row>
                    </v-flex>
                    <v-data-table class="table-header"
                                  :headers="headers"
                                  :items="documentList"
                                  :footer-props="footer"
                                  :options.sync="pagination"
                                  :server-items-length="documentCount"
                                  :loading="loading"
                                  must-sort
                    >
                        <template v-slot:body="{items}">
                            <tbody>
                            <tr :key="doc.id" v-for="doc in items" class="outlined">
                                <td class="text-center">{{doc.metadata.fileName}}</td>
                                <td class="text-center">{{doc.hash}}</td>
                                <td class="text-center"><a @click="gotoDetail(doc.jobId)">{{doc.jobId}}</a></td>
                                <td class="text-center">
                                    <IconButton leftIcon="check" @click="getProof(doc.id)" color="var(--var-color-blue-sword)">{{ $t("documents.proof") }}</IconButton>
                                </td>
                            </tr>
                            </tbody>
                        </template>
                    </v-data-table>
                </Card>
            </v-row>
        </v-flex>
    </v-container>
</template>

<script lang="ts">
    import {tableFooter} from "@/plugins/i18n"
    import {FileFilterOption, PaginationOption} from "@/store/types"
    import {Component, Vue, Watch} from "vue-property-decorator"

    @Component
    export default class Documents extends Vue {

        private filter: FileFilterOption = {
            accountId: undefined,
            name: undefined,
            jobId: undefined,
            id: undefined,
            hash: undefined,
            dates: [],
        }

        private pagination: PaginationOption = {
            itemsPerPage: 10,
            page: 1,
            sortBy: [],
            sortDesc: [],
        }

        private get loading() {
            return this.$modules.jobs.getLoading
        }

        private get documentList() {
            return this.$modules.files.getFiles
        }

        private get documentCount() {
            return this.$modules.files.getFileCount
        }

        private get headers() {
            return [
                {text: this.$t("documents.name"), align: "center", value: "id", width: "25%"},
                {text: this.$t("documents.hash"), align: "center", value: "hash", width: "25%"},
                {text: this.$t("documents.jobId"), align: "center", value: "jobId", width: "25%"},
                {text: "", sortable: false, width: "25%"},
            ]
        }

        private get footer() {
            return tableFooter((key: string) => this.$t(key))
        }

        private updateRange() {
            this.filter.dates.sort((a: string | undefined, b: string | undefined) => {
                return (Date.parse(a!) > Date.parse(b!)) ? 1 : -1
            })

            if (this.filter.dates[0] != null && this.filter.dates[1] != null) {
                const date: Date = new Date(this.filter.dates[0])
                const toDate: Date = new Date(Date.parse(this.filter.dates[1]))
                // Set the same time for both hours to avoid time issues when comparing dates of different timezones.
                date.setHours(23, 59, 59, 999)
                toDate.setHours(23, 59, 59, 999)

                while (date <= toDate) {
                    this.filter.dates[1] = date.toISOString().split("T")[0]
                    date.setDate(date.getDate() + 1)
                }
            }
        }

        @Watch("pagination")
        private search() {
            this.$modules.files.setFilter(this.filter)
            this.$modules.files.setPagination(this.pagination)
            this.$modules.files.loadFiles()
        }

        private back() {
            this.$router.go(-1)
        }

        private gotoDetail(jobId: string) {
            this.$router.push("/jobs/" + jobId)
        }

        private getProof(fileId: string) {
            this.$modules.files.loadProof(fileId).then(() => {
                const a = document.createElement("a")
                a.setAttribute("download", this.$modules.files.getProof()!.file_name + ".json")
                a.setAttribute("href", "data:text/json:charset=utf-8," +
                    encodeURIComponent(JSON.stringify(this.$modules.files.getProof())))
                a.click()
            })
        }

        private mounted() {
            this.filter = this.$modules.files.getFilter()
        }
    }
</script>

<style scoped>

</style>
