import {accountApi} from "@/api/accountApi"
import {resetStore} from "@/store/actions/globalActions"
import modules from "@/store/modules"
import {Account, AccountPatch, AuthRequest, AuthResponse} from "@/store/types"
import globalAxios from "axios"
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
        return await accountApi.me()
            .then((response: Account) => this.setMe(response))

    }

    @Action
    public async loadAccount(id: string) {
        return await accountApi.getById(id)
            .then((response: Account) => this.setAccount(response))

    }

    @Action
    public async loadToken(authRequest: AuthRequest) {

        await accountApi.auth(authRequest).then((response: AuthResponse) => {
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


    @Mutation
    private setRequestInterceptor(value: number | null) {
        this.requestInterceptor = value
    }

    @Mutation
    private setResponseInterceptor(value: number | null) {
        this.responseInterceptor = value
    }
}
