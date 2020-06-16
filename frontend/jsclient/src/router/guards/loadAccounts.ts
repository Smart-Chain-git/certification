import modules from "@/store/modules"
import {NavigationGuardNext, Route} from "vue-router"

export function loadMeIfLogged(to: Route, from: Route, next: NavigationGuardNext) {
    modules.accounts.initToken().then(() => {
        modules.accounts.loadMe().then(() => {
            next()
        }).catch(() => {
            next()
        })
    }).catch(() => next())
}

export function loadTokens(to: Route, from: Route, next: NavigationGuardNext) {
    modules.tokens.loadTokens().then(() => {
        next()
    })
}

export function loadAccounts(to: Route, from: Route, next: NavigationGuardNext) {
    modules.accounts.initToken().then(() => {
        modules.accounts.loadAccounts().then(() => {
            next()
        }).catch(() => {
            next("/")
        })
    }).catch(() => {
        next("/")
    })
}

export function loadAccount(to: Route, from: Route, next: NavigationGuardNext) {
    const id = to.params.id

    modules.accounts.loadAccount(id).then(() => {
        next()
    }).catch(() => {
        next("/")
    })
}
