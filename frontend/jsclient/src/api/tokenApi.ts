
import {AxiosRequestConfig, AxiosResponse} from "axios"

import {Api} from "@/api/api"
import {apiConfig} from "@/api/api.config"

export const API_GET = "/tokens"


export interface Token {
    id: string
    name: string
}


export class TokenApi extends Api {
    public constructor(config: AxiosRequestConfig) {
        super(config)

        this.list = this.list.bind(this)
    }

    public list(): Promise<Array<Token>> {
        return this.get<Array<Token>>(API_GET)
            .then((response: AxiosResponse<Array<Token>>) => {
                return response.data
            })
    }

}

export const tokenApi = new TokenApi(apiConfig)
