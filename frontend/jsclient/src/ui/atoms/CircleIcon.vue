<!--
    A green circle with an icon inside.
-->

<template>
    <v-icon>
        <slot/>
    </v-icon>
</template>

<style scoped>
    .v-icon {
        display: block;
        height: 40px;
        width: 40px;
        border-radius: 20px;
        background-color: var(--v-circle_icon-base);
        text-align: center;
        line-height: 40px;
        color: white;
    }
</style>
