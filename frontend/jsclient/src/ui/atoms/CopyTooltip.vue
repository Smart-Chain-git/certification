<!--
    Component for clickable tooltip with copy action.

    Usage:
        <CopyTooltip copy="" label="" actionText="" />
        copy : the text to copy
        label: some extra info to display before "copy" on the tooltip
        actionText: the text to display on the link to launch copy function


-->

<template>
    <v-tooltip v-model="show" right>
        <template v-slot:activator="{ on }">
            <v-icon class="tooltip-icon" @click="show = !show">info</v-icon>
        </template>
        <template v-slot:default>
            <span>{{ label }} {{ copy }}</span>
            <span @click="copyElm(copy)" class="copyElm text-center" >{{ actionText }}</span>
        </template>
    </v-tooltip>
</template>

<style lang="scss">
    .tooltip-icon:hover {
        color: var(--var-color-orange-sword)
    }

    .copyElm {
        pointer-events: initial;
        cursor: pointer;
        padding-left: 10px;
        font-size: 9px;
    }

    .copyElm:hover {
        text-decoration: underline;
    }

</style>

<script lang="ts">
    import Vue from "vue"
    import {Component, Prop} from "vue-property-decorator"

    @Component
    export default class CopyTooltip extends Vue {
        @Prop(String) private readonly copy!: string
        @Prop(String) private readonly label!: string
        @Prop(String) private readonly actionText!: string
        private show: boolean = false


        private copyElm(ID: string) {
            // assign ID to a custom HTML element and copy its content to clipboard
            const el = document.createElement("textarea")
            el.value = ID
            document.body.appendChild(el)
            el.select()
            document.execCommand("copy")
            document.body.removeChild(el)
            this.show = false
        }
    }
</script>
