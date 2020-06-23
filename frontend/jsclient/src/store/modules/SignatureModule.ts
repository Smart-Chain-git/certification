
import {signatureApi} from "@/api/signatureApi"
import {
    SignatureCheckRequest,
    SignatureCheckResponse, SignatureMultiRequest,
    SignatureResponse,
} from "@/api/types"
import {Action, Module, Mutation, VuexModule} from "vuex-class-modules"


@Module
export default class SignaturesModule extends VuexModule {

    private checkResponse: SignatureCheckResponse | undefined = undefined
    private signResponse: Array<SignatureResponse> = []

    @Action
    public check(sigCheck: SignatureCheckRequest) {
        signatureApi.check(sigCheck).then((response: SignatureCheckResponse) => {
            this.setCheckResponse(response)
        })
    }

    public async signMulti(sign: SignatureMultiRequest) {
        await signatureApi.signMulti(sign).then((response: Array<SignatureResponse>) => {
            this.setSignResponse(response)
        })
    }

    @Mutation
    public setSignResponse(sign: Array<SignatureResponse>) {
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
