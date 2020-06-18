
import {signatureApi} from "@/api/signatureApi"
import {
    SignatureCheckRequest,
    SignatureCheckResponse,
    SignatureRequest,
    SignatureRequestParam,
    SignatureResponse,
} from "@/api/types"
import {Action, Module, Mutation, VuexModule} from "vuex-class-modules"


@Module
export default class SignaturesModule extends VuexModule {

    private checkResponse: SignatureCheckResponse | undefined = undefined
    private signResponse: SignatureResponse | undefined = undefined

    @Action
    public check(sigCheck: SignatureCheckRequest) {
        signatureApi.check(sigCheck).then((response: SignatureCheckResponse) => {
            this.setCheckResponse(response)
        })
    }

    public sign(sign: Array<SignatureRequest>, signP: SignatureRequestParam) {
        signatureApi.sign(sign, signP).then((response: SignatureResponse) => {
            this.setSignResponse(response)
        })
    }

    @Mutation
    public setSignResponse(sign: SignatureResponse) {
        this.signResponse = sign
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
