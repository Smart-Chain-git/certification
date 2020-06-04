import modules from "@/store/modules"
import {NavigationGuardNext, Route} from "vue-router"

export function loadAccounts(to: Route, from: Route, next: NavigationGuardNext) {
    modules.accounts.loadAccounts().then(() => {
        next()
    })
}


export function loadMe(to: Route, from: Route, next: NavigationGuardNext) {
    modules.accounts.loadMe().then(() => {
        next()
    })
}
