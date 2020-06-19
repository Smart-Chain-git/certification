<template>
    <v-menu
            ref="menu"
            v-model="menu"
            :close-on-content-click="false"
            transition="scale-transition"
            offset-y
    >
        <template v-slot:activator="{ on }">
            <v-flex class="date-picker">
                <v-text-field
                        autocomplete="off"
                        :value="datesRangeDisplay"
                        :label="label"
                        :placeholder="placeholder"
                        v-on="on"
                        outlined
                        readonly
                        dense
                        :color="color"
                        clearable
                />
            </v-flex>
            <v-flex class="mt-n1 pa-0" sm="2" md="6">
                <slot class="error-msg" name="error"/>
            </v-flex>
        </template>
        <v-date-picker
                :locale="$i18n.locale"
                :first-day-of-week="$t('first-day-of-week')"
                v-model="date"
                no-title
                :color="color"
                event-color="red lighten-1"
                :min="min"
        >
            <v-spacer></v-spacer>
            <v-btn text :color="color" @click="resetDate">{{ $t("calendar.label.reset") }}</v-btn>
            <v-btn text :color="color" @click="$refs.menu.save(date)">{{ $t("calendar.label.close") }}</v-btn>
        </v-date-picker>
    </v-menu>
</template>

<style lang="css">
    .date-picker {
        max-width: 240px !important;
    }
</style>

<script lang="ts">
    import {Component, Prop, Vue} from "vue-property-decorator"

    @Component
    export default class EditFormDate extends Vue {

        @Prop(Array) private readonly value!: string
        @Prop(String) private readonly color!: string
        @Prop(String) private readonly label!: string
        @Prop(String) private readonly min!: string
        @Prop(String) private readonly placeholder!: string

        private menu = false

        private get date(): string {
            return this.value
        }

        private set date(date: string) {
            this.$emit("input", date)
        }

        private get datesRangeDisplay(): string {
            if (this.date === "") {
                return ""
            }
            return new Date(this.date).toLocaleDateString()
        }

        private resetDate() {
            this.date = ""
        }
    }
</script>
