import {tokenApi, Token} from "@/api/tokenApi"
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

    public getTokens() {
       return this.tokensList
    }
}
