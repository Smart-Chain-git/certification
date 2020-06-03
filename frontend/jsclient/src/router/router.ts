import fetchDataAndRedirect from "@/router/guards/fetchDataAndRedirect"
import checkUserIsAdmin from "@/router/guards/checkUserIsAdmin"
import checkUserHasPubKey from "@/router/guards/checkUserHasPubKey"
import {
    AppTemplate,
    Login,
    ReceptionTemplate,
    Dashboard,
    Jobs,
    Documents,
    SignatureRequest,
    SignatureCheck,
    Resources,
    Settings,
} from "@/ui/components"
import Vue from "vue"
import Router from "vue-router"

import multiguard from "vue-router-multiguard"

Vue.use(Router)


const router = new Router({
    // We use hash mode because, with `history` mode, when we reload the page,
    // the server returns a 404 error. We need the URLs to stay only on the
    // front. The solution with `history` mode and a back controller redirecting
    // to `/` was considered too complicated.
    mode: "hash",
    base: process.env.BASE_URL,
    routes: [
        {
            path: "/login",
            component: ReceptionTemplate,
            children: [
                {path: "", component: Login},
            ],
        },
        {
            path: "/",
            component: AppTemplate,
            beforeEnter: fetchDataAndRedirect,
            children: [
                {path: "", component: Dashboard,},
                {
                    path: "dashboard",
                    component: Dashboard,
                },
                {
                    path: "jobs",
                    component: Jobs,
                },
                {
                    path: "documents",
                    component: Documents,
                },
                {
                    path: "signature-request",
                    component: SignatureRequest,
                    beforeEnter: checkUserHasPubKey,
                },
                {
                    path: "signature-check",
                    component: SignatureCheck,
                },
                {
                    path: "resources",
                    component: Resources,
                },
                {
                    path: "settings",
                    component: Settings,
                    beforeEnter: checkUserIsAdmin,
                },
            ],
        },
    ],
})

export default router

