<template>
    <v-container fluid>
        <v-layout row class="mx-3 mt-3" justify="space-between">
            <v-col class="pl-4" tag="h1">
                <template>{{$t("dashboard.title") }}</template>
            </v-col>
        </v-layout>
        <v-layout row>
            <v-flex>
                <Card>
                    <p>
                        {{ $t("dashboard.welcome") }}<br/><br/>
                        {{ $t("dashboard.content") }}
                    </p>
                </Card>
            </v-flex>
        </v-layout>
        <v-layout row wrap>
            <v-flex xs12 lg3>
                <Card height="90%">
                    <h2>{{ month }}</h2>

                    <StatBoard :number="dashBoardStat.documentsSignedCount" :detail="$t('dashboard.documents')"/>
                    <StatBoard :number="dashBoardStat.jobsCreatedCount" :detail="$t('dashboard.jobsCreated')"/>
                    <StatBoard :number="dashBoardStat.jobsProcessedCount" :detail="$t('dashboard.jobsProcessed')"/>
                </Card>
            </v-flex>
            <v-flex xs12 lg9>
                <Card height="90%">
                    <h2>{{ $t("dashboard.lastJobs")}}</h2>

                    <v-data-table class="table-header mt-4"
                                  :headers="headers"
                                  :items="jobList"
                                  must-sort
                                  :hide-default-footer="true"
                    >
                        <template v-slot:body="{items}">
                            <tbody>
                            <tr :key="job.id" v-for="job in items" class="outlined">
                                <td class="text-center">
                                    <v-row>
                                        <v-col class="col-8 text-center">{{job.flowName}}</v-col>
                                        <v-col class="col-1 align-right">
                                            <CopyTooltip :copy="job.id" :label="$t('job.list.id')+' :'"
                                                         :actionText="$t('job.list.copyId')"/>
                                        </v-col>
                                    </v-row>
                                </td>
                                <td class="text-center">{{job.createdDate | formatTimestamp}}</td>
                                <td class="text-center">{{job.stateDate | formatTimestamp}}</td>
                                <td class="text-center">{{job.state}}</td>
                                <td class="text-center">{{job.docsNumber}}</td>
                                <td class="text-center">{{job.channelName}}</td>
                                <td class="text-center">
                                    <v-btn icon @click="gotoDetail(job.id)">
                                        <v-icon>zoom_in</v-icon>
                                    </v-btn>
                                </td>
                            </tr>
                            </tbody>
                        </template>
                    </v-data-table>
                    <v-flex class="mt-4 mb-8 text-center">
                        <IconButton @click="gotoJobs()" color="var(--var-color-blue-sword)"> {{ $t("dashboard.more")}}</IconButton>
                    </v-flex>
                </Card>
            </v-flex>
        </v-layout>
    </v-container>
</template>

<script lang="ts">

    import moment from "moment"
    import {Component, Vue} from "vue-property-decorator"

    @Component
    export default class Dashboard extends Vue {

        private get headers() {
            return [
                {text: this.$t("job.list.name"), align: "center", value: "flowName", sortable: false},
                {text: this.$t("job.list.creationDate"), align: "center", value: "createdDate", sortable: false},
                {text: this.$t("job.list.statusDate"), align: "center", value: "stateDate", sortable: false},
                {text: this.$t("job.list.status"), align: "center", value: "state", sortable: false},
                {text: this.$t("job.list.docs"), align: "center", value: "docs", sortable: false},
                {text: this.$t("job.list.channel"), align: "center", value: "channelName", sortable: false},
                {text: "", sortable: false},
            ]
        }

        private get month() {
            return this.$t("dashboard.monthStatistics").toString().replace("{0}", moment().format("MMMM"))
        }

        private get dashBoardStat() {
          return this.$modules.stat.dashBoardStat
        }

        private get jobList() {
            return this.$modules.jobs.getJobs
        }

        private gotoDetail(jobId: string) {
            this.$router.push("/jobs/" + jobId)
        }

        private gotoJobs() {
            this.$router.push("/jobs")
        }
    }
</script>

<style scoped>

</style>
