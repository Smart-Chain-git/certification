/*
    This not a real VueJS plugin.
    We load all the components exported from the UI and register them in Vue.

    Reference : https://eldarion.com/blog/2018/10/23/loading-global-vue-components/
*/

import Vue from "vue"
import * as components from "@/ui/components"

Object.entries(components).map(([name, component]) =>
    Vue.component(name, component),
)
