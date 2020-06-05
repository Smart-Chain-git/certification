<!--
    This component is used to create a row in the edit form.
    The title is the name of the row, rendered on the left of the input.

    It should be extended and not be used raw. See EditFormRowAutocomplete for
    an example.

    If the component is editable, we display the extended version, wich shows
    the controls to edit the value. Otherwise, we just display the value as
    plain text.

    Usage :

        <EditFormRow title="foo" :value="..." :editable="boolean">
            <Whatever>
        </EditFormRow>
-->

<template>
    <v-row fluid class="mx-0 mb-2 edit-form-input-row">
        <v-flex xl2 md3 xs12 class="f-title">
            {{ title }}
            <slot name="icon"/>
        </v-flex>
        <v-flex xl10 md9 xs12>
            <v-row row class="ma-0 mt-1">
                <slot class="font-weight-bold mt-2" v-if="editable"/>
                <span v-else class="font-weight-bold mt-2">
                    {{ value }}
                </span>
            </v-row>
        </v-flex>
    </v-row>
</template>

<style type="text/css" scoped>

    .edit-form-input-row {
        min-height: 3rem;
        margin-bottom: .2rem;
    }

    .f-title {
        font-size: 14px !important;
        height: 24px !important;
        line-height: 24px !important;
        margin-top: calc(1.5rem - 12px) !important;
    }

    .edit-form-input-row > .flex {
        flex-grow: 0;
    }

</style>

<script lang="ts">
    import {Component, Prop, Vue} from "vue-property-decorator"

    @Component
    export default class EditFormRow extends Vue {
        @Prop(String) private readonly title!: string
        @Prop({default: null}) private readonly value!: string
        @Prop(Boolean) private readonly editable!: boolean
    }
</script>
