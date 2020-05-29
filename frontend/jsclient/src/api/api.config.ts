import * as qs from "qs"
import { AxiosRequestConfig } from "axios"

export const API_BASE  = process.env.API_BASE || "http://localhost:9090/api"

export const apiConfig: AxiosRequestConfig = {
    timeout: 15000,
    baseURL: API_BASE,
    headers: {
        common: {
            "Content-Type": "application/json",
            "Accept": "application/json",
        },
    },
    paramsSerializer: (params) => qs.stringify(params, { indices: false }),
}
