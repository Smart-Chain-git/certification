<!--
    A green circle with an icon inside.
-->

<template>
    <v-icon class="icon_style" v-if="type === 'icon'" color="white">
        <slot/>
    </v-icon>
    <span class="icon_style" v-else>
        {{ number }}
    </span>
</template>

<style scoped>
    .icon_style {
        display: block;
        height: 40px;
        width: 40px;
        border-radius: 20px;
        background-color: var(--var-color-blue-sword);

        text-align: center;
        line-height: 40px;
        color: white;
    }
</style>
<script lang="ts">
    import {Component, Prop, Vue} from "vue-property-decorator"

    @Component
    export default class CircleIcon extends Vue {
        @Prop(String) private readonly type!: string
        @Prop(Number) private readonly number!: number
    }
</script>
