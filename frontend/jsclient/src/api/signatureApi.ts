import {
    SignatureCheckRequest,
    SignatureCheckResponse, SignatureMultiRequest,
    SignatureResponse
} from "@/api/types"
import {AxiosRequestConfig, AxiosResponse} from "axios"

import {Api} from "@/api/api"
import {apiConfig} from "@/api/api.config"

export const API_CHECK = "/check"
export const API_SIGN = "/sign"

export class SignatureApi extends Api {
    public constructor(config: AxiosRequestConfig) {
        super(config)

        this.check = this.check.bind(this)
    }

    public check(sigCheck: SignatureCheckRequest): Promise<SignatureCheckResponse> {
        return this.post<SignatureCheckResponse, SignatureCheckRequest>(API_CHECK, {}, sigCheck)
            .then((response: AxiosResponse<SignatureCheckResponse>) => {
                return response.data
            })
    }

    public signMulti(sign: SignatureMultiRequest) : Promise<Array<SignatureResponse>> {
        return this.post<Array<SignatureResponse>, SignatureMultiRequest>(API_SIGN, {}, sign)
            .then((response: AxiosResponse<Array<SignatureResponse>>) => {
                return response.data
            })

    }
}

export const signatureApi = new SignatureApi(apiConfig)
