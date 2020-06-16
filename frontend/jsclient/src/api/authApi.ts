import {Api} from "@/api/api"
import {AxiosRequestConfig, AxiosResponse} from "axios"
import {apiConfig} from "@/api/api.config"
import {Account} from "@/api/accountApi"

export const API_AUTH = "/auth"
export const API_ME = "/me"

export interface AuthRequest {
    user: string
    password: string
}

export interface AuthResponse {
    token: string
}

export class AuthApi extends Api {
    public constructor(config: AxiosRequestConfig) {
        super(config)

        this.auth = this.auth.bind(this)
        this.me = this.me.bind(this)
    }


    public auth(credentials: AuthRequest): Promise<AuthResponse> {
        return this.post<AuthResponse, AuthRequest>(API_AUTH, {}, credentials)
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
}

export const authApi = new AuthApi(apiConfig)
