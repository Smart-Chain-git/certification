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
                    <v-flex>
                        <v-row>
                            <v-col>
                                <v-row>
                                    <v-col class="col-2 filter-frame">
                                        <v-text-field :label="$t('job.list.name')"
                                                      outlined
                                                      dense
                                                      v-model="selectedName"
                                                      color="var(--var-color-blue-sword)"/>
                                    </v-col>
                                    <v-col class="col-2 filter-frame">
                                        <v-text-field :label="$t('job.list.id')"
                                                      outlined
                                                      dense
                                                      v-model="selectedID"
                                                      color="var(--var-color-blue-sword)"/>
                                    </v-col>
                                    <v-col class="col-2 filter-frame">
                                        <EditFormDateRange
                                                v-model="selectedDates"
                                                :updateRange="updateRange"
                                                :label="$t('job.list.dates')"
                                                color="var(--var-color-blue-sword)"
                                        />
                                    </v-col>
                                    <v-col class="col-2 filter-frame">
                                        <v-combobox :items="allChannels"
                                                    v-model="selectedChannel"
                                                    outlined
                                                    dense
                                                    :label="$t('job.list.channel')"
                                                    color="var(--var-color-blue-sword)"
                                                    hide-details
                                        />
                                    </v-col>
                                    <v-col class="col-4">
                                        <IconButton @click="search()"
                                                    color="var(--var-color-blue-sword)"
                                                    leftIcon="zoom_in">
                                            {{ $t("job.list.search") }}
                                        </IconButton>
                                        <span class="ml-2 records">{{ jobList.length }} {{ $t("job.list.records")}}</span>
                                    </v-col>
                                </v-row>
                            </v-col>
                            <v-col class="col-2 align-right">
                                <v-row>
                                    <v-col>
                                        <IconButton @click="exportCSV()"
                                                    color="var(--var-color-blue-sword)"
                                                    leftIcon="get_app">
                                            {{ $t("job.list.export") }}
                                        </IconButton>
                                    </v-col>
                                </v-row>
                            </v-col>
                        </v-row>
                    </v-flex>
                    <v-data-table class="table-header"
                                  :headers="headers"
                                  :items="jobList"
                                  :sort-by="sortBy"
                                  :sort-desc="sortDesc"
                                  :footer-props="footer">
                        <template v-slot:body="{items}">
                            <tbody>
                                <tr :key="job.id" v-for="job in items" class="outlined">
                                    <td class="text-center">
                                        <v-row>
                                            <v-col class="col-8 text-center">{{job.flowName}}</v-col>
                                            <v-col class="col-1 align-right">
                                                <CopyTooltip :copy="job.id" :label="$t('job.list.id')+' :'" :actionText="$t('job.list.copyId')"/>
                                            </v-col>
                                        </v-row>
                                    </td>
                                    <td class="text-center">{{job.createdDate | formatDate}}</td>
                                    <td class="text-center">{{job.stateDate | formatDate}}</td>
                                    <td class="text-center">{{job.state}}</td>
                                    <td class="text-center">{{job.docsNumber}}</td>
                                    <td class="text-center">{{job.channelName}}</td>
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

<style scoped>

    .align-right {
        text-align: right;
    }

    .filter-frame::v-deep .v-input--selection-controls__input {
        height: 16px !important;
        width: 16px !important;
    }

    .filter-frame::v-deep .v-input--selection-controls__ripple {
        height: 16px !important;
        width: 16px !important;
        margin: 8px !important;
    }

    .filter-frame::v-deep .v-label {
        font-size: 14px !important;
    }

    .filter-frame::v-deep .v-icon {
        font-size: 16px !important;
    }

    .records {
        font-size: 12px;
        font-style: italic;
    }

    .v-text-field {
        font-size: 12px;
    }

    .outlined > td {
        border-bottom: 1px solid #cccccc;
    }
</style>
<style>
    .v-data-table-header tr {
        background-color: #cccccc;
    }

    .v-data-table-header tr th {
        color: white !important;
    }

    .v-data-table-header tr th .v-icon {
        color: white !important;
    }

</style>
<script lang="ts">
    import {JobCriteria} from "@/api/jobApi"
    import {tableFooter} from "@/plugins/i18n"
    import {Component, Vue} from "vue-property-decorator"

    @Component
    export default class Jobs extends Vue {

        private sortBy = []
        private sortDesc = []
        private selectedName: string | undefined = undefined
        private selectedID: string | undefined = undefined
        private selectedDates: Array<string | undefined> = []
        private selectedChannel: string | undefined = undefined
        private allChannels: Array<string | undefined> = []

        private get jobList() {
            return this.$modules.jobs.getJobs()
        }

        private get headers() {
            return [
                {text: this.$t("job.list.name"), align: "center", value: "flowName"},
                {text: this.$t("job.list.creationDate"), align: "center", value: "createdDate"},
                {text: this.$t("job.list.statusDate"), align: "center", value: "stateDate"},
                {text: this.$t("job.list.status"), align: "center", value: "state"},
                {text: this.$t("job.list.docs"), align: "center", value: "docs"},
                {text: this.$t("job.list.channel"), align: "center", value: "channelName"},
                {text: "", sortable: false},
            ]
        }

        private get footer() {
            return tableFooter((key: string) => this.$t(key))
        }

        private updateRange() {
            this.selectedDates.sort((a: string | undefined, b: string | undefined) => {
                return (Date.parse(a!) > Date.parse(b!)) ? 1 : -1
            })

            if (this.selectedDates[0] != null && this.selectedDates[1] != null) {
                const date: Date = new Date(this.selectedDates[0])
                const toDate: Date = new Date(Date.parse(this.selectedDates[1]))
                // Set the same time for both hours to avoid time issues when comparing dates of different timezones.
                date.setHours(23, 59, 59, 999)
                toDate.setHours(23, 59, 59, 999)

                while (date <= toDate) {
                    this.selectedDates[1] = date.toISOString().split("T")[0]
                    date.setDate(date.getDate() + 1)
                }
            }
        }

        private mounted() {
            this.allChannels = this.$modules.jobs.getJobs().map((j) => j.channelName).filter((c) => c !== undefined)
            this.allChannels = ["", ...this.allChannels]
        }

        private async search() {
            if (this.selectedName === "") {
                this.selectedName = undefined
            }
            if (this.selectedID === "") {
                this.selectedID = undefined
            }
            if (this.selectedChannel === "") {
                this.selectedChannel = undefined
            }
            if (this.selectedDates[0] === "") {
                this.selectedDates[0] = undefined
            }
            if (this.selectedDates[1] === "") {
                this.selectedDates[1] = undefined
            }

            const criteria: JobCriteria = {
                accountId: this.$modules.accounts.meAccount?.id,
                flowName: this.selectedName,
                id: this.selectedID,
                dateBegin: this.selectedDates[0],
                dateEnd: this.selectedDates[1],
                channel: this.selectedChannel,
            }
            await this.$modules.jobs.loadJobs(criteria)
        }

        private async exportCSV() {
            // TODO
        }

        private gotoDetail(jobId: string) {
            this.$router.push("/jobs/" + jobId)
        }

        private gotoDocument(jobId: string) {
            this.$router.push("/documents/" + jobId)
        }

    }
</script>
