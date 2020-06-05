import modules from "@/store/modules"
import {NavigationGuardNext, Route} from "vue-router"

export default (to: Route, from: Route, next: NavigationGuardNext) => {
    if (modules.accounts.meAccount !== undefined) {
        next("/signature-check")
    } else {
        next()
    }
}

