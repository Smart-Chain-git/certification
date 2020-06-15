<!--
    A green circle with an icon inside.
-->

<template>
    <v-icon color="white">
        <slot/>
    </v-icon>
</template>

<style scoped>
    .v-icon {
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
