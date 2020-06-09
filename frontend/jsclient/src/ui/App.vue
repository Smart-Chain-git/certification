<template>
    <v-app>
        <router-view/>
    </v-app>
</template>

<style lang="scss">

    #app {
        -webkit-font-smoothing: antialiased;
        -moz-osx-font-smoothing: grayscale;
        font-size: 14px;
        background-color: #f0f0f7;
    }

    h1 {
        font-size: 24px;
    }

    h2 {
        font-size: 18px;
    }

</style>

<script lang="ts">
    import {Component, Vue} from "vue-property-decorator"

    Component.registerHooks(["beforeRouteEnter", "beforeRouteLeave", "beforeRouteUpdate"])

    @Component
    export default class App extends Vue {

    }
</script>
