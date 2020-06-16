
import {signatureApi, SignatureCheckRequest, SignatureCheckResponse} from "@/api/signatureApi"
import {Action, Module, Mutation, VuexModule} from "vuex-class-modules"


@Module
export default class SignaturesModule extends VuexModule {

    private checkResponse: SignatureCheckResponse | undefined = undefined

    @Action
    public check(sigCheck: SignatureCheckRequest) {
        signatureApi.check(sigCheck).then((response: SignatureCheckResponse) => {
            this.setCheckResponse(response)
        }).catch((_) => {
            //this.setCheckResponse(undefined)
        })
    }

    @Action
    public reset() {
        this.setCheckResponse(undefined)
    }

    @Mutation
    public setCheckResponse(check: SignatureCheckResponse | undefined) {
        this.checkResponse = check
    }

    public getCheckResponse(): SignatureCheckResponse | undefined {
        return this.checkResponse
    }
}
