import globalAxios, {AxiosInstance, AxiosRequestConfig, AxiosResponse} from "axios"

import {API_BASE} from "@/api/api.config"


export class Api {
    //private api: AxiosInstance

    public constructor(config: AxiosRequestConfig) {
      //  this.api = globalAxios
        // this.api.interceptors.request.use((param: AxiosRequestConfig) => {
        //     const newConfig = {
        //         ...param,
        //         baseURL: API_BASE,
        //     }
        //
        //     return {
        //         ...newConfig,
        //     }
        // })
    }

    public getUri(config?: AxiosRequestConfig): string {
        return globalAxios.getUri(config)
    }

    public request<T, R = AxiosResponse<T>>(config: AxiosRequestConfig): Promise<R> {
        return globalAxios.request(config)
    }

    public get<T, R = AxiosResponse<T>>(url: string, config?: AxiosRequestConfig): Promise<R> {
        return globalAxios.get(url, config)
    }

    public delete<T, R = AxiosResponse<T>>(url: string, config?: AxiosRequestConfig): Promise<R> {
        return globalAxios.delete(url, config)
    }

    public head<T, R = AxiosResponse<T>>(url: string, config?: AxiosRequestConfig): Promise<R> {
        return globalAxios.head(url, config)
    }

    public post<T, D, R = AxiosResponse<T>>(url: string, data: D, config?: AxiosRequestConfig): Promise<R> {
        return globalAxios.post(url, data, config)
    }

    public put<T, D, R = AxiosResponse<T>>(url: string, data: D, config?: AxiosRequestConfig): Promise<R> {
        return globalAxios.put(url, data, config)
    }

    public patch<T, R = AxiosResponse<T>>(url: string, data?: string, config?: AxiosRequestConfig): Promise<R> {
        return globalAxios.patch(url, data, config)
    }
}
