/*
    Definition and usage of the plugin `vuetify`.
*/

import {getLocale} from "@/plugins/i18n"

import "@mdi/font/css/materialdesignicons.css"
import "material-design-icons-iconfont/dist/material-design-icons.css"
import Vue from "vue"
import Vuetify from "vuetify/lib"
import en from "vuetify/src/locale/en"
import fr from "vuetify/src/locale/fr"

Vue.use(Vuetify, {
    iconfont: "mdi",
    lang: {
        locales: {fr, en},
        current: getLocale(),
    },
})

// https://github.com/vuetifyjs/vuetify/issues/7387#issuecomment-508696828
// (first link)
export default new (Vuetify as any)({
    theme: {
        themes: {
            light: {
                primary: "#687D37",
                secondary: "#485B65",
                success: "#84b939",
                circle_icon: "#A4C752",
                editor_bg: "#485B65",
                error: "#B90000",
            },
        },
        options: {
            customProperties: true,
        },
        breakpoint: {
            thresholds: {
                xs: 340,
                sm: 540,
                md: 1420,
                lg: 1470,
                xl: 1670,
            },
        },
    },
})
