import {AxiosResponse} from 'axios'
import {VuexModule, Module, Mutation, Action} from "vuex-class-modules"
import {Job, jobApi, JobCriteria} from "@/api/jobApi"

@Module
export default class JobsModule extends VuexModule {
    private jobList: Array<Job> = []
    private currentJob: Job | undefined = undefined

    @Action
    public async loadJobs(criteria: JobCriteria = {}) {
        await jobApi.list(criteria).then((response: Array<Job>) => {
            this.setJobs(response)
        })
    }

    public async loadJob(id: string) {
        await jobApi.getById(id).then((response: Job) => {
            this.setCurrentJob(response)
        })
    }

    @Mutation
    public setJobs(jobs: Array<Job>) {
        this.jobList = jobs
    }

    @Mutation
    public setCurrentJob(job: Job) {
        this.currentJob = job
    }


    public getJobs() {
        return this.jobList
    }

    public getCurrentJob() {
        return this.currentJob
    }
}
