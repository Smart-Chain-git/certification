<template>
  <v-container fluid>
    <v-flex>
      <Card>
        <h2>{{ $t("signatureCheck.title") }}</h2>
        <h2>{{ $t("signatureCheck.subtitle") }}</h2>
        <p class="small-text">
          {{ $t("signatureCheck.text1") }}<br/>
          {{ $t("signatureCheck.text2") }}
        </p>
        <h2 v-if="hashdocument != null">{{ $t("signatureCheck.hash") }} : {{ hashdocument }}</h2>
        <v-flex v-else class="mt-5">
          <v-row>
            <v-col class="col-4 align-right"><h1>{{ $t("signatureCheck.upload") }}</h1></v-col>
            <v-col class="col-5">
              <div @dragover.prevent @drop.prevent="dropFile">
                <v-file-input prepend-icon="" prepend-inner-icon="publish" filled
                              :placeholder="$t('signatureCheck.drop')" outlined v-model="file"
                              @click:clear="resetFile"/>
              </div>
            </v-col>
          </v-row>
          <v-row>
            <v-col class="col-4 align-right"><h1>{{ $t("signatureCheck.uploadProof") }}</h1>
              <span>{{ $t("signatureCheck.optional") }}</span>
            </v-col>
            <v-col class="col-5">
              <div @dragover.prevent @drop.prevent="dropProof">
                <v-file-input prepend-icon="" prepend-inner-icon="publish" filled
                              :placeholder="$t('signatureCheck.drop')" outlined v-model="proof"
                              accept="application/json" @click:clear="resetProof"/>
              </div>
            </v-col>
          </v-row>
        </v-flex>
        <v-flex v-if="checkResponse">
          <v-row class="banner" justify="center" align="center">
            <v-col class="col-2">
              <div class="text-center">
                <v-icon size="100" :color="checkSucceeded ? 'green' : 'red'">{{
                    checkSucceeded ?
                        "check_circle_outline" : "cancel"
                  }}
                </v-icon>
              </div>
            </v-col>
            <v-col class="col-10 pt-4">
              <h1 :class="'title_'+(checkSucceeded ? 'success' : 'error')"> {{
                  mainMessage("title")
                }}</h1>
              <p class="pt-4">{{ mainMessage("message") }}</p>
            </v-col>
          </v-row>
          <v-row v-if="checkSucceeded">
            <v-expansion-panels v-if="checkSucceeded" flat>
              <v-expansion-panel class="more_info">
                <v-expansion-panel-header class="banner accordion_header">
                  <span class="align-right pt-2">{{ $t("signatureCheck.more") }}</span>
                </v-expansion-panel-header>
                <v-expansion-panel-content class="pt-4">
                  <div v-if="checkResponse.check_status === 1">
                    {{ parse("signatureCheck.success.message1.block2.line1") }}<br/><br/>
                    {{ parse("signatureCheck.success.message1.block2.line2") }}<br/>
                    {{ parse("signatureCheck.success.message1.block2.line3") }}<br/>
                    {{ parse("signatureCheck.success.message1.block2.line4") }}<br/>
                    {{ parse("signatureCheck.success.message1.block2.line5") }}<br/><br/>
                    <span v-html="parseLink('signatureCheck.success.message1.block2.line6', {
                                        'download' : checkResponse.proof.file_name + '.json',
                                        'href' : 'data:text/json:charset=utf-8,' + encodeURIComponent(JSON.stringify(checkResponse.proof))
                                    })"/><br/><br/>
                    <span v-html="parseLink('signatureCheck.success.message1.block2.line7', {'href' : '/settings'})"/>
                  </div>
                  <div v-if="checkResponse.check_status === 2">
                    {{ parse("signatureCheck.success.message2.block2.line1") }}<br/><br/>
                    {{ parse("signatureCheck.success.message2.block2.line2") }}<br/><br/>
                    {{ parse("signatureCheck.success.message2.block2.line3") }}<br/>
                    {{ parse("signatureCheck.success.message2.block2.line4") }}<br/>

                    <span v-for="(hash, idx) in checkResponse.proof.hash_list">
                                        {{ parse("signatureCheck.success.message2.block2.line5", idx) }}<br/>
                                    </span>
                    <br/>
                    {{ parse("signatureCheck.success.message2.block2.line6") }}<br/><br/>
                    {{ parse("signatureCheck.success.message2.block2.line7") }}<br/><br/>
                    {{ parse("signatureCheck.success.message2.block2.line8") }}<br/><br/>
                    {{ parse("signatureCheck.success.message2.block2.line9") }}<br/><br/>
                    <span
                        v-html="parseLink('signatureCheck.success.message2.block2.line10', 'here', {'href' : '/settings'})"/>
                  </div>
                </v-expansion-panel-content>
              </v-expansion-panel>
            </v-expansion-panels>
          </v-row>
        </v-flex>
        <v-flex v-if="checkResponse" class="mb-5">
          <v-row>
            <v-col class="col-2"></v-col>
            <v-col class="col-8 small">{{ $t("signatureCheck.success.info.title") }}</v-col>
          </v-row>
          <v-row>
            <v-col class="col-2"></v-col>
            <v-col class="col-4 small">
              <h3>{{ $t("signatureCheck.success.info.col1.title") }}</h3>
              <ol>
                <li>{{ $t("signatureCheck.success.info.col1.step1") }}</li>
                <li>{{ $t("signatureCheck.success.info.col1.step2") }}</li>
                <li>{{ $t("signatureCheck.success.info.col1.step3") }}</li>
                <li>{{ $t("signatureCheck.success.info.col1.step4") }}</li>
              </ol>
            </v-col>
            <v-col class="col-5 small">
              <h3>{{ $t("signatureCheck.success.info.col2.title") }}</h3>
              <ol>
                <li>{{ $t("signatureCheck.success.info.col2.step1") }}</li>
                <li>{{ $t("signatureCheck.success.info.col2.step2") }}</li>
                <li>{{ $t("signatureCheck.success.info.col2.step3") }}</li>
                <li>{{ $t("signatureCheck.success.info.col2.step4") }}</li>
                <li>{{ $t("signatureCheck.success.info.col2.step5") }}</li>
                <li>{{ $t("signatureCheck.success.info.col2.step6") }}</li>
                <li>{{ $t("signatureCheck.success.info.col2.step7") }}</li>
              </ol>
            </v-col>
            <v-col class="col-1"></v-col>
          </v-row>
        </v-flex>
        <div class="text-center">
          <IconButton v-if="!checkResponse" leftIcon="double_arrow" @click="check"
                      color="var(--var-color-blue-sword)" :disabled="!file && !hashdocument">{{
              $t("signatureCheck.generate")
            }}
          </IconButton>
          <IconButton v-else leftIcon="double_arrow" @click="fullReload" color="var(--var-color-blue-sword)">
            {{ $t("signatureCheck.regenerate") }}
          </IconButton>
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

.banner {
  background-color: #feedc7;
}

.small {
  font-size: 10px;
}

.accordion_header {
  vertical-align: middle;
  font-size: 12px;
}

.v-expansion-panel-header {
  min-height: 30px !important;
  padding: 0;
  margin: 0;
}

.title_success {
  color: green;
}

.title_error {
  color: red;
}

.more_info {
  font-size: 12px;
  background-color: #fffaef !important;
}

.small-text {
  font-size: 12px;
  margin-left: 15px;
  margin-right: 15px;
  text-align: justify;
}

</style>
<script lang="ts">
import {SignatureCheckRequest, SignatureCheckResponse} from "@/api/types"
import {Component, Prop, Vue} from "vue-property-decorator"
import * as CryptoJS from "crypto-js"


@Component
export default class SignatureCheck extends Vue {
  private static format(str: string, values: Array<string>) {
    for (let i = 0; i < values.length; ++i) {
      str = str.replace("{" + i + "}", values[i])
    }
    return str
  }

  @Prop({default: null}) private readonly hashdoc!: string | null

  private file: File | null = null
  private proof: File | null = null
  private fileHash: string = ""
  private proofHash: string | undefined = undefined
  private hashdocument: string | null = this.hashdoc


  private mounted() {
    this.reload()

  }

  private dropFile(e: any) {
    this.file = e.dataTransfer.files[0]
  }

  private resetFile() {
    this.file = null
  }

  private dropProof(e: any) {
    this.proof = e.dataTransfer.files[0]
  }

  private resetProof() {
    this.proof = null
  }


  private get checkResponse(): SignatureCheckResponse | undefined {
    return this.$modules.signatures.getCheckResponse()
  }

  public get checkSucceeded() {
    return this.checkResponse?.output === "OK"
  }

  private mainMessage(part: string) {
    if (this.checkSucceeded) {
      return this.parse("signatureCheck.success.message" + this.checkResponse?.check_status! + ".block1." + part)
    } else {
      return this.parse("signatureCheck.errors." + this.checkResponse?.error?.toLowerCase() + "." + part)
    }
  }

  private parse(message: string, idx: number = 0) {
    if (this.checkResponse) {
      const res: string = this.$t(message).toString()

      switch (message) {
        case "signatureCheck.success.message1.block1.message":
          return SignatureCheck.format(res, [
            this.checkResponse.signer || this.$t("signatureCheck.unknown").toString(),
            this.checkResponse.timestamp!.toString()])

        case "signatureCheck.success.message2.block1.message":
          return SignatureCheck.format(res, [
            this.checkResponse.signer || this.$t("signatureCheck.unknown").toString(),
            this.checkResponse.timestamp!.toString()])

        case "signatureCheck.success.message1.block2.line1":
          return SignatureCheck.format(res, [this.checkResponse.proof.file_name])

        case "signatureCheck.success.message1.block2.line2":
        case "signatureCheck.success.message2.block2.line1":
          return SignatureCheck.format(res, [this.checkResponse.proof.algorithm,
            this.checkResponse.proof.hash_document])

        case "signatureCheck.success.message1.block2.line3":
        case "signatureCheck.success.message2.block2.line2":
        case "signatureCheck.success.message2.block2.line6":
          return SignatureCheck.format(res, [this.checkResponse.proof.hash_root])

        case "signatureCheck.success.message1.block2.line4":
        case "signatureCheck.success.message2.block2.line8":
          return SignatureCheck.format(res, [this.checkResponse.proof.transaction_hash!,
            this.checkResponse.proof.block_hash!, this.checkResponse.timestamp!.toString()])

        case "signatureCheck.success.message2.block2.line4":
          return SignatureCheck.format(res, [this.checkResponse.proof.hash_document])

        case "signatureCheck.success.message1.block2.line5":
        case "signatureCheck.success.message2.block2.line9":
          return SignatureCheck.format(res, [this.checkResponse.proof.origin_public_key,
            this.checkResponse.signer || this.$t("signatureCheck.unknown").toString()])

        case "signatureCheck.success.message1.block2.line7":
        case "signatureCheck.success.message2.block2.line10":
          return SignatureCheck.format(res, [this.checkResponse.proof.contract_address])

        case "signatureCheck.success.message2.block2.line5":
          const hash = this.checkResponse.proof.hash_list[idx]
          return SignatureCheck.format(res, [hash.position!, hash.hash || "",
            this.checkResponse.check_process[idx]])

        case "signatureCheck.errors.hash_inconsistent.message":
          return SignatureCheck.format(res, [this.checkResponse.hash_document!,
            this.checkResponse.hash_document_proof!])

        case "signatureCheck.errors.unknown_root_hash.message":
          return SignatureCheck.format(res, [this.checkResponse.hash_root!])

        case "signatureCheck.errors.document_known_unknown_root_hash.message":
          return SignatureCheck.format(res, [
            this.checkResponse.signer || this.$t("signatureCheck.unknown").toString(),
            this.checkResponse.public_key!, this.checkResponse.date!.toString()])

        case "signatureCheck.errors.no_transaction.message":
          return SignatureCheck.format(res, [this.checkResponse.hash_root!])

        case "signatureCheck.errors.incorrect_hash.message":
          return SignatureCheck.format(res, [this.checkResponse.hash!, this.checkResponse.proof_file_hash!])

        case "signatureCheck.errors.incorrect_origin_public_key.message":
          return SignatureCheck.format(res, [this.checkResponse.proof_file_origin_public_key!,
            this.checkResponse.origin_public_key!])

        case "signatureCheck.errors.incorrect_signature_date.message":
          return SignatureCheck.format(res, [this.formatTimestamp(this.checkResponse.proof_file_signature_date),
            this.formatTimestamp(this.checkResponse.signature_date)])

        case "signatureCheck.errors.incorrect_hash_algorithm.message":
          return SignatureCheck.format(res, [this.checkResponse.proof_file_algorithm!, this.checkResponse.hash!])

        case "signatureCheck.errors.unknown_hash_algorithm.message":
          return SignatureCheck.format(res, [this.checkResponse.proof_file_algorithm!])

        case "signatureCheck.errors.incorrect_public_key.message":
          return SignatureCheck.format(res, [this.checkResponse.proof_file_public_key!, this.checkResponse.public_key!])

        case "signatureCheck.errors.incorrect_contract_address.message":
          return SignatureCheck.format(res, [this.checkResponse.proof_file_contract_address!,
            this.checkResponse.contract_address!])

        default:
          return res
      }
    }
  }

  private parseLink(message: string, stats: { [key: string]: string }) {
    let format = ""
    const res = this.parse(message)
    for (const i of Object.keys(stats)) {
      format = format + i + "=\"" + stats[i] + "\" "
    }
    return res?.replace("{#}", "<a " + format + ">" + this.$t("signatureCheck.here").toString() + "</a>")
  }

  private formatTimestamp(date?: Date) {
    if (date === undefined) {
      return "UNDEFINED"
    }
    switch (this.$i18n.locale) {
      case "fr":
        return this.$moment(date).format("DD/MM/YYYY HH:mm:ss")
      default:
        return this.$moment(date).format("YYYY/MM/DD HH:mm:ss")
    }

  }

  private check() {
    if (this.hashdocument != null) {
      this.fileHash = this.hashdocument
      this.send()
    } else if (this.file !== null) {
      const reader = new FileReader()
      reader.onloadend = (e) => {
        const wordArray = CryptoJS.lib.WordArray.create(e.target!.result)
        this.fileHash = CryptoJS.SHA256(wordArray).toString()
        if (this.proof !== null) {
          const readerProof = new FileReader()
          readerProof.onloadend = (e2) => {
            this.proofHash = e2.target!.result!.toString().substr("data:application/json;base64,".length)
            this.send()
          }
          readerProof.readAsDataURL(this.proof!)
        } else {
          this.send()
        }
      }
      reader.readAsArrayBuffer(this.file!)
    }
  }

  private send() {
    const sigCheck: SignatureCheckRequest = {
      documentHash: this.fileHash,
      proof: this.proofHash,
    }
    this.$modules.signatures.check(sigCheck)
  }

  private fullReload() {
    this.hashdocument = null
    this.reload()

  }

  private reload() {
    this.file = null
    this.proof = null
    this.fileHash = ""
    this.proofHash = undefined
    this.$modules.signatures.reset()
  }
}
</script>
