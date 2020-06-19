<template>
    <v-container fluid>
        <v-row class="mx-3 mt-3" justify="space-between">
            <v-col class="pl-4 col-5" tag="h1">
                <template>{{$t("job.detail.title") }}</template>
            </v-col>
            <v-col>
                <IconButton leftIcon="arrow_back" @click="back" color="var(--var-color-blue-sword)">{{ $t("job.detail.back") }}</IconButton>
            </v-col>
        </v-row>
        <v-row>
            <v-flex lg6 md7 sm8 xs11>
                <Card>
                    <v-flex>
                        <EditFormRow :title="$t('job.detail.id')" :editable="false" :value="currentJob.id"/>
                        <EditFormRow :title="$t('job.detail.name')" :editable="false" :value="currentJob.flowName"/>
                        <EditFormRow :title="$t('job.detail.date')" :editable="false" :value="currentJob.createdDate"/>
                        <EditFormRow :title="$t('job.detail.statusDate')" :editable="false" :value="currentJob.stateDate"/>
                        <EditFormRow :title="$t('job.detail.status')" :editable="false" :value="currentJob.state"/>
                        <EditFormRow :title="$t('job.detail.docs')" :editable="true">
                            <a class="mt-3" @click="gotoDocuments(currentJob.id)">{{ currentJob.docsNumber }}</a>
                        </EditFormRow>
                        <EditFormRow :title="$t('job.detail.channel')" :editable="false" :value="currentJob.channelName"/>
                        <EditFormRow :title="$t('job.detail.block')" :editable="false" :value="currentJob.blockHash"/>
                        <EditFormRow :title="$t('job.detail.transaction')" :editable="false" :value="currentJob.txHash"/>
                        <EditFormRow :title="$t('job.detail.hash')" :editable="false" :value="currentJob.algorithm"/>
                        <EditFormRow :title="$t('job.detail.rootHash')" :editable="false" :value="currentJob.rootHash"/>
                    </v-flex>
                </Card>
            </v-flex>
        </v-row>
    </v-container>
</template>

<style scoped>
</style>
<script lang="ts">
    import {Component, Vue} from "vue-property-decorator"

    @Component
    export default class JobDetail extends Vue {

        private get currentJob() {
            return this.$modules.jobs.getCurrentJob()
        }

        private back() {
            this.$router.push("/jobs")
        }

        private gotoDocuments(job: string) {
            this.$modules.files.setFilter({
                accountId: this.$modules.accounts.meAccount!.id,
                dates: [],
                jobId: job,
            })
            this.$router.push("/documents")
        }

    }
</script>
