/*
    Definition and usage of the plugin `i18n`.
*/

import Vue from "vue"
import VueI18n, { LocaleMessages } from "vue-i18n"

import fr from "@/locales/fr.json"
import en from "@/locales/en.json"

export function getLocale(): string {
    return process.env.VUE_APP_I18N_LOCALE || process.env.VUE_APP_I18N_FALLBACK_LOCALE || "fr"
}

Vue.use(VueI18n)

export default new VueI18n({
  locale: getLocale(),
  fallbackLocale: getLocale(),
  messages: { fr, en },
})

export function tableFooter(i18n: (key: string) => any): any {
    return {
        "items-per-page-options" : [5, 10, 15, 50],
        "items-per-page-text": i18n("message.footer.items-per-page-text") + '\xa0',
        "page-text": i18n("message.footer.page-text"),
    }
}
