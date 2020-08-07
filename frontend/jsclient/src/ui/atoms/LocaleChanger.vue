<template>
  <v-overflow-btn
      v-model="$i18n.locale"
      :items="langs"
      :dark="textColor === 'white'"
  >
    <template v-slot:selection="{item: selected}">
      {{ selected.text }}
    </template>
    <template v-slot:item="{item}">
      <country-flag :country="item.flag"/>
    </template>
  </v-overflow-btn>
</template>

<style lang="scss" scoped>
.v-overflow-btn {
  width: 4em;
}
</style>

<script lang="ts">

import {Component, Prop, Vue} from "vue-property-decorator"

@Component
export default class LocaleChanger extends Vue {
  @Prop(String) private textColor!: string

  private langs = [
    {
      text: "EN",
      value: "en",
      flag: "us",
    }, {
      text: "FR",
      value: "fr",
      flag: "fr",
    },
  ]
}
</script>


