import {AxiosRequestConfig, AxiosResponse} from "axios"

import {Api} from "@/api/api"
import {apiConfig} from "@/api/api.config"

export const API_CHECK = "/check"

export interface SignatureCheckRequest {
    hash: string,
    proof?: string,
}

export interface SignatureCheckResponse {
    check: string,
    check_status: number,
    timestamp: number,
    error?: string,
    hash_document: string,
    hash_document_roof: string,
    id: string,
    jobId: string,
    signer: string,
    check_process: Array<string>
    proof: string,
    date: string
}

export class SignatureApi extends Api {
    public constructor(config: AxiosRequestConfig) {
        super(config)

        this.check = this.check.bind(this)
    }

    public check(sigCheck: SignatureCheckRequest): Promise<SignatureCheckResponse> {
        return this.post<SignatureCheckResponse, SignatureCheckRequest>(API_CHECK, sigCheck)
            .then((response: AxiosResponse<SignatureCheckResponse>) => {
                return response.data
            })
    }
}

export const signatureApi = new SignatureApi(apiConfig)
