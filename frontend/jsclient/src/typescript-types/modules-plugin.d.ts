/*
    Here we define (for the typescript compiler) the property $modules on vue
    instance objects. It is added by the plugin `plugins/modulesLoader.ts` at
    app startup.

    Reference : https://www.mistergoodcat.com/post/vuejs-plugins-with-typescript
*/

import { Modules } from '@/store/modules'

declare module 'vue/types/vue' {
    interface Vue {
        $modules: Modules
    }
}
