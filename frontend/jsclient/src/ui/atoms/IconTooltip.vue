<!--
    This component is used to insert a icon with a tooltip triggered when the icon is hovered.
    The icon is the material design name of the icon. The text is the text of the tooltip.

    Usage:
        <IconTooltip icon="info" text="Yolo" />
-->

<template>
    <v-tooltip bottom>
        <template v-slot:activator="{ on }">
            <v-icon class="tooltip-icon" v-on="on">{{ icon }}</v-icon>
        </template>
        <span>{{ text }}</span>
    </v-tooltip>
</template>

<style lang="scss">
    .tooltip-icon:hover {
        color: var(--v-circle_icon-base);
    }
</style>

<script lang="ts">
    import Vue from "vue"
    import {Component, Prop} from "vue-property-decorator"

    @Component
    export default class IconTooltip extends Vue {
        @Prop(String) private readonly icon!: string
        @Prop(String) private readonly text!: string
    }
</script>
