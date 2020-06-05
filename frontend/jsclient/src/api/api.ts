import globalAxios, { AxiosRequestConfig, AxiosResponse} from "axios"


export class Api {
    private config: AxiosRequestConfig

    public constructor(config: AxiosRequestConfig) {
        this.config = config
    }

    public getUri(params = {},config: AxiosRequestConfig = this.config): string {
        let confParam= {
            ...config,
            'params' : {...params}
        }
        return globalAxios.getUri(confParam)
    }

    public request<T, R = AxiosResponse<T>>(params = {},config: AxiosRequestConfig = this.config): Promise<R> {
        let confParam= {
            ...config,
            'params' : {...params}
        }
        return globalAxios.request(confParam)
    }

    public get<T, R = AxiosResponse<T>>(url: string,params = {}, config: AxiosRequestConfig = this.config): Promise<R> {
        let confParam= {
            ...config,
            'params' : {...params}
        }
        return globalAxios.get(url, confParam)
    }

    public delete<T, R = AxiosResponse<T>>(url: string,params = {}, config: AxiosRequestConfig = this.config): Promise<R> {
        let confParam= {
            ...config,
            'params' : {...params}
        }
        return globalAxios.delete(url, confParam)
    }

    public head<T, R = AxiosResponse<T>>(url: string,params = {}, config: AxiosRequestConfig = this.config): Promise<R> {
        let confParam= {
            ...config,
            'params' : {...params}
        }
        return globalAxios.head(url, confParam)
    }

    public post<T, D, R = AxiosResponse<T>>(url: string,params = {}, data: D, config: AxiosRequestConfig = this.config): Promise<R> {
        let confParam= {
            ...config,
            'params' : {...params}
        }
        return globalAxios.post(url, data, confParam)
    }

    public put<T, D, R = AxiosResponse<T>>(url: string,params = {}, data: D, config: AxiosRequestConfig = this.config): Promise<R> {
        let confParam= {
            ...config,
            'params' : {...params}
        }
        return globalAxios.put(url, data, confParam)
    }

    public patch<T, R = AxiosResponse<T>>(url: string,params = {}, data?: string, config: AxiosRequestConfig = this.config): Promise<R> {
        let confParam= {
            ...config,
            'params' : {...params}
        }
        return globalAxios.patch(url, data, config)
    }
}
