import modules from "@/store/modules"
import {Route, NavigationGuardNext} from "vue-router"

export default (from: Route, to: Route, next: NavigationGuardNext) => {
    if (modules.accounts.meAccount === undefined) {
        next("/")
    } else {
        modules.jobs.loadJobs({accountId: modules.accounts.meAccount.id}).then(() => {
            next()
        }).catch((_) => {
            next("/")
        })
    }
}
