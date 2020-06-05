import fetchDataAndRedirect from "@/router/guards/fetchDataAndRedirect"
import checkUserIsAdmin from "@/router/guards/checkUserIsAdmin"
import checkUserHasPubKey from "@/router/guards/checkUserHasPubKey"
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
            path: "/signature-check",
            component: SignatureCheck,
            beforeEnter: multiguard([fetchDataAndRedirect, loadMe]),
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
                  /*  props: {selfEditing: true},*/
                    beforeEnter: loadMe,
                },
                {
                    path: "channel-management",
                    component: ChannelManagement,
                    beforeEnter: loadMe,
                },
                {
                    path: "dashboard",
                    component: Dashboard,
                    beforeEnter: loadMe,
                },
                {
                    path: "jobs",
                    component: Jobs,
                    beforeEnter: loadMe,
                },
                {
                    path: "documents",
                    component: Documents,
                    beforeEnter: loadMe,
                },
                {
                    path: "signature-request",
                    component: SignatureRequest,
                    beforeEnter: multiguard([loadMe, checkUserHasPubKey]),
                },
                {
                    path: "resources",
                    component: Resources,
                    beforeEnter: loadMe,
                },
                {
                    path: "settings",
                    component: Settings,
                    beforeEnter: multiguard([loadMe, checkUserIsAdmin]),
                },
            ],
        },
    ],
})

export default router

