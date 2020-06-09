import {AxiosRequestConfig, AxiosResponse} from "axios"

import {Api} from "@/api/api"
import {apiConfig} from "@/api/api.config"


export const API_GET = "/jobs"


export interface Job {
    id: string
    createdDate: Date
    injectedDate?: Date
    validatedDate?: Date
    numberOfTry: number
    blockHash?: string,
    blockDepth?: number
    algorithm: string,
    flowName: string,
    stateDate: Date,
    state: string,
    contractAddress?: string,
    transactionHash?: string
    channelName?: string
}

export interface JobCriteria {
    id?: string
    accountId?: string
    flowName?: string
    dateBegin?: string
    dateEnd?: string
    channel?: string
}


export class JobApi extends Api {
    public constructor(config: AxiosRequestConfig) {
        super(config)
        this.list = this.list.bind(this)
    }

    public list(criteria: JobCriteria = {}): Promise<Array<Job>> {
        return this.get<Array<Job>>(API_GET, criteria)
            .then((response: AxiosResponse<Array<Job>>) => {
                return response.data
            })
    }


}

export const jobApi = new JobApi(apiConfig)
