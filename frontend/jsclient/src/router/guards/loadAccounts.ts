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
