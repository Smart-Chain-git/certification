
import {signatureApi, SignatureCheckRequest, SignatureCheckResponse} from '@/api/signatureApi'
import {AxiosResponse} from 'axios'
import {Action, Module, Mutation, VuexModule} from "vuex-class-modules"


@Module
export default class SignaturesModule extends VuexModule {

    private errorCheck: string = ""
    private successCheck: string = ""
    private checkResponse: SignatureCheckResponse | undefined = undefined

    @Action
    public check(sigCheck: SignatureCheckRequest) {
        signatureApi.check(sigCheck).then((response: SignatureCheckResponse) => {
            this.setCheckResponse(response)
        }).catch((_) => {
            this.setCheckResponse({check: "OK",
                check_status: 1,
                timestamp: 0,
                error: "",
                hash_document: "hashdoc",
                hash_document_roof: "hash_doc_root",
                id: "id",
                jobId: "jobId",
                signer: "signer",
                check_process: [],
                proof: "proof",
            date: "date"})
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

    public getCheckResponse() {
        return this.checkResponse
    }
}
