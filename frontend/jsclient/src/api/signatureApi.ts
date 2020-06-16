import {AxiosRequestConfig, AxiosResponse} from "axios"

import {Api} from "@/api/api"
import {apiConfig} from "@/api/api.config"

export const API_CHECK = "/check"

export interface SignatureCheckRequest {
    documentHash: string,
    proof?: string,
}

export interface Proof {
    file_name: string,
    hash_root: string,
    hash_document: string,
    algorithm: string,
    block_hash?: string,
    blockDepth?: number,
    transaction_hash?: string
    signature_date?: Date
    origin_public_key: string,
    origin: string
}

export interface SignatureCheckResponse {
    check_status: number,
    signer?: string,
    timestamp: number,
    proof: Proof
    error?: string,
    /*hash_document?: string,
    hash_document_proof?: string,
    hash_root?: string,*/
    current_depth?: number,
    expected_depth?: number,
    check_process?: Array<string>
    date?: Date,
    public_key?: string
    output: string,
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
