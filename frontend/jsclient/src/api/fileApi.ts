import {FileCriteria, JobFile, Proof} from "@/api/types"
import {AxiosRequestConfig, AxiosResponse} from "axios"

import {Api} from "@/api/api"
import {apiConfig} from "@/api/api.config"


export const API_GET = "/files"
export const API_GET_COUNT = "/files-count"


export class FileApi extends Api {
    public constructor(config: AxiosRequestConfig) {
        super(config)
        this.list = this.list.bind(this)
    }

    public list(criteria: FileCriteria = {}): Promise<Array<JobFile>> {
        return this.get<Array<JobFile>>(API_GET, criteria)
            .then((response: AxiosResponse<Array<JobFile>>) => {
                return response.data
            })
    }

    public getById(id: string): Promise<JobFile> {
        return this.get<JobFile>(API_GET + "/" + id)
            .then((response: AxiosResponse<JobFile>) => {
                return response.data
            })
    }

    public count(criteria: FileCriteria = {}): Promise<number> {
        return this.get<number>(API_GET_COUNT, criteria)
            .then((response: AxiosResponse<number>) => {
                return response.data
            })
    }

    public proof(fileId: string): Promise<Proof> {
        return this.get<Proof>(API_GET + "/" + fileId + "/proof")
            .then((response: AxiosResponse<Proof>) => {
                return response.data
            })
    }
}

export const fileApi = new FileApi(apiConfig)
