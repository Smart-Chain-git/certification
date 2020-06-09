<template>
    <v-container fluid>
        <v-row class="mx-3 mt-3" justify="space-between">
            <v-col class="pl-4 col-5" tag="h1">
                <template>{{$t("job.detail.title") }}</template>
            </v-col>
            <v-col>
                <IconButton leftIcon="arrow_back" @click="back">{{ $t("job.detail.back") }}</IconButton>
            </v-col>
        </v-row>
        <v-row>
            <v-flex lg6 md7 sm8 xs11>
                <Card>
                    <v-flex>
                        <EditFormRow :title="$t('job.detail.id')" :editable="false" :value="id"/>
                        <EditFormRow :title="$t('job.detail.name')" :editable="false" :value="name"/>
                        <EditFormRow :title="$t('job.detail.date')" :editable="false" :value="date"/>
                        <EditFormRow :title="$t('job.detail.statusDate')" :editable="false" :value="statusDate"/>
                        <EditFormRow :title="$t('job.detail.docs')" :editable="false" :value="docs"/>
                        <EditFormRow :title="$t('job.detail.channel')" :editable="false" :value="channel"/>
                        <EditFormRow :title="$t('job.detail.block')" :editable="false" :value="block"/>
                        <EditFormRow :title="$t('job.detail.transaction')" :editable="false" :value="transaction"/>
                        <EditFormRow :title="$t('job.detail.hash')" :editable="false" :value="hash"/>
                        <EditFormRow :title="$t('job.detail.rootHash')" :editable="false" :value="rootHash"/>
                    </v-flex>
                </Card>
            </v-flex>
        </v-row>
    </v-container>
</template>

<style scoped>

</style>
<script lang="ts">
    import {Component, Prop, Vue} from "vue-property-decorator"

    @Component
    export default class JobDetail extends Vue {
        @Prop(String) private readonly id!: string


        private calculated() {
            this.$modules.jobs.blablabla()
        }

        private back() {
            this.$router.push("/jobs")
        }

    }
</script>
