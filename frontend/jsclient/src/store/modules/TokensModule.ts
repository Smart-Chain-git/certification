import {tokenApi} from "@/api/tokenApi"
import {Token, TokenCreateRequest} from "@/store/types"
import Vue from "vue"
import {Action, Module, Mutation, VuexModule} from "vuex-class-modules"


@Module
export default class TokensModule extends VuexModule {

    private tokensList: Array<Token> = []

    @Action
    public async loadTokens() {
       await tokenApi.list().then((response: Array<Token>) => {
           this.setTokens(response)
       })
    }

    @Mutation
    public setTokens(tokens: Array<Token>) {
        this.tokensList = tokens
    }

    @Mutation
    public addToken(token: Token) {
        this.tokensList.push(token)
    }

    public getTokens() {
       return this.tokensList
    }

    public async revokeToken(tokenId: string) {
        await tokenApi.revokeById(tokenId).then((response: Token) => {
            for (let i = 0 ; i < this.tokensList.length ; i++) {
                if (this.tokensList[i].id === response.id) {
                    Vue.set(this.tokensList, i, response)
                    break
                }
            }
        })
    }

    public async createToken(tokenCreate: TokenCreateRequest) {
        await tokenApi.create(tokenCreate).then((response: Token) => {
            this.addToken(response)
        })
    }
}
