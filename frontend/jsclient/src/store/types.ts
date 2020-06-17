
export interface Account {
    id: string
    login: string
    email: string
    fullName: string | undefined
    isAdmin: boolean,
    pubKey: string | null
}

export interface AuthRequest {
    user: string
    password: string
}

export interface AuthResponse {
    token: string
}

export interface AccountPatch {
    email: string | undefined
    password: string | null
    fullName: string | undefined
    isAdmin: boolean | undefined
}

export interface PaginationOption {
    page: number,
    itemsPerPage: number,
    sortBy: Array<string>,
    sortDesc: Array<boolean>
}

export interface FilterOption {
    flowName: string | undefined,
    id: string | undefined,
    dates: Array<string | undefined>,
    channelName: string | undefined
}

export interface Job {
    id: string
    createdDate: Date
    injectedDate?: Date
    validatedDate?: Date
    numberOfTry: number
    blockHash?: string,
    blockDepth?: number
    algorithm: string,
    flowName: string,
    stateDate: Date,
    state: string,
    contractAddress?: string,
    transactionHash?: string,
    channelName?: string,
    docsNumber: number
}

export interface JobCriteria {
    id?: string
    accountId?: string
    flowName?: string
    dateBegin?: string
    dateEnd?: string
    channel?: string
    sort?: Array<string>
    desc?: Array<boolean>
    page?: number
    size?: number
}
