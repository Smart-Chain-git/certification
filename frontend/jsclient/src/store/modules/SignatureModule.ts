
import {signatureApi} from "@/api/signatureApi"
import {SignatureCheckRequest, SignatureCheckResponse} from "@/api/types"
import {Action, Module, Mutation, VuexModule} from "vuex-class-modules"


@Module
export default class SignaturesModule extends VuexModule {

    private checkResponse: SignatureCheckResponse | undefined = undefined

    @Action
    public check(sigCheck: SignatureCheckRequest) {
        signatureApi.check(sigCheck).then((response: SignatureCheckResponse) => {
            this.setCheckResponse(
            {
                signer: "tz1VSUr8wwNhLAzempoch5d6hLRiTh8Cjcjb",
                timestamp: new Date("2020-06-11T16:12:22Z"),
                check_process: ["1240", "1245"],
                check_status: 1,
                output: "OK",
                proof: {
                    algorithm: "SHA-256",
                    block_hash: "BLB2zRsSuSjcKUbMydEqih61wJh318SJbAbZYhs9z9SnYe1fCcx",
                    contract_address: "KT1WJCy3cwLrfWhBH95otTSSAFMT8xndnt3K",
                    file_name: "bouletmaton.jpg",
                    hash_document: "142f9eb8376093e5c24c74714bef07c187cd9a4a81e4f758515ce21b06b2e12a",
                    hash_list: [{
                        hash: "1",
                        position: "right"
                    },
                        {
                            position: "left"
                        }],
                    hash_root: "142f9eb8376093e5c24c74714bef07c187cd9a4a81e4f758515ce21b06b2e12a",
                    origin: "SWORD",
                    origin_public_key: "tz1VSUr8wwNhLAzempoch5d6hLRiTh8Cjcjb",
                    public_key: "tz1VSUr8wwNhLAzempoch5d6hLRiTh8Cjcjb",
                    signature_date: new Date("2020-06-11T16:12:16.3563409Z"),
                    transaction_hash: "opCD4Kr73nQxuLwXVw6YyPSLUmHJ9ASHoeDgVZW8B4AeCFKzBdA",
                   /* urls: [],
                    version: "1.0.0",
                    signer: "tz1VSUr8wwNhLAzempoch5d6hLRiTh8Cjcjb",
                    timestamp: "2020-06-11T16:12:22Z",*/
                }
            })
            //this.setCheckResponse(response)
        }).catch((_) => {
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
