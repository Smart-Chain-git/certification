<!--
    Used for the app main contents.
    Sets the general style of the app's cards.
-->

<template>
    <v-card class="pa-6 ma-6" :width="width" :max-width="maxWidth" :height="height">
        <slot/>
    </v-card>
</template>
<script lang="ts">
    import {Component, Prop, Vue} from "vue-property-decorator"

    @Component
    export default class Card extends Vue {
        @Prop({default: "auto"}) private readonly width!: string
        @Prop({default: "auto"}) private readonly height!: string
        @Prop({default: "100%"}) private readonly maxWidth!: string
    }
</script>
