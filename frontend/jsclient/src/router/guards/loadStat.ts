import modules from "@/store/modules"
import {NavigationGuardNext, Route} from "vue-router"

export function loadDashBoard() {
    return (from: Route, to: Route, next: NavigationGuardNext) => {
        modules.stat.loadDashBoardStat().then(() => {
            next()
        }).catch(() => next("/"))
    }
}
