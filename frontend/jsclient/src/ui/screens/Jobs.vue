<template>
    <div>
        <h1>jobs</h1>
        <table>
            <thead>
                <tr>
                    <td>id</td>
                </tr>
            </thead>

            <tr v-for v-for="job in jobs" :key="job.id">
                <td>{{job.id}}</td>
            </tr>

        </table>
    </div>
</template>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator"
    import {Job, jobApi} from "@/api/jobApi"


    @Component
    export default class Jobs extends Vue {

        private jobs: Array<Job> = []


        private mounted() {
            jobApi.list("20180626_02236").then((response: Array<Job>) => {
                this.jobs = response
            })
        }


    }
</script>

<style scoped>

</style>
