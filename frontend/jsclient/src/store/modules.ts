import AccountsModule from "@/store/modules/AccountsModule"
import store from "@/store/store"

const accounts = new AccountsModule({
    store,
    name: "accounts",
})



const modules = {
    accounts,
}

export type Modules = typeof modules
export default modules
