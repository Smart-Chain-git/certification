<template>
    <v-row justify="center">
        <v-dialog v-model="display" persistent :max-width="maxWidth">
            <v-card>
                <v-card-title class="headline dialog_top" v-html="$t(title)"></v-card-title>
                <div class="popupContent">
                    <v-card-title class="subtitle-1" v-if="subTitle != null" v-html="$t(subTitle)"></v-card-title>
                    <v-card-text v-if="content != null" class="subtitle-2" v-html="$t(content)"></v-card-text>
                    <v-card-actions class="buttonLine">
                        <v-spacer></v-spacer>
                        <IconButton class="cancelButton" @click="cancelAction" v-if="cancelButtonText"
                                    :leftIcon="cancelButtonIcon">
                            {{ $t(cancelButtonText) }}
                        </IconButton>
                        <IconButton class="confirmButton" @click="confirmAction" v-if="confirmButtonText"
                                    :leftIcon="confirmButtonIcon">
                            {{ $t(confirmButtonText) }}
                        </IconButton>
                    </v-card-actions>
                </div>
            </v-card>
        </v-dialog>
    </v-row>
</template>

<style type="text/css" scoped>
    .popupContent {
        padding: 20px 40px 40px;
    }

    .buttonLine {
        padding-top: 20px
    }

    .cancelButton {
        color: white;
        background-color: #485B65 !important;
    }
    .confirmButton {
        color: white;
        background-color: #687D37 !important;
    }
    .dialog_top {
        background-color: var(--var-color-blue-sword);
        color: white;
    }
</style>

<script lang="ts">
    import {Component, Vue, Prop} from "vue-property-decorator"

    @Component
    export default class Dialog extends Vue {
        @Prop({default: 400, type: Number}) private maxWidth!: number
        @Prop() private readonly title!: string
        @Prop({default: null}) private readonly subTitle!: string
        @Prop({default: null}) private readonly content!: string
        @Prop({default: null, type: Function}) private readonly cancelAction!: () => void
        @Prop({default: null, type: Function}) private readonly confirmAction!: () => void

        @Prop({default: null}) private readonly cancelButtonText!: string
        @Prop({default: null}) private readonly confirmButtonText!: string

        @Prop({default: null}) private readonly cancelButtonIcon!: string
        @Prop({default: null}) private readonly confirmButtonIcon!: string

        @Prop() private display!: boolean
    }
</script>
