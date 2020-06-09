import {VuexModule, Module, Mutation, Action} from "vuex-class-modules"
import {Job, jobApi, JobCriteria} from '@/api/jobApi'

@Module
export default class JobsModule extends VuexModule {
    private jobList: Array<Job> = []

    @Action
    public async loadJobs(criteria: JobCriteria = {}) {
        await jobApi.list(criteria).then((response: Array<Job>) => {
            this.setJobs(response)
        })
    }

    @Mutation
    public setJobs(jobs: Array<Job>) {
        this.jobList = jobs
    }


    public getJobs() {
        return this.jobList
    }
}
