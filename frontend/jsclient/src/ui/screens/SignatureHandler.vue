<template>
    <v-container fluid>
        <v-row class="mx-3 mt-3" justify="space-between">
            <v-col class="pl-4" tag="h1">
                <h1>{{$t('signatureHandler.title') }}</h1>
            </v-col>
        </v-row>
        <v-row>
            <v-flex>
                <Card width="98%">
                    <CardTitle type="number" number=1>{{ $t("signatureHandler.start")}}</CardTitle>
                    <v-flex xs8 lg4 class="ml-4">
                        <EditFormRow :title="$t('signatureHandler.algorithm') + '*'" :editable="true">
                            <v-combobox class="combobox" hide-details outlined dense :items="algorithms" v-model="selectedAlgorithm"/>
                        </EditFormRow>
                        <EditFormRow :title="$t('signatureHandler.name') + '*'" :editable="true">
                            <EditFormTitleEdit v-model="flowName"/>
                        </EditFormRow>
                        <EditFormRow :title="$t('signatureHandler.data')" :editable="true">
                            <v-row v-for="(key, index) in dataKeys" class="row_metadata">
                                <v-col class="col-5">
                                    <v-text-field outlined dense disabled :value="key"/>
                                </v-col>
                                <v-col class="col-5">
                                    <v-text-field outlined dense v-model="dataValues[index]"/>
                                </v-col>
                                <v-col class="col-2">
                                    <v-btn icon @click="removeJobMetadata(index)">
                                        <v-icon>delete</v-icon>
                                    </v-btn>
                                </v-col>
                            </v-row>
                            <v-row class="row_metadata">
                                <v-col class="col-5">
                                    <v-text-field outlined dense v-model="newKey"/>
                                </v-col>
                                <v-col class="col-5">
                                    <v-text-field outlined dense v-model="newValue"/>
                                </v-col>
                                <v-col class="col-2">
                                    <v-btn icon @click="addJobMetadata" :disabled="!canAddJobMetadata">
                                        <v-icon>add</v-icon>
                                    </v-btn>
                                </v-col>
                            </v-row>
                        </EditFormRow>
                        <div class="mandatory">
                           {{ $t("signatureHandler.mandatory") }}
                        </div>
                    </v-flex>

                    <CardTitle type="number" number=2 class="mt-8">{{ $t("signatureHandler.uploadFiles")}}</CardTitle>
                    <v-flex xs8 lg4 class="ml-4">
                        <v-row>
                            <v-col class="col-3">{{ $t("signatureHandler.upload")}}</v-col>
                            <v-col class="col-8">
                                <div @dragover.prevent @drop.prevent="drop">
                                    <v-file-input class="file" outlined dense v-model="files" multiple/>
                                </div>
                            </v-col>
                        </v-row>
                    </v-flex>
                    <v-data-table class="table-header"
                                  :headers="headers"
                                  :items="filesSign"
                                  :footer-props="footer"
                    >
                        <template v-slot:body="{items}">
                            <tbody>
                            <tr v-if="items.length === 0">
                                <td colspan="5">{{ $t("signatureHandler.noData") }}</td>
                            </tr>
                            <tr v-for="(file, index) in items" class="outlined">
                                <td>
                                    <v-text-field outlined dense v-model="file.name" class="pt-5"/>
                                </td>
                                <td class="text-center">{{file.size}} Kio</td>
                                <td class="text-center">{{file.hash}}</td>
                                <td class="text-center" style="vertical-align: top">
                                    <v-row v-for="(key, index) in file.keys" dense align="center" justify="center" class="row_metadata">
                                        <v-col class="col-5">
                                            <v-text-field outlined dense :value="key" disabled/>
                                        </v-col>
                                        <v-col class="col-5">
                                            <v-text-field outlined dense v-model="file.values[index]"/>
                                        </v-col>
                                        <v-col class="col-1 text-center">
                                            <v-btn class="mb-6" icon @click="removeFileMetadata(file, index)">
                                                <v-icon>delete</v-icon>
                                            </v-btn>
                                        </v-col>
                                    </v-row>
                                    <v-row dense align="center" justify="center" class="row_metadata">
                                        <v-col class="col-5">
                                            <v-text-field outlined dense v-model="file.newKey"/>
                                        </v-col>
                                        <v-col class="col-5">
                                            <v-text-field outlined dense v-model="file.newValue"/>
                                        </v-col>
                                        <v-col class="col-1 text-center">
                                            <v-btn :disabled="!canAddFileMetadata(file)" class="mb-6" icon @click="addFileMetadata(file)">
                                                <v-icon>add</v-icon>
                                            </v-btn>
                                        </v-col>
                                    </v-row>
                                </td>
                                <td class="text-center">
                                    <v-btn @click="deleteFile(index)" icon>
                                        <v-icon>delete</v-icon>
                                    </v-btn>
                                </td>
                            </tr>
                            </tbody>
                        </template>
                    </v-data-table>
                    <v-flex class="align-right pt-8">
                        <IconButton leftIcon="cancel" color="red" :disabled="filesSign.length === 0" @click="cancel">{{ $t("signatureHandler.cancel") }}</IconButton>
                        <IconButton class="ml-5" leftIcon="alarm_on" color="var(--var-color-orange-sword)" :disabled="!canSign" @click="sign">{{ $t("signatureHandler.sign") }}</IconButton>
                    </v-flex>
                </Card>
            </v-flex>
        </v-row>
    </v-container>
</template>

<style scoped>
    .combobox {
        max-width: 240px;
    }

    .row_metadata {
        max-height: 50px;
    }

    .file {
        max-width: 320px;
    }

    .mandatory {
        margin-top: 30px;
        font-size: 12px;
    }
</style>

<script lang="ts">
    import {SignatureMultiRequest, SignatureRequest} from "@/api/types"
    import {tableFooter} from "@/plugins/i18n"
    import {FileSign} from "@/store/types"
    import CryptoJS from "crypto-js"
    import {Component, Vue, Watch} from "vue-property-decorator"

    @Component
    export default class SignatureHandler extends Vue {

        private static metadataConversion(item: FileSign) {
            const metadata: { [key: string]: string } = {}
            item.keys.forEach((k, index) => {
                metadata[k] = item.values[index]
            })
            return metadata
        }

        private files: Array<File> = []
        private filesSign: Array<FileSign> = []
        private algorithms: Array<string> = ["SHA-256"]
        private selectedAlgorithm: string = ""
        private flowName: string = ""
        private dataKeys: Array<string> = []
        private dataValues: Array<string> = []
        private newKey: string = ""
        private newValue: string = ""

        private drop(e: any) {
            this.files = e.dataTransfer.files
        }

        @Watch("files")
        private update() {
            this.files.forEach((f: File) => {
                const reader = new FileReader()
                reader.onloadend = (e) => {
                    const wordArray = CryptoJS.lib.WordArray.create(e.target!.result)
                    const hashCrypt = CryptoJS.SHA256(wordArray).toString()
                    this.filesSign.push(
                     {
                         name: f.name,
                         size: f.size,
                         hash: hashCrypt,
                         keys: [],
                         values: [],
                    })
                }
                reader.readAsArrayBuffer(f)
            })
        }

        private addFileMetadata(item: FileSign) {
                item.keys.push(item.newKey!)
                item.values.push(item.newValue!)
                item.newKey = ""
                item.newValue = ""
        }

        private addJobMetadata() {
            this.dataKeys.push(this.newKey)
            this.dataValues.push(this.newValue)
            this.newKey = ""
            this.newValue = ""
        }

        private canAddFileMetadata(item: FileSign) {
            return item.newKey !== undefined && item.newKey !== "" && !item.keys.find((s) => s === item.newKey)
        }

        private get canAddJobMetadata() {
            return this.newKey !== "" && !this.dataKeys.find((s) => s === this.newKey)
        }

        private removeFileMetadata(item: FileSign, key: number) {
            item.keys.splice(key, 1)
            item.values.splice(key, 1)
        }

        private removeJobMetadata(key: number) {
            this.dataKeys.splice(key, 1)
            this.dataValues.splice(key, 1)
        }

        private async sign() {
            const filesUpload: Array<SignatureRequest> = []

            this.filesSign.forEach((f) => {
                filesUpload.push({
                    metadata: {
                        fileName: f.name,
                        fileSize: f.size,
                        customFields: SignatureHandler.metadataConversion(f),
                    },
                    hash: f.hash,
                })
            })
            const customFieldsUpload: {[key: string]: string} = {}

            this.dataKeys.forEach((k, ind) => {
                customFieldsUpload[k] = this.dataValues[ind]
            })

            const signatures: SignatureMultiRequest = {
                algorithm: this.selectedAlgorithm,
                callBackUrl: "",
                flowName: this.flowName,
                files: filesUpload,
                customFields: customFieldsUpload,
            }

            this.$modules.signatures.signMulti(signatures).then(() => {
                this.$router.push("/jobs/")
            })
        }

        private get headers() {
            return [
                {text: this.$t("signatureHandler.fileName"), align: "center", value: "name", width: "25%"},
                {text: this.$t("signatureHandler.size"), align: "center", value: "size", width: "15%"},
                {text: this.$t("signatureHandler.hash"), align: "center", value: "hash", width: "20%"},
                {text: this.$t("signatureHandler.keyValue"), align: "center", sortable: false, width: "35%"},
                {text: "", sortable: false, width: "5%"},
            ]
        }

        private get footer() {
            return tableFooter((key: string) => this.$t(key))
        }

        private deleteFile(index: number) {
            this.filesSign.splice(index, 1)
        }

        private cancel() {
            this.selectedAlgorithm = ""
            this.files = []
            this.filesSign = []
            this.flowName = ""
            this.dataValues = []
            this.dataKeys = []
        }

        private get canSign() {
            return (this.filesSign.length !== 0 && this.selectedAlgorithm !== "" && this.flowName !== "")
        }
    }
</script>