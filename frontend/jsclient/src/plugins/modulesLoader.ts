/*
    This plugin, defined and used at the same time, adds the store"s modules to
    vue instance objects. It will be accessible in the components.

    ! Needs to be called after Vue.use(Vuex) !
*/

import Vue from "vue"

import modules from "@/store/modules"

Vue.use({
    install: (V) => {
        V.prototype.$modules = modules
    },
})
