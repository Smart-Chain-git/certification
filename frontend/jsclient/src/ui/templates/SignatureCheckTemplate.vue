<template>
    <div>
        <div class="background">
            <img src="@/ui/assets/reception-background.png" alt="reception background">
        </div>
        <NavbarTop showLogo="true" backgroundColor="var(--var-color-blue-sword)" textColor="white"/>
        <v-content>
            <v-flex class="white">
                <v-btn text to="/signature-check">
                    <v-icon>check_circle_outline</v-icon>
                    <span class="ml-5">{{ $t('signatureCheck.check')}}</span>
                </v-btn>
                <v-btn text to="/about">
                    <v-icon>account_box</v-icon>
                    <span class="ml-5">{{ $t('signatureCheck.about')}}</span>
                </v-btn>
                <v-btn text to="/contact">
                    <v-icon>local_cafe</v-icon>
                    <span class="ml-5">{{ $t('signatureCheck.contact')}}</span>
                </v-btn>
            </v-flex>
            <v-divider/>
            <v-flex>
                <v-container fill-height>
                    <v-layout align-center justify-center>
                        <div>
                            <router-view/>
                        </div>
                    </v-layout>
                </v-container>
            </v-flex>
        </v-content>
    </div>
</template>

<style lang="scss" scoped>
    .white {
        background-color: white;
    }

    .background {
        position: fixed;
        top: -50%;
        left: -50%;
        width: 200%;
        height: 200%;
    }

    .background img {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        margin: auto;
        min-width: 50%;
        min-height: 50%;
        /* The blur of 4px was defined with try-and-errors. */
        filter: blur(4px);
    }

    .v-btn--active {
        background-color: var(--var-color-orange-sword);
        opacity: 0.9;
    }
</style>
<script lang="ts">
    import {Component, Vue} from 'vue-property-decorator'

    @Component
    export default class SignatureCheckTemplate extends Vue {
    }
</script>

