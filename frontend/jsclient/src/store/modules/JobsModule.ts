
import {FilterOption, PaginationOption} from "@/store/types"
import {Job, JobCriteria} from "@/api/types"
import {Action, Module, Mutation, VuexModule} from "vuex-class-modules"
import {jobApi} from "@/api/jobApi"
import AccountsModule from "@/store/modules/AccountsModule"
import deepcopy from "ts-deepcopy"

@Module
export default class JobsModule extends VuexModule {

    private accountsModule: AccountsModule

    private isLoading: boolean = false
    private jobList: Array<Job> = []
    private currentJob: Job | undefined = undefined
    private jobCount: number = 0
    private paginationOption: PaginationOption = {
        page: 1,
        itemsPerPage: 10,
        sortBy: [],
        sortDesc: [],
    }
    private filter: FilterOption = {
        flowName: "",
        id: "",
        dates: [],
        channelName: "",
    }

    constructor(options: any) {
        super(options)
        this.accountsModule = options.accountsModule
    }

    @Action
    public async loadJobs() {
        this.setLoading(true)
        this.filterUpdate()
        const sorts: Array<string> = []
        for (let i = 0; i < this.paginationOption.sortBy.length; ++i) {
            sorts.push(this.paginationOption.sortBy[i] + ":" + ((this.paginationOption.sortDesc[i]) ? "desc" : "asc"))
        }

        const criteria: JobCriteria = {
            accountId: this.accountsModule.meAccount?.id,
            flowName: this.filter.flowName,
            id: this.filter.id,
            dateBegin: this.filter.dates[0],
            dateEnd: this.filter.dates[1],
            channel: this.filter.channelName,
            sort: sorts,
            page: this.paginationOption.page - 1,
            size: this.paginationOption.itemsPerPage,
        }


        await jobApi.list(criteria).then((response: Array<Job>) => {
            this.setJobs(response)
        })
        await jobApi.count(criteria).then((response: number) => {
            this.setJobCount(response)
        })
        this.setLoading(false)
    }

    @Action
    public async loadLastJobs(count: number) {
        const criteria: JobCriteria = {
            accountId: this.accountsModule.meAccount?.id,
            page: 0,
            sort: ["creationDate:desc"],
            size: count,
        }
        await jobApi.list(criteria).then((response: Array<Job>) => {
            this.setJobs(response)
        })
    }


    public get getLoading(): boolean {
        return this.isLoading
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

    public get getJobs(): Array<Job> {
        return this.jobList
    }

    public getCurrentJob() {
        return this.currentJob
    }

    @Mutation
    public setJobCount(count: number) {
        this.jobCount = count
    }

    public get getJobCount(): number {
        return this.jobCount
    }

    @Mutation
    public setPagination(pg: PaginationOption) {
        this.paginationOption = pg
    }

    public getPagination() {
        return deepcopy<PaginationOption>(this.paginationOption)
    }

    @Mutation
    public setFilter(f: FilterOption) {
        this.filter = f
    }

    get getFilter() {
        return deepcopy<FilterOption>(this.filter)
    }

    @Mutation
    private setLoading(isLoading: boolean) {
        this.isLoading = isLoading
    }

    @Mutation
    private filterUpdate() {
        if (this.filter.flowName === "") {
            this.filter.flowName = undefined
        }
        if (this.filter.id === "") {
            this.filter.id = undefined
        }
        if (this.filter.channelName === "") {
            this.filter.channelName = undefined
        }
        if (this.filter.dates[0] === "") {
            this.filter.dates[0] = undefined
        }
        if (this.filter.dates[1] === "") {
            this.filter.dates[1] = undefined
        }
    }

}
