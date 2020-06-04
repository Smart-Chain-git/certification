<template>
    <div>
        <v-app-bar app fixed dense class="elevation-2" :color="backgroundColor">
            <div class="ml-4" v-if="showLogo">
                <img src="@/ui/assets/logo_sword_white.png" height="40"/>
            </div>
            <!-- Takes all the space (puts the next element at the end of the row). -->
            <div class="flex-grow-1"/>
            <v-toolbar-items>
                <v-divider vertical/>
                <v-menu bottom left>
                    <template v-slot:activator="{ on }">
                        <v-btn class="mr-4" text v-on="on" v-if="isUserConnected">
                            <v-icon :color="textColor" class="mr-1">person</v-icon>
                            <div :style="{ color: `${textColor}` }">
                                {{ $t('menu.hello')}} {{ meName }}
                            </div>
                            <v-icon :color="textColor">expand_more</v-icon>
                        </v-btn>
                        <v-btn text v-else @click="login">
                            <v-icon :color="textColor" class="mr-1">person</v-icon>
                            <div :style="{ color: `${textColor}` }">
                                {{ $t('menu.login') }}
                            </div>
                            <v-icon :color="textColor">expand_more</v-icon>
                        </v-btn>
                    </template>
                    <v-list class="mt-9" v-if="isUserConnected">
                        <v-list-item to="/dashboard">
                            <v-list-item-action class="centered-icon">
                                <v-icon color="var(--var-color-blue-sword)">home</v-icon>
                            </v-list-item-action>
                            <v-list-item-title>{{ $t('menu.dashboard') }}</v-list-item-title>
                        </v-list-item>
                        <v-list-item to="/profile">
                            <v-list-item-action class="centered-icon">
                                <v-icon color="var(--var-color-blue-sword)">account_box</v-icon>
                            </v-list-item-action>
                            <v-list-item-title>{{ $t('menu.profile') }}</v-list-item-title>
                        </v-list-item>

                        <v-list-item to="/channel-management">
                            <v-list-item-action class="centered-icon">
                                <v-icon color="var(--var-color-blue-sword)">queue</v-icon>
                            </v-list-item-action>
                            <v-list-item-title>{{ $t('menu.channelManagement') }}</v-list-item-title>
                        </v-list-item>
                        <v-list-item href="#" @click.native="logout">
                            <v-list-item-action class="centered-icon">
                                <v-icon color="var(--var-color-blue-sword)">lock_open</v-icon>
                            </v-list-item-action>
                            <v-list-item-title>{{ $t('menu.logout') }}</v-list-item-title>
                        </v-list-item>
                    </v-list>
                </v-menu>
            </v-toolbar-items>
        </v-app-bar>
    </div>
</template>

<style lang="scss">
    :root {
        --var-color-blue-sword: #225588;
        --var-color-orange-sword: #ffc423;
        --var-color-light-orange-sword: #506b77;
    }

    img {
        margin-bottom: 15px;
    }

    .v-list-item:hover {
        color: white !important;
        border-left: solid var(--var-color-orange-sword) 10px !important;
        box-sizing: content-box;
        padding-left: 6px;
    }
</style>
<script lang="ts">
    import {Component, Vue, Prop} from "vue-property-decorator"

    @Component
    export default class NavbarTop extends Vue {
        @Prop(Boolean) private showLogo!: boolean
        @Prop(String) private backgroundColor!: string
        @Prop(String) private textColor!: string

        private get isUserConnected() {
            return this.$modules.accounts.meAccount !== undefined
        }

        private get meName() {
            return this.$modules.accounts.meName
        }

        private login() {
            this.$router.push("/login")
        }

        private logout() {
            this.$modules.accounts.invalidateToken()
            if (this.$router.currentRoute.path !== "/signature-check") {
                console.log("logged out")
                this.$router.push("/signature-check")
            }
        }
    }
</script>

