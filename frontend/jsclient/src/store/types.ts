
export interface AuthRequest {
    user: string,
    password: string
}

export interface AuthResponse {
    token: string
}

export interface Account {
    id: string
    login: string
    email: string
    fullName: string | undefined
    company: string | undefined
    country: string | undefined
    publicKey: string | undefined
    hash: string | undefined
    isAdmin: boolean
    disabled: boolean
}

export interface AccountCreate {
    login: string
    email: string
    password: string
    fullName: string | undefined
    company: string | undefined
    country: string | undefined
    publicKey: string | undefined
    hash: string | undefined
    isAdmin: boolean
}

export interface AccountPatch {
    email: string | undefined
    password: string | undefined
    fullName: string | undefined
    company: string | undefined
    country: string | undefined
    publicKey: string | undefined
    hash: string | undefined
    isAdmin: boolean | undefined
    disabled: boolean | undefined
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
