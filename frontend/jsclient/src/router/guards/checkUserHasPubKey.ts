import {fetchData} from "@/store/actions/globalActions"
import modules from "@/store/modules"
import {NavigationGuardNext, Route} from "vue-router"

export default (to: Route, from: Route, next: NavigationGuardNext) => {
    if (modules.accounts.meAccount?.pubKey !== null) {
        return next()
    } else {
        return next("/")
    }
}

