import {FilterOption, PaginationOption} from "@/store/types"
import {VuexModule, Module, Mutation, Action} from "vuex-class-modules"
import {Job, jobApi, JobCriteria} from "@/api/jobApi"
import AccountsModule from "@/store/modules/AccountsModule"

@Module
export default class JobsModule extends VuexModule {

    private accountsModule: AccountsModule

    private isLoading: boolean = false
    private jobList: Array<Job> = []
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


    public getLoading(): boolean {
        return this.isLoading
    }


    @Mutation
    public setJobs(jobs: Array<Job>) {
        this.jobList = jobs
    }

    public getJobs() {
        return this.jobList
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
        this.paginationOption = pg
    }

    public getPagination() {
        return this.paginationOption
    }

    @Mutation
    public setFilter(f: FilterOption) {
        this.filter = f
    }

    public getFilter() {
        return { ...this.filter }
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
