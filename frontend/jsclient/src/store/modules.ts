import AccountsModule from "@/store/modules/AccountsModule"
import SignaturesModule from "@/store/modules/SignatureModule"
import store from "@/store/store"

const accounts = new AccountsModule({
    store,
    name: "accounts",
})

const signatures = new SignaturesModule({
    store,
    name: "signatures",
})

const modules = {
    accounts,
    signatures,
}

export type Modules = typeof modules
export default modules
