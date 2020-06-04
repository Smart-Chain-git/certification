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

export function loadMeIfAny(to: Route, from: Route, next: NavigationGuardNext) {
    if (modules.accounts.COOKIE_TOKEN !== null) {
        modules.accounts.loadMe().then(() => {
            next()
        })
    }
}
