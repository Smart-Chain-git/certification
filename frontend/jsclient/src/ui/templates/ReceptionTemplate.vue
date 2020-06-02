<!--
    This component is used to create the reception template, every screen when
    the user is not logged yet. Right now, it is only used for the Login.

    It renders the background image. It also center the <Whatever> component
    rendered inside it (usually a <Card>).

    It is used in the router and can render nested routes/components.
-->

<template>
    <v-content>
        <div class="background">
            <img src="@/ui/assets/reception-background.png" alt="reception background">
        </div>
        <v-container fill-height>
            <v-layout align-center justify-center>
                <router-view />
            </v-layout>
        </v-container>
    </v-content>
</template>

<style scoped>
    /* Reference : https://css-tricks.com/perfect-full-page-background-image/#article-header-id-2 */

    .background {
        position: fixed; 
        top: -50%; 
        left: -50%; 
        width: 200%; 
        height: 200%;
    }
    
    .background img {
        position: absolute; 
        top: 0; 
        left: 0; 
        right: 0; 
        bottom: 0; 
        margin: auto; 
        min-width: 50%;
        min-height: 50%;
        /* The blur of 4px was defined with try-and-errors. */
        filter: blur(4px);
    }

    .container {
        position: relative; /* otherwise the content is below the background image. */
    }
</style>
