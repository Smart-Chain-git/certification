import AccountsModule from "@/store/modules/AccountsModule"
import JobsModule from "@/store/modules/JobsModule"
import TokensModule from "@/store/modules/TokensModule"
import store from "@/store/store"

const accounts = new AccountsModule({
    store,
    name: "accounts",
})

const jobs = new JobsModule({
    store,
    name: "jobs",
})

const tokens = new TokensModule({
    store,
    name: "tokens",
})

const modules = {
    accounts,
    jobs,
    tokens,
}

export type Modules = typeof modules
export default modules
