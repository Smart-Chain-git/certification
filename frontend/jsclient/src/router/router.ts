import checkUserHasPubKey from "@/router/guards/checkUserHasPubKey"
import checkUserIsAdmin from "@/router/guards/checkUserIsAdmin"
import fetchDataAndRedirect from "@/router/guards/fetchDataAndRedirect"
import {loadAccount, loadAccounts, loadMeIfLogged, loadTokens} from "@/router/guards/loadAccounts"
import loadCurrentJob from "@/router/guards/loadCurrentJob"

import {loadCurrentJob, loadJobs} from "@/router/guards/loadJobs"
import {loadMeIfLogged, loadTokens} from "@/router/guards/loadAccounts"
import {
    Accounts,
    AppTemplate,
    ChannelManagement,
    Dashboard,
    Documents,
    EditAccount,
    JobDetail,
    Jobs,
    Login,
    ReceptionTemplate,
    Resources,
    SignatureCheck,
    SignatureRequest,
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
            beforeEnter: loadMeIfLogged,
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
                    props: {
                        access: "selfEditing",
                    },
                },
                {
                    path: "create-account",
                    component: EditAccount,
                    beforeEnter: checkUserIsAdmin,
                    props: {
                        access: "creating",
                    },
                },
                {
                    path: "accounts/:id",
                    component: EditAccount,
                    beforeEnter: multiguard([checkUserIsAdmin, loadAccount]),
                    props: {
                        access: "adminEditing",
                    },
                },
                {
                    path: "channel-management",
                    component: ChannelManagement,
                },
                {
                    path: "dashboard",
                    component: Dashboard,
                    beforeEnter: loadJobs(8),
                },
                {
                    path: "jobs",
                    component: Jobs,
                    beforeEnter: loadTokens,
                },
                {
                    path: "jobs/:id",
                    component: JobDetail,
                    props: true,
                    beforeEnter: loadCurrentJob,
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
                    component: Accounts,
                    beforeEnter: multiguard([checkUserIsAdmin, loadAccounts]),
                },
            ],
        },
    ],
})

export default router

