import globalAxios, { AxiosRequestConfig, AxiosResponse} from "axios"


export class Api {
    private config: AxiosRequestConfig

    public constructor(config: AxiosRequestConfig) {
        this.config = config
    }

    public getUri(config: AxiosRequestConfig = this.config): string {
        return globalAxios.getUri(config)
    }

    public request<T, R = AxiosResponse<T>>(config: AxiosRequestConfig = this.config): Promise<R> {
        return globalAxios.request(config)
    }

    public get<T, R = AxiosResponse<T>>(url: string, config: AxiosRequestConfig = this.config): Promise<R> {
        return globalAxios.get(url, config)
    }

    public delete<T, R = AxiosResponse<T>>(url: string, config: AxiosRequestConfig = this.config): Promise<R> {
        return globalAxios.delete(url, config)
    }

    public head<T, R = AxiosResponse<T>>(url: string, config: AxiosRequestConfig = this.config): Promise<R> {
        return globalAxios.head(url, config)
    }

    public post<T, D, R = AxiosResponse<T>>(url: string, data: D, config: AxiosRequestConfig = this.config): Promise<R> {
        return globalAxios.post(url, data, config)
    }

    public put<T, D, R = AxiosResponse<T>>(url: string, data: D, config: AxiosRequestConfig = this.config): Promise<R> {
        return globalAxios.put(url, data, config)
    }

    public patch<T, D, R = AxiosResponse<T>>(url: string, data?: D, config: AxiosRequestConfig = this.config): Promise<R> {
        return globalAxios.patch(url, data, config)
    }
}
