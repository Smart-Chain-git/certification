<!--
    A standard button in the app.

    Usage:

        <IconButton leftIcon="" rightIcon="" ...other>
            text
        </IconButton

        <IconButton @click="stuff()" />
-->

<template>
    <v-btn :small="small" v-bind="$attrs" @click="$emit('click')" :class="(small ? 'pl-1' : '')" :text="text">
        <v-icon v-if="leftIcon" :class="'left-icon' + (small ? 'mr-1' : ' mr-2')">{{ leftIcon }}</v-icon>
        <slot/>
        <v-icon v-if="rightIcon" :class="'right-icon' + (small ? 'ml-1' : ' ml-2')">{{ rightIcon }}</v-icon>
    </v-btn>
</template>

<style type="scss" scoped>

    .v-btn {
        /* remove vuetify default style */
        text-transform: none;
        letter-spacing: 0;
    }

</style>

<script lang="ts">
    import {Component, Prop, Vue} from 'vue-property-decorator'

    @Component
    export default class IconButton extends Vue {

        @Prop(String) private readonly leftIcon!: string
        @Prop(String) private readonly rightIcon!: string
        @Prop({default: false}) private readonly small!: boolean
        @Prop({default: false}) private readonly text!: boolean

    }
</script>
