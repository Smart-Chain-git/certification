import fetchDataAndRedirect from "@/router/guards/fetchDataAndRedirect"
import checkUserIsAdmin from "@/router/guards/checkUserIsAdmin"
import checkUserHasPubKey from "@/router/guards/checkUserHasPubKey"
import { loadMeIfLogged } from "@/router/guards/loadAccounts"
import loadCurrentJob from "@/router/guards/loadCurrentJob"
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
    JobDetail,
} from "@/ui/components"
import Vue from "vue"
import Router from "vue-router"

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
                    component: Settings,
                    beforeEnter: checkUserIsAdmin,
                },
            ],
        },
    ],
})

export default router

