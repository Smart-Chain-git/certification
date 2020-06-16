
export interface AuthRequest {
    user: string,
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
    isAdmin: boolean,
    disabled: boolean,
    company: string | undefined,
    country: string | undefined,
    hash: string,
    publicKey: string | undefined
}

export interface AccountPatch {
    email: string | undefined,
    password: string | undefined,
    fullName: string | undefined,
    isAdmin: boolean | undefined,
    disabled: boolean | undefined,
    publicKey: string | undefined,
    hash: string | undefined,
    company: string | undefined,
    country: string | undefined
}

export interface AccountCreate {
    password: string,
    email: string,
    login: string,
    fullName: string,
    isAdmin: boolean | undefined,
    disabled: boolean,
    publicKey: string | undefined,
    hash: string | undefined,
    company: string | undefined,
    country: string | undefined
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
