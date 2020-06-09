import AccountsModule from "@/store/modules/AccountsModule"
import JobsModule from "@/store/modules/JobsModule"
import store from "@/store/store"

const accounts = new AccountsModule({
    store,
    name: "accounts",
})

const jobs = new JobsModule({
    store,
    name: "jobs",
})



const modules = {
    accounts,
    jobs,
}

export type Modules = typeof modules
export default modules
