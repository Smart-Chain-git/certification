<template>
  <Card>
    <vue-markdown id="markdown" :source="markdown"/>
  </Card>
</template>

<style lang="scss" scoped>
#markdown {
  padding: 2em;

  ::v-deep {
    h1 {
      color: var(--var-color-blue-sword);
    }

    ul {
      margin-top: 1em;
    }
  }
}
</style>

<script lang="ts">
import {Component, Vue} from "vue-property-decorator"
import VueMarkdown from "vue-markdown"
// @ts-ignore
import AboutMarkdown from "@/documentation/About.md"

@Component({
  components: {
    VueMarkdown,
  },
})
export default class About extends Vue {
  private get markdown() {
    return AboutMarkdown
  }
}
</script>

