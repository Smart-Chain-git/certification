import fetchDataAndRedirect from "@/router/guards/fetchDataAndRedirect"
import checkUserIsAdmin from "@/router/guards/checkUserIsAdmin"
import checkUserHasPubKey from "@/router/guards/checkUserHasPubKey"
import redirectIfLogged from "@/router/guards/redirectIfLogged"
import { loadAccounts, loadMe, loadMeIfAny } from "@/router/guards/loadAccounts"
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
    EditAccount,
    ChannelManagement,
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
            path: "/index",
            component: SignatureCheck,
            beforeEnter: redirectIfLogged,
        },
        {
            path: "/signature-check",
            component: SignatureCheck,
            beforeEnter: fetchDataAndRedirect,
        },
        {
            path: "/",
            component: AppTemplate,
            beforeEnter: fetchDataAndRedirect,
            children: [
                {
                    path: "",
                    redirect: "/dashboard",
                },
                {
                    path: "profile",
                    component: EditAccount,
                },
                {
                    path: "channel-management",
                    component: ChannelManagement,
                },
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

