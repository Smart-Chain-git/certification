import {Modules} from "@/store/modules"

/**
 * Retrieve all the data required for the app to be operational
 * @param modules Access to the list of modules
 */
export const fetchData = async (modules: Modules) => {
    await modules.accounts.initToken().then(async () => await Promise.all([
        // modules.accounts.loadRoles(),
    ]))
}

/**
 * Reset all the store modules states.
 * It it aimed to be used for logout.
 * @param modules Access to the list of modules.
 */
export const resetStore = (modules: Modules) => {
    modules.accounts.reset()
}
