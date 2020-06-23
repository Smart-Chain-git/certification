import {resetStore} from "@/store/actions/globalActions"
import modules from "@/store/modules"
import {NavigationGuardNext, Route} from "vue-router"


export default function(to: Route, from: Route, next: NavigationGuardNext) {
    const token = to.params.token

    modules.accounts.invalidateToken()
        .then(() => {
            resetStore(modules)
            modules.accounts.loadActivationToken(token)
                .then(() => {
                    modules.accounts.getAccountByActivationToken()
                        .then(() => {
                            next()
                        }).catch(() => {
                            next()
                        },
                    )
                })
        })
}
