
export interface AuthRequest {
    user: string
    password: string
}

export interface AuthResponse {
    token: string
}

export interface Account {
    id: string,
    login: string,
    email: string,
    fullName: string | undefined,
    company: string | undefined,
    country: string | undefined,
    publicKey: string | undefined,
    hash: string | undefined,
    isAdmin: boolean,
    disabled: boolean,
}

export interface AccountCreate {
    login: string,
    email: string,
    fullName: string | undefined,
    company: string | undefined,
    country: string | undefined,
    publicKey: string | undefined,
    hash: string | undefined,
    isAdmin: boolean,
    disabled: boolean,
}

export interface AccountPatch {
    email: string | undefined,
    password: string | undefined,
    fullName: string | undefined,
    company: string | undefined,
    country: string | undefined,
    publicKey: string | undefined,
    hash: string | undefined,
    isAdmin: boolean | undefined,
    disabled: boolean | undefined,
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

export interface Token {
    id: string
    name: string
    revoked: boolean
    creationDate: Date
    expirationDate?: Date
}

export interface TokenCreateRequest {
    expirationDate?: Date,
    name: string
}
