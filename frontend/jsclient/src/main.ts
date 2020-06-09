import "@/plugins/componentsLoader"
import i18n from "@/plugins/i18n"
import "@/plugins/modulesLoader"
import "@/plugins/registerServiceWorker"
import vuetify from "@/plugins/vuetify"
import router from "@/router/router"
import store from "@/store/store"
import {App} from "@/ui/components"
import moment from "moment"
import "core-js" // Add polyfills for older browsers; replaced during compilation by babel {"useBuiltIns": "entry"}.
import Vue from "vue"

Vue.config.productionTip = false


Vue.filter("formatDate", (value: string) => {
    return moment(String(value)).format("DD/MM/YYYY HH:MM:SS")
})


new Vue({
    router,
    store,
    i18n,
    vuetify,
    render: (h) => h(App),
}).$mount("#app")
