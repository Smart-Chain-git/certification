import Vue from "vue"
import moment from "moment"
import VueMoment from "vue-moment"
import i18n from "@/plugins/i18n"

Vue.use(VueMoment, {
    moment,
})

moment.locale("en")

Vue.filter("formatDate", (date?: Date) => {
    if (date === undefined) {
        return i18n.t("moment.unknown-date")
    }
    return moment(date).format("L")

})

Vue.filter("formatTimestamp", (date?: Date) => {
    if (date === undefined) {
        return i18n.t("moment.unknown-date")
    }
    return moment(date).format("L LTS")
})
