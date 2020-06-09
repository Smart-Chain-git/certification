<template>
    <v-menu
            ref="menu"
            v-model="menu"
            :close-on-content-click="false"
            transition="scale-transition"
            offset-y
            max-width="290px"
            min-width="290px"
    >
        <template v-slot:activator="{ on }">
            <v-flex class="date-picker" sm="10" md="6">
                <v-text-field
                        autocomplete="off"
                        :value="datesRangeDisplay"
                        :label="label"
                        v-on="on"
                        outlined
                        readonly
                        dense
                        :color="color"
                />
            </v-flex>
            <v-flex class="mt-n1 pa-0" sm="2" md="6">
                <slot class="error-msg" name="error"/>
            </v-flex>
        </template>
        <v-date-picker
                :locale="$i18n.locale"
                :first-day-of-week="$t('first-day-of-week')"
                v-model="dates"
                @input="updateRange"
                @update:picker-date="eventDates"
                range
                no-title
                :color="color"
                :allowed-dates="allowedDate"
                :events="eventDatesArray"
                event-color="red lighten-1"
        >
            <v-spacer></v-spacer>
            <v-btn text :color="color" @click="resetDates">{{ $t('calendar.label.reset') }}</v-btn>
            <v-btn text :color="color" @click="$refs.menu.save(dates)">{{ $t('calendar.label.close') }}</v-btn>
        </v-date-picker>
    </v-menu>
</template>

<style lang="css">
    .date-picker {
        max-width: 400px;
    }
</style>

<script lang="ts">
    import {Component, Prop, Vue, Watch} from "vue-property-decorator"

    @Component
    export default class EditFormDateRange extends Vue {

        @Prop(Array) private readonly value!: Array<string>
        @Prop(Array) private readonly notAllowedDates: Array<any> | undefined
        @Prop(String) private readonly color!: string
        @Prop(String) private readonly label!: string
        @Prop(Function) private updateRange!: () => void

        private eventDatesArray: Array<string> = []
        private menu = false

        private get dates(): Array<string> {
            return this.value
        }

        private set dates(dates: Array<string>) {
            this.$emit("input", dates)
        }

        private get datesRangeDisplay(): string | null {
            let fromDisplay = ""
            let toDisplay = ""

            this.dates.sort()

            if (this.dates[0] !== "" && this.dates[0] != null) {
                fromDisplay = new Date(this.dates[0]).toLocaleDateString()
            }
            if (this.dates[1] !== "" && this.dates[1] != null) {
                toDisplay = new Date(this.dates[1]).toLocaleDateString()
            }
            if (toDisplay === "") {
                if (fromDisplay === "") {
                    return null
                }
                return fromDisplay
            }
            return fromDisplay + " - " + toDisplay
        }

        private resetDates() {
            this.dates = ["", ""]
        }

        private allowedDate = (date: string) => {
            const time = Date.parse(date)
            if (undefined !== this.notAllowedDates) {
                for (const notAllowedRange of this.notAllowedDates) {
                    if (time >= Date.parse(notAllowedRange.from) && time <= Date.parse(notAllowedRange.to)) {
                        return false
                    }
                }
            }
            return true
        }

        @Watch("notAllowedDates")
        private eventDates() {
            this.eventDatesArray = []
            if (undefined !== this.notAllowedDates) {
                for (const notAllowedRange of this.notAllowedDates) {
                    const date: Date = new Date(notAllowedRange.from)
                    const to: Date = new Date(notAllowedRange.to)
                    while (date.getTime() <= to.getTime()) {
                        this.eventDatesArray[this.eventDatesArray.length] =
                            date.toISOString().split("T")[0]
                        date.setDate(date.getDate() + 1)
                    }
                }
            }
        }
    }
</script>
