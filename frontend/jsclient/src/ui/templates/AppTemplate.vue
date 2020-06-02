<!--
    This component is used to create the standard template for an app's screen.
    It currently renders the top bar and the left menu.

    It is used in the router and can render nested routes/components.
-->

<template>
    <div>
        <!-- Mobile header-->
        <div v-if="$vuetify.breakpoint.xs || $vuetify.breakpoint.sm">
            <v-app-bar fixed dense>
                <v-toolbar class="pa-0 toolbar" dark dense>
                    <v-toolbar-title class="ml-6">Kami Outside</v-toolbar-title>
                    <v-spacer/>
                    <v-menu right>
                        <template v-slot:activator="{ on }">
                            <v-btn class="mr-6" text v-on="on">
                                <v-icon>menu</v-icon>
                            </v-btn>
                        </template>
                        <v-list id="mobileMenu" class="mt-9">
                            <v-list-item ref="side-menu-messages" to="/messages">
                                <v-list-item-action class="centered-icon">
                                    <v-icon>mail_outline</v-icon>
                                </v-list-item-action>
                                <v-list-item-title>{{ $t('title.all-messages') }}</v-list-item-title>
                            </v-list-item>

                            <v-list-item v-if="canCreateMessage" ref="side-menu-create-message" to="/create-message">
                                <v-list-item-action class="centered-icon">
                                    <v-icon>add_circle_outline</v-icon>
                                </v-list-item-action>
                                <v-list-item-title>{{ $t('title.create-message') }}</v-list-item-title>
                            </v-list-item>
                            <hr/>
                            <v-list-item ref="side-menu-milestones" to="/campaigns">
                                <v-list-item-action class="centered-icon">
                                    <v-icon>date_range</v-icon>
                                </v-list-item-action>
                                <v-list-item-title>{{ $t('title.all-milestones') }}</v-list-item-title>
                            </v-list-item>
                            <v-list-item v-if="canCreateMessage" to="/create-campaign">
                                <v-list-item-action class="centered-icon">
                                    <v-icon>add_circle_outline</v-icon>
                                </v-list-item-action>
                                <v-list-item-title>{{ $t('title.create-milestone') }}</v-list-item-title>
                            </v-list-item>
                            <v-list-item v-if="canManageExports" to="/campaigns/export">
                                <v-list-item-action class="centered-icon">
                                    <v-icon>arrow_downward</v-icon>
                                </v-list-item-action>
                                <v-list-item-title>{{ $t('title.export-campaigns') }}</v-list-item-title>
                            </v-list-item>
                            <hr/>
                            <v-list-item to="/accounts" v-if="canManageUsers()">
                                <v-list-item-action class="centered-icon">
                                    <v-icon>people</v-icon>
                                </v-list-item-action>
                                <v-list-item-content>
                                    <v-list-item-title>{{ $t('title.all-accounts') }}</v-list-item-title>
                                </v-list-item-content>
                            </v-list-item>
                            <v-list-item to="/create-account">
                                <v-list-item-action class="centered-icon">
                                    <v-icon>add_circle_outline</v-icon>
                                </v-list-item-action>
                                <v-list-item-content>
                                    <v-list-item-title>{{ $t('title.user-create') }}</v-list-item-title>
                                </v-list-item-content>
                            </v-list-item>
                            <hr/>
                            <v-list-item to="/roles">
                                <v-list-item-action class="centered-icon">
                                    <v-icon>security</v-icon>
                                </v-list-item-action>
                                <v-list-item-content>
                                    <v-list-item-title>{{ $t('title.handleRoles') }}</v-list-item-title>
                                </v-list-item-content>
                            </v-list-item>
                            <v-list-item to="/profile">
                                <v-list-item-action class="centered-icon">
                                    <v-icon>person</v-icon>
                                </v-list-item-action>
                                <v-list-item-title>{{ $t('navbar.profile') }}</v-list-item-title>
                            </v-list-item>
                            <v-list-item href="#" @click.native="logout">
                                <v-list-item-action class="centered-icon">
                                    <v-icon>power_settings_new</v-icon>
                                </v-list-item-action>
                                <v-list-item-title>{{ $t('navbar.logout') }}</v-list-item-title>
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
                    <v-toolbar-title class="toolbar-title ml-4">Kami Outside</v-toolbar-title>
                </v-toolbar>
                <v-list dense>
                    <v-list-item ref="side-menu-messages" to="/messages">
                        <v-list-item-action>
                            <v-icon>mail_outline</v-icon>
                        </v-list-item-action>
                        <v-list-item-title>{{ $t('title.all-messages') }}</v-list-item-title>
                    </v-list-item>

                    <v-list-item v-if="canCreateMessage" ref="side-menu-create-message" to="/create-message">
                        <v-list-item-action>
                            <v-icon>add_circle_outline</v-icon>
                        </v-list-item-action>
                        <v-list-item-title>{{ $t('title.create-message') }}</v-list-item-title>
                    </v-list-item>

                    <v-list-group prepend-icon="date_range">
                        <template v-slot:activator>
                            <v-list-item-content>
                                <v-list-item-title>{{ $t('title.manage-milestones') }}</v-list-item-title>
                            </v-list-item-content>
                        </template>
                        <v-list-item to="/campaigns" exact>
                            <v-list-item-action/>
                            <v-list-item-content>
                                <v-list-item-title>{{ $t('title.all-milestones') }}</v-list-item-title>
                            </v-list-item-content>
                        </v-list-item>
                        <v-list-item v-if="canCreateMessage" to="/create-campaign">
                            <v-list-item-action/>
                            <v-list-item-content>
                                <v-list-item-title>{{ $t('title.create-milestone') }}</v-list-item-title>
                            </v-list-item-content>
                        </v-list-item>
                        <v-list-item v-if="canManageExports" to="/campaigns/export">
                            <v-list-item-action/>
                            <v-list-item-content>
                                <v-list-item-title>{{ $t('title.export-campaigns') }}</v-list-item-title>
                            </v-list-item-content>
                        </v-list-item>
                    </v-list-group>
                    <v-list-group prepend-icon="people" v-if="canManageUsers()">
                        <template v-slot:activator>
                            <v-list-item-content>
                                <v-list-item-title>{{ $t('title.handleUsers') }}</v-list-item-title>
                            </v-list-item-content>
                        </template>
                        <v-list-item to="/accounts">
                            <v-list-item-action/>
                            <v-list-item-content>
                                <v-list-item-title>{{ $t('title.all-accounts') }}</v-list-item-title>
                            </v-list-item-content>
                        </v-list-item>
                        <v-list-item to="/create-account">
                            <v-list-item-action/>
                            <v-list-item-content>
                                <v-list-item-title>{{ $t('title.user-create') }}</v-list-item-title>
                            </v-list-item-content>
                        </v-list-item>
                    </v-list-group>

                    <v-list-item v-if="canManageUsers" ref="side-menu-create-message" to="/roles">
                        <v-list-item-action>
                            <v-icon>security</v-icon>
                        </v-list-item-action>
                        <v-list-item-title>{{ $t('title.handleRoles') }}</v-list-item-title>
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
                                {{ meName }}
                                <v-icon>expand_more</v-icon>
                            </v-btn>
                        </template>
                        <v-list class="mt-9">
                            <v-list-item to="/profile">
                                <v-list-item-action class="centered-icon">
                                    <v-icon>person</v-icon>
                                </v-list-item-action>
                                <v-list-item-title>{{ $t('navbar.profile') }}</v-list-item-title>
                            </v-list-item>
                            <v-list-item href="#" @click.native="logout">
                                <v-list-item-action class="centered-icon">
                                    <v-icon>power_settings_new</v-icon>
                                </v-list-item-action>
                                <v-list-item-title>{{ $t('navbar.logout') }}</v-list-item-title>
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
    .centered-icon {
        margin-right: 5px !important;
        margin-left: 0 !important;
        padding: 0 !important;
    }

    .v-list-group__header__append-icon {
        min-width: 20px !important;
    }

    .v-toolbar__content {
        padding: 0px !important;
    }

    .v-navigation-drawer__content .v-toolbar__content {
        background-color: rgb(164, 199, 82);
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
        .v-list-item:hover {
            opacity: 0.9 !important;
        }

        .v-list-item__title {
            font-size: 12px !important;
        }

        .v-list-group__items .v-list-item {
            background-color: #505050 !important;
        }

        .v-list-item__action {
            margin: 0 0 0 0;
        }

        .v-list-group__header.v-list-item--active .v-list-item__title {
            color: white !important;
        }

        .v-list > .v-list-item--active .v-list-item__title {
            color: white !important;
        }

        .v-list-item--active {
            opacity: 0.8 !important;
            color: white !important;
        }

        .v-list-item--active .v-list-item__title {

            font-weight: bold !important;
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

        private get canCreateMessage() {
            return true
        }

        private get canManageExports() {
            return true
        }

        private logout() {
            this.$router.push("/login")
        }

        private get meName() {
            return this.$modules.accounts.meName
        }

        private canManageUsers() {
            return true
        }
    }
</script>
