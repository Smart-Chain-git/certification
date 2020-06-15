
import {AxiosRequestConfig, AxiosResponse} from "axios"

import {Api} from "@/api/api"
import {apiConfig} from "@/api/api.config"

export const API_GET = "/tokens"


export interface Token {
    id: string
    name: string
    revoked: boolean
    expirationDate: Date
}

export interface TokenPatch {
    revoked: boolean
}

export interface TokenCreateRequest {
    expirationDate?: Date,
    name: string
}


export class TokenApi extends Api {
    public constructor(config: AxiosRequestConfig) {
        super(config)

        this.list = this.list.bind(this)
        this.updateById = this.updateById.bind(this)
    }

    public list(): Promise<Array<Token>> {
        return this.get<Array<Token>>(API_GET)
            .then((response: AxiosResponse<Array<Token>>) => {
                return response.data
            })
    }

    public updateById(tokenId: string, tokenPatch: TokenPatch): Promise<Token> {
        return this.patch<Token, TokenPatch>(API_GET + "/" + tokenId, {}, tokenPatch)
            .then((response: AxiosResponse<Token>) => {
                return response.data
            })
    }

    public create(tokenCreate: TokenCreateRequest): Promise<Token> {
        return this.post<Token, TokenCreateRequest>(API_GET, {}, tokenCreate)
            .then((response: AxiosResponse<Token>) => {
                return response.data
            })
    }

}

export const tokenApi = new TokenApi(apiConfig)
