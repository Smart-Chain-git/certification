import {AxiosRequestConfig, AxiosResponse} from "axios"

import {Api} from "@/api/api"
import {apiConfig} from "@/api/api.config"

export const API_GET = "/accounts"

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

export class AccountApi extends Api {
    public constructor(config: AxiosRequestConfig) {
        super(config)

        this.getById = this.getById.bind(this)
    }

    public getById(id: string): Promise<Account> {
        return this.get<Account>(API_GET + "/" + id)
            .then((response: AxiosResponse<Account>) => {
                return response.data
            })
    }

    public list(): Promise<Array<Account>> {
        return this.get<Array<Account>>(API_GET)
            .then((response: AxiosResponse<Array<Account>>) => {
                return response.data
            })
    }

    public patchById(id: string, data: AccountPatch): Promise<Account> {
        return this.patch<Account, AccountPatch>(API_GET + "/" + id, data)
            .then((response: AxiosResponse<Account>) => {
                return response.data
            })
    }
}

export const accountApi = new AccountApi(apiConfig)
