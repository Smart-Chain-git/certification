<template>
    <div>
        <NavbarTop showLogo="true" backgroundColor="var(--var-color-blue-sword)" textColor="white"/>
        <v-content>Signature Check</v-content>
    </div>
</template>

<style lang="scss" scoped>

</style>
<script lang="ts">
    import {NavbarTop} from "@/ui/components"
    import {Component, Vue} from "vue-property-decorator"

    @Component({
        components: {NavbarTop},
    })

    export default class SignatureCheck extends Vue {
    }
</script>

