import {FilterOption, PaginationOption} from "@/store/types"
import {VuexModule, Module, Mutation, Action} from "vuex-class-modules"
import {Job, jobApi, JobCriteria} from "@/api/jobApi"

@Module
export default class JobsModule extends VuexModule {
    private jobList: Array<Job> = []
    private currentJob: Job | undefined = undefined
    private jobCount: number = 0
    private navigationOptions: PaginationOption = {
        page: 1,
        itemsPerPage: 10,
        sortBy: [],
        sortDesc: [],
    }
    private filters: FilterOption = {
        flowName: "",
        id: "",
        dates: [],
        channelName: "",
    }

    @Action
    public async loadJobs(criteria: JobCriteria = {}) {
        await jobApi.list(criteria).then((response: Array<Job>) => {
            this.setJobs(response)
        })
        await jobApi.count(criteria).then((response: number) => {
            this.setJobCount(response)
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

    @Mutation
    public setJobCount(count: number) {
        this.jobCount = count
    }

    public getJobCount() {
        return this.jobCount
    }

    @Mutation
    public setPagination(pg: PaginationOption) {
        this.navigationOptions = pg
    }

    public getPagination() {
        return this.navigationOptions
    }

    @Mutation
    public setFilter(f: FilterOption) {
        this.filters = f
    }

    public getFilter() {
        return this.filters
    }

    @Action
    public filterUpdate() {
        if (this.filters.flowName === "") {
            this.filters.flowName = undefined
        }
        if (this.filters.id === "") {
            this.filters.id = undefined
        }
        if (this.filters.channelName === "") {
            this.filters.channelName = undefined
        }
        if (this.filters.dates[0] === "") {
            this.filters.dates[0] = undefined
        }
        if (this.filters.dates[1] === "") {
            this.filters.dates[1] = undefined
        }
    }

}
