
<template>
  <div class="mt-10">
      <div class="stats">{{ number }}</div>
      <div class="stats-details">{{ detail }}</div>
  </div>
</template>

<style type="scss" scoped>
    .stats {
        font-size: 60px;
        font-weight: bold;
        color: var(--var-color-blue-sword);
        line-height: 1;
    }

    .stats-details {
        margin-top: 0px;
        margin-left: 4px;
        font-size: 20px;
        color: var(--var-color-blue-sword);
    }
</style>

<script lang="ts">
    import {Component, Prop, Vue} from "vue-property-decorator"

    @Component
    export default class StatBoard extends Vue {

        @Prop(Number) private readonly number!: string
        @Prop(String) private readonly detail!: string

    }
</script>
