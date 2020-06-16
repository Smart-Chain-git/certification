export interface AccountPatch {
    email: string | undefined
    password: string | null
    fullName: string | undefined
    isAdmin: boolean | undefined
    isActive: boolean | undefined
    pubKey: string | undefined
    company: string | undefined
}

export interface AccountCreate {
    password: string,
    email: string
    login: string
    fullName: string
    isAdmin: boolean | undefined
    isActive: boolean
    pubKey: string | undefined
    company: string | undefined
}
