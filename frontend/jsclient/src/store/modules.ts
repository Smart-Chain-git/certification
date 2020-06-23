import AccountsModule from "@/store/modules/AccountsModule"

import FilesModule from "@/store/modules/FilesModule"
import SignaturesModule from "@/store/modules/SignatureModule"
import JobsModule from "@/store/modules/JobsModule"
import TokensModule from "@/store/modules/TokensModule"
import store from "@/store/store"

const accounts = new AccountsModule({
    store,
    name: "accounts",
})

const signatures = new SignaturesModule({
    store,
    name: "signatures",
})

const jobs = new JobsModule({
    store,
    name: "jobs",
    accountsModule: accounts,
})

const tokens = new TokensModule({
    store,
    name: "tokens",
})

const files = new FilesModule({
    store,
    name: "files",
    accountsModule: accounts,
})


const modules = {
    accounts,
    signatures,
    jobs,
    tokens,
    files,
}

export type Modules = typeof modules
export default modules
