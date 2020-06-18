<!--
    Component to edit a template title.

    Usage :

        <EditFormTitleStatic label="..." v-model="title" />
-->

<template>
    <div>
        <v-text-field dense
                      :class="'edit-form-title-input ' + cssClass"
                      hide-details
                      :color="color"
                      outlined
                      :label="label"
                      v-model="title"
                      :placeholder="placeholder"
                      :type="type"
        />
    </div>
</template>

<style type="text/css">
    .edit-form-title-input input:focus::placeholder {
        color: transparent !important;
    }

    .edit-form-title-input {
        max-width: 240px;
        width: 240px;
    }
</style>

<script lang="ts">
    import {Component, Prop, Vue} from "vue-property-decorator"

    @Component
    export default class EditFormTitleEdit extends Vue {

        @Prop(String) private readonly label!: string
        @Prop(String) private readonly value!: string
        @Prop(String) private readonly placeholder!: string
        @Prop({default: "text"}) private readonly type!: string
        @Prop({default: ""}) private readonly cssClass!: string
        @Prop(String) private readonly color!: string

        private get title() {
            return this.value
        }

        private set title(title) {
            this.$emit("input", title)
        }
    }
</script>
