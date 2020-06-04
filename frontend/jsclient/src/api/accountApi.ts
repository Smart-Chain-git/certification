import {AxiosRequestConfig, AxiosResponse} from "axios"

import {Api} from "@/api/api"
import {apiConfig} from "@/api/api.config"

import {API_BASE} from "@/api/api.config"

export const API_AUTH = API_BASE + "/auth"
export const API_ME = API_BASE + "/me"
export const API_GET = API_BASE + "/accounts"


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

export class AccountApi extends Api {
    public constructor(config: AxiosRequestConfig) {
        super(config)

        this.auth = this.auth.bind(this)
        this.me = this.me.bind(this)
        this.getById = this.getById.bind(this)
    }


    public auth(credentials: AuthRequest): Promise<AuthResponse> {
        return this.post<AuthResponse, AuthRequest>(API_AUTH, credentials)
            .then((response: AxiosResponse<AuthResponse>) => {
                return response.data
            })
    }

    public me(): Promise<Account> {
        return this.get<Account>(API_ME)
            .then((response: AxiosResponse<Account>) => {
                return response.data
            })
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


}

export const accountApi = new AccountApi(apiConfig)
