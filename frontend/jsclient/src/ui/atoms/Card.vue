<!--
    Used for the app main contents.
    Sets the general style of the app's cards.
-->

<template>
    <v-card class="pa-6 ma-6" :width="width">
        <slot/>
    </v-card>
</template>
<script lang="ts">
    import {Component, Prop, Vue} from "vue-property-decorator"

    @Component
    export default class Card extends Vue {
        @Prop({default: ""}) private readonly width!: string
    }
</script>
