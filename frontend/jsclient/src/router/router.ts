import checkUserHasPubKey from "@/router/guards/checkUserHasPubKey"
import checkUserIsAdmin from "@/router/guards/checkUserIsAdmin"
import fetchActivationToken from "@/router/guards/fetchActivationToken"
import fetchDataAndRedirect from "@/router/guards/fetchDataAndRedirect"
import {loadAccount, loadAccounts, loadMeIfLogged, loadTokens} from "@/router/guards/loadAccounts"
import {loadCurrentJob, loadJobs} from "@/router/guards/loadJobs"

import {
    About,
    Accounts,
    Activation,
    AppTemplate,
    ChannelManagement,
    Contact,
    Dashboard,
    Documents,
    EditAccount,
    JobDetail,
    Jobs,
    Login,
    ReceptionTemplate,
    Resources,
    SignatureCheck,
    SignatureCheckTemplate,
    SignatureHandler,
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
            path: "/activation/:token",
            component: ReceptionTemplate,
            children: [
                {path: "", component: Activation, props: true, beforeEnter: fetchActivationToken},
            ],
        },
        {
            path: "/signature-check",
            component: SignatureCheckTemplate,
            beforeEnter: loadMeIfLogged,
            children: [
                {
                    path: "",
                    component: SignatureCheck,
                },
                {
                    path: ":hashdoc",
                    component: SignatureCheck,
                    props: true,
                },
            ],
        },
        {
            path: "/about",
            component: SignatureCheckTemplate,
            beforeEnter: loadMeIfLogged,
            children: [
                {
                    path: "",
                    component: About,
                },
            ],
        },
        {
            path: "/contact",
            component: SignatureCheckTemplate,
            beforeEnter: loadMeIfLogged,
            children: [
                {
                    path: "",
                    component: Contact,
                },
            ],
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
                    beforeEnter: loadTokens,
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
                    component: SignatureHandler,
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

