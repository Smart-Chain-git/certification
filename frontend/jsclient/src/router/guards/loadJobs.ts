import modules from "@/store/modules"
import {NavigationGuardNext, Route} from "vue-router"

export function loadCurrentJob(from: Route, to: Route, next: NavigationGuardNext) {
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

export function loadJobs(count: number) {
    return (from: Route, to: Route, next: NavigationGuardNext) => {
        modules.jobs.loadLastJobs(count).then(() => {
            next()
        }).catch(() => next("/"))
    }
}
