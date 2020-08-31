import {DashBoardStat} from "@/api/types"
import {AxiosRequestConfig, AxiosResponse} from "axios"

import {Api} from "@/api/api"
import {apiConfig} from "@/api/api.config"


export const API_GET_DASHBOARD = "/stats/dashboard"

export class StatApi extends Api {
    public constructor(config: AxiosRequestConfig) {
        super(config)
        this.dashBoard = this.dashBoard.bind(this)
    }

    public dashBoard(): Promise<DashBoardStat> {
        return this.get<DashBoardStat>(API_GET_DASHBOARD)
            .then((response: AxiosResponse<DashBoardStat>) => {
                return response.data
            })
    }

}

export const statApi = new StatApi(apiConfig)
