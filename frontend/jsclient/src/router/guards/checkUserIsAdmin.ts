import {fetchData} from "@/store/actions/globalActions"
import modules from "@/store/modules"
import {NavigationGuardNext, Route} from "vue-router"

export default (to: Route, from: Route, next: NavigationGuardNext) => {
    if (modules.accounts.meAccount?.isAdmin) {
        return next()
    } else {
        return next("/")
    }
}

