import {accountApi} from "@/api/accountApi"
import {authApi} from "@/api/authApi"
import {resetStore} from "@/store/actions/globalActions"
import modules from "@/store/modules"

import {Account, AccountCreate, AccountPatch, AccountValidation, AuthRequest, AuthResponse} from "@/api/types"
import globalAxios, {AxiosError, AxiosResponse} from "axios"
import Cookies from "js-cookie"
import Vue from "vue"
import {Action, Module, Mutation, VuexModule} from "vuex-class-modules"


@Module
export default class AccountsModule extends VuexModule {

    public readonly COOKIE_TOKEN = "token"

    public token: string | undefined = undefined
    private accounts: { [key: string]: Account } = {}
    private requestInterceptor: number | null = null
    private responseInterceptor: number | null = null
    private currentAccount: Account | undefined = undefined
    private httpStatus: number = 0
    private currentTokenAccount: Account | undefined = undefined
    private currentTokenError: number = 200


    /**
     * The connected user.
     */
    private me: Account | undefined = undefined


    get allAccounts(): Array<Account> {
        return Object.values(this.accounts)
    }

    public get meAccount() {
        return this.me
    }

    public get meName() {
        return this.me ? `${this.me.fullName || ""}` : ""
    }

    public getAccountById(id: string): Account {
        const account: Account | undefined = this.allAccounts.find((r) => r.id === id)

        if (account === undefined) {
            throw new Error(`The store doesn"t contain account with id=${id}.`)
        }
        return account
    }

    @Mutation
    public setAccounts(accounts: Array<Account>) {
        this.accounts = {}

        accounts.forEach((account: Account) => {
            this.accounts[account.id] = account
        })
    }

    @Mutation
    public setAccount(account?: Account) {
        if (account !== null && account !== undefined) {
            // Bug of reactivity forcing to delete the existing message before updating it.
            if (this.accounts[account.id] != null) {
                Vue.delete(this.accounts, account.id)
            }
            Vue.set(this.accounts, account.id, account)
            this.currentAccount = account
            if (account.id === this.me!.id) {
                this.me = account
            }
        }
    }

    @Mutation
    public setMe(me: Account) {
        this.me = me
    }

    @Mutation
    public reset() {
        this.token = undefined
        this.me = undefined
        this.accounts = {}
    }


    @Mutation
    public setToken(token: string) {
        this.token = token
    }


    @Action
    public async loadMe() {
        await authApi.me()
            .then((response: Account) => this.setMe(response))
    }

    @Action
    public async loadAccount(id: string) {
        await accountApi.getById(id)
            .then((response: Account) => this.setAccount(response))
    }

    @Action
    public async loadToken(authRequest: AuthRequest) {
        await authApi.auth(authRequest).then((response: AuthResponse) => {
            const token = response.token
            // Set the token in the store and in the cookies
            this.setToken(token)
            Cookies.set(this.COOKIE_TOKEN, token)
            // Setup the token for the axios requests
            this.initToken()
        })
    }


    @Action
    public async initToken() {
        // Load the token from the cookie if needed
        const cookie = Cookies.get(this.COOKIE_TOKEN)
        if (this.token == null && cookie != null) {
            this.setToken(cookie)
        }
        // Add the token to the requests headers
        if (this.requestInterceptor === null) {

            const requestInterceptor = globalAxios.interceptors.request.use(async (config) => {
                    const token = this.token
                    if (token) {
                        config.headers.Authorization = `Bearer ${token}`
                    }
                    return config
                }
                ,
                (error) => {
                    return Promise.reject(error)
                },
            )
            this.setRequestInterceptor(requestInterceptor)
        }

        // Check for token validity for each response
        if (this.responseInterceptor === null) {
            const responseInterceptor = globalAxios.interceptors.response.use(undefined, (error) => {
                const response = error.response
                // Control over the response state:
                // - undefined: corresponds to a status 403 (no token provided)
                // - status 401: the token has expired
                if (response === undefined || response.status === 401) {
                    // Reset the store and invalidate the token
                    // Modules retrieved from router instance.
                    resetStore(modules)
                    this.invalidateToken()
                }
                return Promise.reject(error)
            })
            this.setResponseInterceptor(responseInterceptor)
        }
    }

    @Action
    public async invalidateToken() {
        Cookies.remove(this.COOKIE_TOKEN)
        if (this.requestInterceptor !== null) {
            globalAxios.interceptors.request.eject(this.requestInterceptor)
            this.setRequestInterceptor(null)
        }
        if (this.responseInterceptor !== null) {
            globalAxios.interceptors.response.eject(this.responseInterceptor)
            this.setResponseInterceptor(null)
        }
    }

    @Action
    public async loadAccounts() {
        await accountApi.list().then((response: Array<Account>) => {
            this.setAccounts(response)
        })
    }


    public async updateAccount(id: string, patch: AccountPatch) {
        await accountApi.patchById(id, patch).then((response: Account) => {
            this.setAccount(response)
        })
    }

    public getCurrentAccount() {
        return this.currentAccount
    }

    @Action
    public async createAccount(account: AccountCreate) {
        await accountApi.create(account).then((response: Account) => {
            this.setHttpStatus(200)
            Vue.set(this.accounts, response.id, response)
        }).catch((error) => {
            this.setHttpStatus(error.response.status)
        })
    }

    @Mutation
    public setHttpStatus(code: number) {
        this.httpStatus = code
    }

    public getHttpStatus() {
        return this.httpStatus
    }

    @Action
    public async loadActivationToken(token: string) {
        this.setToken(token)
        this.initToken()
    }

    @Action
    public async validateAccount(password: AccountValidation) {
        await accountApi.validate(password)
    }

    @Action
    public async getAccountByActivationToken() {
        await accountApi.getAccountByActivationToken()
            .then((response: Account) => {
                this.setCurrentActivationAccount(response)
                this.resetCurrentActivationError()
            }).catch((error: AxiosError) => {
                this.setCurrentActivationError(error.response!.status)
                this.resetCurrentActivationAccount()
            })
    }

    public get currentActivationAccount(): Account | undefined {
        return this.currentTokenAccount
    }

    @Mutation
    public setCurrentActivationAccount(account: Account) {
        this.currentTokenAccount = account
    }

    public get currentActivationError(): number {
        return this.currentTokenError
    }

    @Mutation
    public setCurrentActivationError(error: number) {
        this.currentTokenError = error
    }

    @Mutation
    public resetCurrentActivationAccount() {
        this.currentTokenAccount = undefined
    }

    @Mutation
    public resetCurrentActivationError() {
        this.currentTokenError = 200
    }

    @Mutation
    private setRequestInterceptor(value: number | null) {
        this.requestInterceptor = value
    }

    @Mutation
    private setResponseInterceptor(value: number | null) {
        this.responseInterceptor = value
    }

}
