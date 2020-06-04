<!--
    This component is used to create the standard template for an app's screen.
    It currently renders the top bar and the left menu.

    It is used in the router and can render nested routes/components.
-->

<template>
    <div id="rootMenu">
        <!-- Mobile header-->
        <div v-if="$vuetify.breakpoint.xs || $vuetify.breakpoint.sm">
            <v-app-bar fixed dense>
                <v-toolbar class="pa-0 toolbar" dark dense>
                    <v-toolbar-menu class="ml-6" style="background-color:blue;">
                        <img src="@/ui/assets/logo_sword_white.png"/>
                    </v-toolbar-menu>
                    <v-spacer/>
                    <v-menu right>
                        <template v-slot:activator="{ on }">
                            <v-btn class="mr-6" text v-on="on">
                                <v-icon>menu</v-icon>
                            </v-btn>
                        </template>
                        <v-list id="mobileMenu" class="mt-9">
                            <v-list-item ref="side-menu-dashboard" to="/dashboard">
                                <v-list-item-action>
                                    <v-icon color="var(--var-color-orange-sword)">home</v-icon>
                                </v-list-item-action>
                                <v-list-item-title>{{ $t('menu.dashboard') }}</v-list-item-title>
                            </v-list-item>
                            <v-list-item ref="side-menu-jobs" to="/jobs">
                                <v-list-item-action>
                                    <v-icon color="var(--var-color-orange-sword)">list</v-icon>
                                </v-list-item-action>
                                <v-list-item-title>{{ $t('menu.jobs') }}</v-list-item-title>
                            </v-list-item>
                            <v-list-item ref="side-menu-documents" to="/documents">
                                <v-list-item-action>
                                    <v-icon color="var(--var-color-orange-sword)">description</v-icon>
                                </v-list-item-action>
                                <v-list-item-title>{{ $t('menu.documents') }}</v-list-item-title>
                            </v-list-item>
                            <v-list-item v-if="hasPublicKey" ref="side-menu-signature-request" to="/signature-request">
                                <v-list-item-action>
                                    <v-icon color="var(--var-color-orange-sword)">alarm_on</v-icon>
                                </v-list-item-action>
                                <v-list-item-title>{{ $t('menu.signatureRequest') }}</v-list-item-title>
                            </v-list-item>
                            <v-list-item ref="side-menu-signature-check" to="/signature-check">
                                <v-list-item-action>
                                    <v-icon color="var(--var-color-orange-sword)">check_circle_outline</v-icon>
                                </v-list-item-action>
                                <v-list-item-title>{{ $t('menu.signatureCheck') }}</v-list-item-title>
                            </v-list-item>
                            <v-list-item ref="side-menu-resources" to="/resources">
                                <v-list-item-action>
                                    <v-icon color="var(--var-color-orange-sword)">info</v-icon>
                                </v-list-item-action>
                                <v-list-item-title>{{ $t('menu.resources') }}</v-list-item-title>
                            </v-list-item>
                            <v-list-item v-if="isAdmin" ref="side-menu-settings" to="/settings">
                                <v-list-item-action>
                                    <v-icon color="var(--var-color-orange-sword)">settings</v-icon>
                                </v-list-item-action>
                                <v-list-item-title>{{ $t('menu.settings') }}</v-list-item-title>
                            </v-list-item>
                            <v-list-item to="/profile">
                                <v-list-item-action class="centered-icon">
                                    <v-icon>account_box</v-icon>
                                </v-list-item-action>
                                <v-list-item-title>{{ $t('menu.profile') }}</v-list-item-title>
                            </v-list-item>

                            <v-list-item to="/channel-management">
                                <v-list-item-action class="centered-icon">
                                    <v-icon>queue</v-icon>
                                </v-list-item-action>
                                <v-list-item-title>{{ $t('menu.channelManagement') }}</v-list-item-title>
                            </v-list-item>
                            <v-list-item href="#" @click.native="logout">
                                <v-list-item-action class="centered-icon">
                                    <v-icon>lock_open</v-icon>
                                </v-list-item-action>
                                <v-list-item-title>{{ $t('menu.logout') }}</v-list-item-title>
                            </v-list-item>
                        </v-list>
                    </v-menu>
                </v-toolbar>
            </v-app-bar>
            <v-content class="mobile-content">
                <router-view :key="$route.path"/>
            </v-content>
        </div>
        <!-- Desktop header-->
        <div v-else>
            <v-navigation-drawer dark app permanent>
                <v-toolbar dark dense>
                    <v-toolbar-menu class="toolbar-menu ml-4">
                        <img src="@/ui/assets/logo_sword_white.png" height="40"/>
                    </v-toolbar-menu>
                </v-toolbar>
                <v-list dense>
                    <v-list-item ref="side-menu-dashboard" to="/dashboard">
                        <v-list-item-action>
                            <v-icon color="var(--var-color-orange-sword)">home</v-icon>
                        </v-list-item-action>
                        <v-list-item-title>{{ $t('menu.dashboard') }}</v-list-item-title>
                    </v-list-item>

                    <v-list-item ref="side-menu-jobs" to="/jobs">
                        <v-list-item-action>
                            <v-icon color="var(--var-color-orange-sword)">list</v-icon>
                        </v-list-item-action>
                        <v-list-item-title>{{ $t('menu.jobs') }}</v-list-item-title>
                    </v-list-item>

                    <v-list-item ref="side-menu-documents" to="/documents">
                        <v-list-item-action>
                            <v-icon color="var(--var-color-orange-sword)">description</v-icon>
                        </v-list-item-action>
                        <v-list-item-title>{{ $t('menu.documents') }}</v-list-item-title>
                    </v-list-item>
                    <v-list-item v-if="hasPublicKey" ref="side-menu-signature-request" to="/signature-request">
                        <v-list-item-action>
                            <v-icon color="var(--var-color-orange-sword)">alarm_on</v-icon>
                        </v-list-item-action>
                        <v-list-item-title>{{ $t('menu.signatureRequest') }}</v-list-item-title>
                    </v-list-item>

                    <v-list-item ref="side-menu-signature-check" to="/signature-check">
                        <v-list-item-action>
                            <v-icon color="var(--var-color-orange-sword)">check_circle_outline</v-icon>
                        </v-list-item-action>
                        <v-list-item-title>{{ $t('menu.signatureCheck') }}</v-list-item-title>
                    </v-list-item>

                    <v-list-item ref="side-menu-resources" to="/resources">
                        <v-list-item-action>
                            <v-icon color="var(--var-color-orange-sword)">info</v-icon>
                        </v-list-item-action>
                        <v-list-item-title>{{ $t('menu.resources') }}</v-list-item-title>
                    </v-list-item>

                    <v-list-item v-if="isAdmin" ref="side-menu-settings" to="/settings">
                        <v-list-item-action>
                            <v-icon color="var(--var-color-orange-sword)">settings</v-icon>
                        </v-list-item-action>
                        <v-list-item-title>{{ $t('menu.settings') }}</v-list-item-title>
                    </v-list-item>
                </v-list>
            </v-navigation-drawer>
            <v-app-bar app fixed class="elevation-2" dense>
                <!-- Takes all the space (puts the next element at the end of the row). -->
                <div class="flex-grow-1"/>
                <v-toolbar-items>
                    <v-divider vertical/>
                    <v-menu bottom left>
                        <template v-slot:activator="{ on }">
                            <v-btn class="mr-4" text v-on="on">
                                <div v-if="">
                                    <v-icon class="mr-1">person</v-icon>
                                    {{ $t('menu.hello')}} {{ meName }}
                                </div>
                                <v-icon>expand_more</v-icon>
                            </v-btn>
                        </template>
                        <v-list class="mt-9">
                            <v-list-item to="/dashboard">
                                <v-list-item-action class="centered-icon">
                                    <v-icon>home</v-icon>
                                </v-list-item-action>
                                <v-list-item-title>{{ $t('menu.dashboard') }}</v-list-item-title>
                            </v-list-item>
                            <v-list-item to="/profile">
                                <v-list-item-action class="centered-icon">
                                    <v-icon>account_box</v-icon>
                                </v-list-item-action>
                                <v-list-item-title>{{ $t('menu.profile') }}</v-list-item-title>
                            </v-list-item>

                            <v-list-item to="/channel-management">
                                <v-list-item-action class="centered-icon">
                                    <v-icon>queue</v-icon>
                                </v-list-item-action>
                                <v-list-item-title>{{ $t('menu.channelManagement') }}</v-list-item-title>
                            </v-list-item>
                            <v-list-item href="#" @click.native="logout">
                                <v-list-item-action class="centered-icon">
                                    <v-icon>lock_open</v-icon>
                                </v-list-item-action>
                                <v-list-item-title>{{ $t('menu.logout') }}</v-list-item-title>
                            </v-list-item>
                        </v-list>
                    </v-menu>
                </v-toolbar-items>
            </v-app-bar>
            <v-content>
                <router-view :key="$route.path"/>
            </v-content>
        </div>
    </div>
</template>

<style lang="scss">

    #rootMenu {
        --var-color-blue-sword: #225588;
        --var-color-orange-sword: #ffc423;
        --var-color-light-orange-sword: #506b77;
    }

    img {
        margin-left: 30px;
        margin-top: 20px;
    }

    .centered-icon {
        margin-right: 5px !important;
        margin-left: 0 !important;
        padding: 0 !important;
    }

    .v-toolbar {
        contain: none !important;
    }

    .v-list-group__header__append-icon {
        min-width: 20px !important;
    }

    .v-toolbar__content {
        padding: 0px !important;
    }

    .v-navigation-drawer__content {
        background-color: var(--var-color-blue-sword);
    }

    .v-navigation-drawer__content .v-toolbar__content {
        background-color: var(--var-color-blue-sword);
    }

    .mobile-content {
        margin-top: 48px !important;
    }

    #mobileMenu .centered-icon {
        margin-right: 10px !important;
    }

    .v-menu__content {
        z-index: 5 !important;
    }

    .v-input {
        padding: 0 !important;
        margin: 0 !important;
    }

    #mobileMenu hr {
        border-top: none;
        border-right: none;
        border-left: none;
        border-bottom: solid 1px;
    }

    .v-navigation-drawer {
        .v-list {
            background-color: var(--var-color-blue-sword) !important;
        }

        .v-list-item:hover {
            opacity: 0.9 !important;
        }

        .v-list-item__menu {
            font-size: 12px !important;
        }

        .v-list-group__items .v-list-item {
            background-color: var(--var-color-blue-sword) !important;
        }

        .v-list-item__action {
            margin: 0 0 0 0;
        }

        .v-list-item--active {
            background-color: var(--var-color-light-orange-sword) !important;
            color: white !important;
            border-left: solid var(--var-color-orange-sword) 10px;
            box-sizing: border-box;
            padding-left: 6px;
        }
    }
</style>

<script lang="ts">
    import {Component, Vue, Watch} from "vue-property-decorator"

    @Component
    export default class AppTemplate extends Vue {

        /**
         * Watcher on router which verifies that the user is well-authenticated before accessing the app routes.
         * @param newUrl Route to be accessed
         */
        @Watch("$route", {immediate: true, deep: true})
        private checkAuthentication(newUrl: string) {
            if (this.$modules.accounts.token === undefined) {
                this.$router.push("/login")
            }
        }

        private logout() {
            this.$router.push("/signature-check")
        }

        private get meName() {
            return this.$modules.accounts.meName
        }

        private get isAdmin() {
            return this.$modules.accounts.meAccount?.isAdmin
        }

        private get hasPublicKey() {
            return this.$modules.accounts.meAccount?.pubKey !== null
        }

    }
</script>
