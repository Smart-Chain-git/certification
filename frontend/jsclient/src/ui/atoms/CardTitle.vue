<!--
    The title of a card in the app.

    Right now, it is used only for creation/edit form titles, the icon is
    always needed. In the futur, we will probably need to separate it in several
    titles.
-->
<template>
    <v-card-title class="mb-6 pa-0">
        <CircleIcon :type="type" :number="number">
            {{ icon }}
        </CircleIcon>
        <h2 class="title-text">
            <slot/>
        </h2>
    </v-card-title>
</template>

<style type="text/css" scoped>
    .title-text {
        margin-left: 20px;
        color: var(--var-color-blue-sword)
    }
</style>

<script lang="ts">
    import { Component, Vue, Prop } from "vue-property-decorator"

    @Component
    export default class CardTitle extends Vue {
        @Prop({type: String, default: "icon"}) private readonly type!: string
        @Prop(String) private readonly icon!: string
        @Prop(Number) private readonly number!: number
    }
</script>
