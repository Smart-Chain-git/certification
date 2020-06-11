import {AxiosRequestConfig, AxiosResponse} from "axios"

import {Api} from "@/api/api"
import {apiConfig} from "@/api/api.config"

export const API_CHECK = "/check"

export interface SignatureCheckRequest {
    documentHash: string,
    proof?: string,
}

export interface Proof {
    filename: string,
    rootHash: string,
    documentHash: string,
    algorithm: string,
    blockHash?: string,
    blockDepth?: number,
    transactionHash?: string
}

export interface SignatureCheckResponse {
    signer?: string,
    check: string,
    check_status: number,
    timestamp: number,
    error?: string,
    proof: Proof
    hash_document?: string,
    hash_document_proof?: string,
    hash_root?: string,
    current_depth: number,
    expected_depth: number,
    check_process: Array<string>
    date: Date,
    public_key: string
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
