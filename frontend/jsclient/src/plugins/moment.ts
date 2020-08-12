import Vue from "vue"
import moment from "moment"
import VueMoment from "vue-moment"

Vue.use(VueMoment, {
    moment,
})

moment.locale("en")

Vue.filter("formatDate", (value: string) => {
    return moment(String(value)).format("DD/MM/YYYY HH:mm:ss")
})
