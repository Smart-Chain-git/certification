<template>
    <v-container fluid>
        <v-row class="mx-3 mt-3" justify="space-between">
            <v-col class="pl-4" tag="h1">
                <template>{{$t("job.list.title") }}</template>
            </v-col>
        </v-row>
        <v-row>
            <v-flex xs11>
                <Card>
                    <v-flex>
                        <v-row>
                            <v-col class="filter-frame">
                                <v-text-field :placeholder="$t('job.list.name')" outlined dense></v-text-field>
                               <!-- <div class="filterBox">
                                    <v-checkbox :key="document.id" :label="document.name"
                                                :ripple="false"
                                                :value="document"
                                                color="primary"
                                                hide-details
                                                v-for="document in documents"
                                                v-model="selectedDocuments"
                                    />
                                </div>-->
                            </v-col>
                            <v-col class="filter-frame">
                                <!--<div style="font-weight: bold">
                                    {{ $t('job.list.filters.id') }}
                                </div>

                                <div class="filterBox">
                                    <v-checkbox :key="state.id" :label="state.name"
                                                :ripple="false"
                                                :value="state"
                                                color="primary"
                                                hide-details
                                                v-for="state in milestoneStates"
                                                v-model="selectedMilestoneStates"
                                    />
                                </div>-->
                                <v-text-field :placeholder="$t('job.list.id')" outlined dense></v-text-field>
                            </v-col>


                            <v-col class="filter-frame">
                                <!--<div style="font-weight: bold">
                                    {{ $t('job.list.date') }}
                                </div>
                                <div class="filterBox">
                                    <v-checkbox :key="campaign.id" :label="campaign.name"
                                                :ripple="false"
                                                :value="campaign"
                                                color="primary"
                                                hide-details
                                                multiple
                                                v-for="campaign in campaigns"
                                                v-model="selectedCampaigns"
                                    />
                                </div>-->
                            </v-col>

                            <v-col class="filter-frame">
                                <!--<div style="font-weight: bold">
                                    {{ $t('job.list.channel') }}
                                </div>
                                <div class="filterBox">
                                    <v-checkbox :key="state.id" :label="state.name"
                                                :ripple="false"
                                                :value="state"
                                                color="primary"
                                                hide-details
                                                v-for="state in messageStates"
                                                v-model="selectedMessageStates"
                                    />
                                </div>-->
                                <div class="filterBox">
                                    <v-checkbox :key="channel" v-for="channel in channels"
                                                :label="channel"
                                                :ripple="false"
                                                hide-details
                                                multiple/>
                                </div>
                            </v-col>

                            <v-col>
                                <IconButton @click="refreshData()" color="secondary"
                                            leftIcon="filter_list"
                                            small>
                                    {{ $t('job.list.search') }}
                                </IconButton>
                                <br/>
                                <IconButton @click="resetFilter()" class="mt-4" color="secondary"
                                            leftIcon="clear" outlined small text>
                                    {{ $t('all-messages.clearFilter') }}
                                </IconButton>
                            </v-col>
                        </v-row>

                    </v-flex>
                    <v-data-table :headers="headers" :items="joblist" :sort-by="sortBy" :sort-desc="sortDesc" :footer-props="footer">
                        <template v-slot:body="{items}">
                            <tr :key="job.id" v-for="job in items">
                                <td>
                                    <table>
                                        <tr>
                                            <td style="width:80%" class="text-center">{{job.flowName}}</td>
                                            <td class="align-right">
                                                <IconTooltip icon="info" :text="job.id"/>
                                            </td>
                                        </tr>
                                    </table>
                                </td>
                                <td class="text-center">{{job.createdDate | formatDate}}</td>
                                <td class="text-center">{{job.stateDate | formatDate}}</td>
                                <td class="text-center">{{job.state}}</td>
                                <td class="text-center">{{job.docsNumber}}</td>
                                <td class="text-center">{{job.channelName}}</td>
                                <td class="text-center">
                                    <v-btn icon>
                                        <v-icon>edit</v-icon>
                                    </v-btn>
                                    <v-btn icon>
                                        <v-icon>zoom_in</v-icon>
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

<script lang="ts">
    import {Job} from '@/api/jobApi'
    import {tableFooter} from "@/plugins/i18n"
    import {Component, Vue} from "vue-property-decorator"

    @Component
    export default class Jobs extends Vue {

        private sortBy = []
        private sortDesc = []

        private get joblist() {
            return this.$modules.jobs.getJobs()
        }

        private get headers() {
            return [
                {text: this.$t("job.list.name"), align: "center", value: "id"},
                {text: this.$t("job.list.creationDate"), align: "center", value: "creationDate"},
                {text: this.$t("job.list.statusDate"), align: "center", value: "statusDate"},
                {text: this.$t("job.list.status"), align: "center", value: "status"},
                {text: this.$t("job.list.docs"), align: "center", value: "docs"},
                {text: this.$t("job.list.channel"), align: "center", value: "channelName"},
                {text: "", sortable: false},
            ]
        }

        private get footer() {
            return tableFooter((key: string) => this.$t(key))
        }

        private get channels() {
            console.log(this.joblist.map((j: Job) => j.channelName))
            return this.joblist.map((j: Job) => j.channelName)
        }

    }
</script>

<style scoped>
    .align-right {
        text-align: right;
    }
    .filterBox {
        height: 100px;
        overflow-y: auto;
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

</style>
