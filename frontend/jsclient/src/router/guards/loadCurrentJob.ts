import modules from "@/store/modules"
import {NavigationGuardNext, Route} from "vue-router"

export default (from: Route, to: Route, next: NavigationGuardNext) => {
    const id = from.params.id

    if (id !== undefined) {
        modules.jobs.loadJob(id).then(() => {
            next()
        }).catch(() => {
            next("/")
        })
    } else {
        next("/")
    }
}
