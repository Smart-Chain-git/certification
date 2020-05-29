module.exports = {
    pluginOptions: {
        i18n: {
            locale: 'fr',
            fallbackLocale: 'en',
            localeDir: 'locales',
            enableInSFC: true,
        },
    },
    chainWebpack: config => {
        // Set up null-loader to handle 'vuetify' while testing
        if (process.env.NODE_ENV === 'test') {
            const sassRule = config.module.rule('sass')
            sassRule.uses.clear()
            sassRule.use('null-loader').loader('null-loader')
        }
        // Make webpack use the config tsconfig.json to handle import paths.
        config.resolve.alias.delete('@')
        config.resolve
            .plugin('tsconfig-paths')
            .use(require('tsconfig-paths-webpack-plugin'))
    },
    outputDir: 'build/dist',
    transpileDependencies: ['vuetify'],
}
