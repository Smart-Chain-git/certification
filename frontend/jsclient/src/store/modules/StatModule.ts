import {Action, Module, Mutation, VuexModule} from "vuex-class-modules"
import {DashBoardStat} from "@/api/types"
import {statApi} from "@/api/statApi"


@Module
export default class StatModule extends VuexModule {

    public dashBoardStat: DashBoardStat = {jobsCreatedCount: 0, jobsProcessedCount: 0, documentsSignedCount: 0}

    @Action
    public async loadDashBoardStat() {
        await statApi.dashBoard().then((response: DashBoardStat) => {
            this.setDashBoard(response)
        })
    }

    @Mutation
    public setDashBoard(dashBoardStat: DashBoardStat) {
        this.dashBoardStat = dashBoardStat
    }

}
