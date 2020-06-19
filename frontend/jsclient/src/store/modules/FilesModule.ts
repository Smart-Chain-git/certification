
import {fileApi} from "@/api/fileApi"
import {FileFilterOption, PaginationOption} from "@/store/types"
import {FileCriteria, JobFile, Proof} from "@/api/types"
import {Action, Module, Mutation, VuexModule} from "vuex-class-modules"
import AccountsModule from "@/store/modules/AccountsModule"
import deepcopy from "ts-deepcopy"

@Module
export default class FilesModule extends VuexModule {

    private accountsModule: AccountsModule

    private isLoading: boolean = false
    private fileList: Array<JobFile> = []
    private fileCount: number = 0
    private proof: Proof | undefined = undefined
    private paginationOption: PaginationOption = {
        page: 1,
        itemsPerPage: 10,
        sortBy: [],
        sortDesc: [],
    }
    private filter: FileFilterOption = {
        accountId: undefined,
        name: undefined,
        jobId: undefined,
        id: undefined,
        hash: undefined,
        dates: []
    }

    constructor(options: any) {
        super(options)
        this.accountsModule = options.accountsModule
    }

    @Action
    public async loadFiles() {
        this.setLoading(true)
        this.filterUpdate()
        const sorts: Array<string> = []
        for (let i = 0; i < this.paginationOption.sortBy.length; ++i) {
            sorts.push(this.paginationOption.sortBy[i] + ":" + ((this.paginationOption.sortDesc[i]) ? "desc" : "asc"))
        }

        const criteria: FileCriteria = {
            accountId: this.accountsModule.meAccount?.id,
            id: this.filter.id,
            dateStart: this.filter.dates[0],
            dateEnd: this.filter.dates[1],
            hash: this.filter.hash,
            jobId: this.filter.jobId,
            name: this.filter.name,
            sort: sorts,
            page: this.paginationOption.page - 1,
            size: this.paginationOption.itemsPerPage,
        }


        await fileApi.list(criteria).then((response: Array<JobFile>) => {
            this.setFiles(response)
        })
        await fileApi.count(criteria).then((response: number) => {
            this.setFileCount(response)
        })
        this.setLoading(false)
    }

    public get getLoading(): boolean {
        return this.isLoading
    }

    @Mutation
    public setFiles(jobs: Array<JobFile>) {
        this.fileList = jobs
    }

    public get getFiles(): Array<JobFile> {
        return this.fileList
    }

    @Mutation
    public setFileCount(count: number) {
        this.fileCount = count
    }

    public get getFileCount(): number {
        return this.fileCount
    }

    @Mutation
    public setPagination(pg: PaginationOption) {
        this.paginationOption = pg
    }

    public getPagination() {
        return deepcopy<PaginationOption>(this.paginationOption)
    }

    @Mutation
    public setFilter(f: FileFilterOption) {
        this.filter = f
    }

    get getFilter() {
        return deepcopy<FileFilterOption>(this.filter)
    }

    @Mutation
    private setLoading(isLoading: boolean) {
        this.isLoading = isLoading
    }

    @Mutation
    private setProof(proof: Proof) {
        this.proof = proof
    }

    public getProof() {
        return this.proof
    }

    @Mutation
    private filterUpdate() {
        if (this.filter.name === "") {
            this.filter.name = undefined
        }
        if (this.filter.id === "") {
            this.filter.id = undefined
        }
        if (this.filter.jobId === "") {
            this.filter.jobId = undefined
        }
        if (this.filter.dates[0] === "") {
            this.filter.dates[0] = undefined
        }
        if (this.filter.dates[1] === "") {
            this.filter.dates[1] = undefined
        }
    }

    public async loadProof(fileId: string) {
        await fileApi.proof(fileId).then((response: Proof) => {
            this.setProof(response)
        })
    }
}
