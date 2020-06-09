import {fetchData} from "@/store/actions/globalActions"
import modules from "@/store/modules"
import {NavigationGuardNext, Route} from "vue-router"

export default (to: Route, from: Route, next: NavigationGuardNext) => {
    return fetchData(modules).then(() => {
        next()
    }).catch((_) => {
        next("/signature-check")
    })
}

