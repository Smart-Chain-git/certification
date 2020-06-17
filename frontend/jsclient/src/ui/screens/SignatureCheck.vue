<template>
    <v-container fluid>
        <v-flex xs11>
        <Card>
            <h2>{{ $t("signatureCheck.title") }}</h2>
            <h2>{{ $t("signatureCheck.subtitle") }}</h2>
            <span>{{ $t("signatureCheck.text1") }}</span>
            <br/>
            <span>{{ $t("signatureCheck.text2") }}</span>
            <br/>
            <v-flex class="mt-12">
                <v-row>
                    <v-col class="col-4"><h1>{{ $t("signatureCheck.upload") }}</h1></v-col>
                    <v-col class="col-5">
                        <v-file-input prepend-icon="" prepend-inner-icon="publish" filled :placeholder="$t('signatureCheck.drop')" outlined v-model="file">
                        </v-file-input>
                    </v-col>
                </v-row>
                <v-row>
                    <v-col class="col-4"><h1>{{ $t("signatureCheck.uploadProof") }}</h1><span>{{ $t("signatureCheck.optional")}}</span></v-col>
                    <v-col class="col-5">
                        <v-file-input prepend-icon="" prepend-inner-icon="publish" filled :placeholder="$t('signatureCheck.drop')" outlined v-model="proof">
                        </v-file-input>
                    </v-col>
                </v-row>
            </v-flex>
            <v-flex v-if="checkResponse !== undefined">
                <v-row :class="'banner_'+(checkSucceeded ? 'success' : 'error')">
                    <v-col class="col-2">
                        <v-icon size="100" :color="checkSucceeded ? 'green' : 'red'">{{ checkSucceeded ? "check_circle_outline" : "cancel"}}</v-icon>
                    </v-col>
                    <v-col class="col-10">
                         <h1 :class="'title_'+(checkSucceeded ? 'success' : 'error')"> {{ shortMessage }}</h1>
                        <p>{{ longMessage }}</p>
                    </v-col>
                </v-row>
                <v-row v-if="checkSucceeded">
                    <v-expansion-panels>
                        <v-expansion-panel class="more_info">
                            <v-expansion-panel-header>{{ $t("signatureCheck.more") }}</v-expansion-panel-header>
                            <v-expansion-panel-content>
                                <div v-if="checkResponse.check_status === 0">
                                    {{ parse("signatureCheck.success.message1.block2.line1") }}<br/><br/>
                                    {{ parse("signatureCheck.success.message1.block2.line2") }}<br/>
                                    {{ parse("signatureCheck.success.message1.block2.line3") }}<br/>
                                    {{ parse("signatureCheck.success.message1.block2.line4") }}<br/>
                                    {{ parse("signatureCheck.success.message1.block2.line5") }}<br/><br/>
                                    <span v-html="parseLink('signatureCheck.success.message1.block2.line6', 'here',{
                                        'download' : checkResponse.proof.file_name + '.json',
                                        'href' : 'data:text/json:charset=utf-8,' + encodeURIComponent(JSON.stringify(checkResponse.proof))
                                    })"/><br/><br/>
                                    <span v-html="parseLink('signatureCheck.success.message1.block2.line7', 'here', {'href' : '/settings'})"/>
                                </div>
                                <div v-if="checkResponse.check_status === 1">
                                    {{parse("signatureCheck.success.message2.block2.line1")}}<br/><br/>
                                    {{parse("signatureCheck.success.message2.block2.line2")}}<br/><br/>
                                    {{parse("signatureCheck.success.message2.block2.line3")}}<br/>
                                    {{parse("signatureCheck.success.message2.block2.line4")}}<br/>

                                    <span v-for="(hash, idx) in checkResponse.proof.hash_list">
                                        {{parse("signatureCheck.success.message2.block2.line5", idx)}}<br/>
                                    </span>
                                    <br/>
                                    {{parse("signatureCheck.success.message2.block2.line6")}}<br/><br/>
                                    {{parse("signatureCheck.success.message2.block2.line7")}}<br/><br/>
                                    {{parse("signatureCheck.success.message2.block2.line8")}}<br/><br/>
                                    {{parse("signatureCheck.success.message2.block2.line9")}}<br/><br/>
                                    <span v-html="parseLink('signatureCheck.success.message2.block2.line10', 'here', {'href' : '/settings'})"/>
                                </div>
                            </v-expansion-panel-content>
                        </v-expansion-panel>
                    </v-expansion-panels>
                </v-row>
            </v-flex>
            <v-flex v-if="checkSucceeded" class="mt-5">
                <v-row>
                    <v-col class="col-2"></v-col>
                    <v-col class="col-8 small">{{ $t("signatureCheck.success.info.title")}}</v-col>
                </v-row>
                <v-row>
                    <v-col class="col-2"></v-col>
                    <v-col class="col-4 small">
                        <h3>{{ $t("signatureCheck.success.info.col1.title")}}</h3>
                        <ol>
                            <li>{{ $t("signatureCheck.success.info.col1.step1")}}</li>
                            <li>{{ $t("signatureCheck.success.info.col1.step2")}}</li>
                            <li>{{ $t("signatureCheck.success.info.col1.step3")}}</li>
                            <li>{{ $t("signatureCheck.success.info.col1.step4")}}</li>
                        </ol>
                    </v-col>
                    <v-col class="col-5 small">
                        <h3>{{ $t("signatureCheck.success.info.col2.title")}}</h3>
                        <ol>
                            <li>{{ $t("signatureCheck.success.info.col2.step1")}}</li>
                            <li>{{ $t("signatureCheck.success.info.col2.step2")}}</li>
                            <li>{{ $t("signatureCheck.success.info.col2.step3")}}</li>
                            <li>{{ $t("signatureCheck.success.info.col2.step4")}}</li>
                            <li>{{ $t("signatureCheck.success.info.col2.step5")}}</li>
                            <li>{{ $t("signatureCheck.success.info.col2.step6")}}</li>
                            <li>{{ $t("signatureCheck.success.info.col2.step7")}}</li>
                        </ol>
                    </v-col>
                    <v-col class="col-1"> </v-col>
                </v-row>
            </v-flex>
            <div class="text-center mt-5">
                <IconButton leftIcon="double_arrow" @click="check" color="var(--var-color-blue-sword)" :disabled="file === null">{{ $t("signatureCheck.generate") }}</IconButton>
            </div>

            <v-flex id="bottom">
                <span class="ml-10 mr-10">{{ $t("signatureCheck.com") }}</span>
                <a class="ml-10 mr-10" href="#/GCU">{{ $t("signatureCheck.GCU") }}</a>
                <a class="ml-10 mr-10" href="#/policy">{{ $t("signatureCheck.policy") }}</a>
            </v-flex>
        </Card>
        </v-flex>
    </v-container>
</template>

<style lang="scss" scoped>
    h2 {
        color: var(--var-color-blue-sword);
        text-align: center;
        padding-bottom: 1rem;
    }

    span {
        padding-bottom: 1rem;
    }

    #bottom {
        margin-top: 50px;
        text-align: center;
        font-size: 8px;
    }

    .banner_success {
        background-color: lightgreen;
    }

    .banner_error {
        background-color: lightsalmon;
    }

    .small {
        font-size: 10px;
    }

    .title_success{
        color: green;
    }

    .title_error {
        color: red;
    }

    .more_info {
        font-size: 12px;
        background-color: #eeeeee !important;
    }
</style>
<script lang="ts">
    import {SignatureCheckRequest, SignatureCheckResponse} from "@/api/types"
    import {Component, Vue} from "vue-property-decorator"
    import * as CryptoJS from "crypto-js"

    @Component
    export default class SignatureCheck extends Vue {
        private file: Blob | null = null
        private proof: Blob | null = null
        private contentFile: string | undefined = undefined
        private contentProof: string | undefined = undefined

        private mounted() {
            this.file = null
            this.proof = null
            this.$modules.signatures.reset()
        }

        private get checkResponse(): SignatureCheckResponse | undefined {
            return this.$modules.signatures.getCheckResponse()
        }

        public get checkSucceeded() {
            return this.checkResponse?.output === "OK"
        }

        private static format(str: string, values: Array<string>) {
            for (let i = 0 ; i < values.length ; ++i) {
                str = str.replace("{" + i + "}", values[i])
            }
            return str
        }

        private get shortMessage() {
            if (this.checkSucceeded) {
                let n = this.checkResponse?.check_status! + 1
                return this.parse("signatureCheck.success.message" + n.toString() + ".block1.title")
            } else {
                return this.parse("signatureCheck.errors." + this.checkResponse?.error?.toLowerCase() + ".title")
            }
        }

        private get longMessage() {
            if (this.checkSucceeded) {
                let t = this.checkResponse?.check_status! + 1
                return this.parse("signatureCheck.success.message" + t.toString() + ".block1.message")
            } else {
               return this.parse("signatureCheck.errors." + this.checkResponse?.error?.toLowerCase())
            }
        }

        private parse(message: string, idx: number = 0) {
            if (this.checkResponse) {
                let res: string = this.$t(message).toString()

                switch (message) {
                    case "signatureCheck.success.message1.block1.message":
                        return SignatureCheck.format(res, [(this.checkResponse.signer !== undefined) ? this.checkResponse.signer : this.$t("signatureCheck.unknown").toString(), this.checkResponse.timestamp!.toString()])
                    case "signatureCheck.success.message2.block1.message":
                        return SignatureCheck.format(res, [(this.checkResponse.signer !== undefined) ? this.checkResponse.signer : this.$t("signatureCheck.unknown").toString(), this.checkResponse.timestamp!.toString()])

                    case "signatureCheck.success.message1.block2.line1":
                        return SignatureCheck.format(res, [this.checkResponse.proof.file_name])

                    case "signatureCheck.success.message1.block2.line2":
                    case "signatureCheck.success.message2.block2.line1":
                        return SignatureCheck.format(res, [this.checkResponse.proof.algorithm, this.checkResponse.proof.hash_document])

                    case "signatureCheck.success.message1.block2.line3":
                    case "signatureCheck.success.message2.block2.line2":
                    case "signatureCheck.success.message2.block2.line6":
                        return SignatureCheck.format(res, [this.checkResponse.proof.hash_root])

                    case "signatureCheck.success.message1.block2.line4":
                    case "signatureCheck.success.message2.block2.line8":
                        return SignatureCheck.format(res, [this.checkResponse.proof.transaction_hash!, this.checkResponse.proof.block_hash!, this.checkResponse.timestamp!.toString()])

                    case "signatureCheck.success.message2.block2.line4":
                        return SignatureCheck.format(res, [this.checkResponse.proof.hash_document])

                    case "signatureCheck.success.message1.block2.line5":
                    case "signatureCheck.success.message2.block2.line9":
                       return SignatureCheck.format(res, [this.checkResponse.proof.origin_public_key, (this.checkResponse.signer !== undefined) ? this.checkResponse.signer : this.$t("signatureCheck.unknown").toString()])

                    case "signatureCheck.success.message1.block2.line7":
                    case "signatureCheck.success.message2.block2.line10":
                        return SignatureCheck.format(res, [this.checkResponse.proof.contract_address])

                    case "signatureCheck.success.message2.block2.line5":
                        let hash = this.checkResponse.proof.hash_list[idx]
                        return SignatureCheck.format(res, [hash.position!, (hash.hash) ? hash.hash : "", this.checkResponse.check_process[idx]])

                    case "signatureCheck.errors.hash_inconsistent":
                        return SignatureCheck.format(res, [this.checkResponse., this.checkResponse.proof.hash_document])

                    case "signatureCheck.errors.unknown_root_hash":
                        return SignatureCheck.format(res, [this.checkResponse.proof.hash_root])

                    case "signatureCheck.errors.document_known_unknown_root_hash":
                        return SignatureCheck.format(res, [(this.checkResponse.signer !== undefined) ? this.checkResponse.signer : this.$t("signatureCheck.unknown").toString(), this.checkResponse.proof.public_key, this.checkResponse.timestamp!.toString()])

                    case "signatureCheck.errors.no_transaction":
                        return SignatureCheck.format(res, [this.checkResponse.proof.hash_root])

                    default:
                        return res
                }
            }
        }

        private parseLink(message: string, placeholder: string, stats: {[key: string]: string}) {
            let format = ""
            let res = this.parse(message)
            for (let i in stats) {
                format = format + i + "=\"" + stats[i] + "\" "
            }
            return res?.replace("{#}", "<a "+format+">" + placeholder + "</a>")
        }

        private check() {
            if (this.file !== null) {
                const reader = new FileReader()
                reader.onload = () => {
                    this.contentFile = reader.result?.toString()

                    if (this.proof !== null) {
                        const readerProof = new FileReader()
                        readerProof.onload = () => {
                            this.contentProof = readerProof.result?.toString()
                            this.encodeSend()
                        }
                        readerProof.readAsText(this.proof)
                    } else {
                        this.encodeSend()
                    }
                }
                reader.readAsText(this.file)
            }
        }

        private encodeSend() {
            console.log(this.contentFile)
            const hashFile: string = CryptoJS.SHA256(this.contentFile!).toString()
            let proof: string | undefined = undefined

            console.log(hashFile)
            console.log(this.contentProof)

            if (this.contentProof !== undefined) {
                proof = btoa(this.contentProof).toString()
            }

            const sigCheck: SignatureCheckRequest = {
                //documentHash: hashFile,
                documentHash: "142f9eb8376093e5c24c74714bef07c187cd9a4a81e4f758515ce21b06b2e12a",
                proof: proof,
            }
            this.$modules.signatures.check(sigCheck)
        }
    }
</script>

