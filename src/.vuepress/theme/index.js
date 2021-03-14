"use strict";
const path_1 = require("path");
const eject_1 = require("./node/eject");
const plugins_1 = require("./node/plugins");
const config_1 = require("./node/config");
const getAlias = (themeConfig, ctx) => {
    const { siteConfig } = ctx;
    // Resolve algolia
    const isAlgoliaSearch = Boolean(themeConfig.algolia) ||
        Object.keys((siteConfig.locales && themeConfig.locales) || {}).some((base) => themeConfig.locales[base].algolia);
    const blogEnabled = themeConfig.blog !== false;
    const commentPluginEnabled = themeConfig.comment !== false;
    const commentEnabled = themeConfig.comment &&
        themeConfig.comment.type &&
        themeConfig.comment.type !== "disable";
    const themeColorEnabled = !(themeConfig.themeColor === false && themeConfig.darkmode === "disable");
    const noopModule = "vuepress-theme-hope/util/noopModule";
    return {
        "@AlgoliaSearchBox": isAlgoliaSearch
            ? themeConfig.algoliaType === "full"
                ? path_1.resolve(__dirname, "./components/AlgoliaSearch/Full.vue")
                : path_1.resolve(__dirname, "./components/AlgoliaSearch/Dropdown.vue")
            : noopModule,
        "@BlogInfo": blogEnabled
            ? path_1.resolve(__dirname, "./components/Blog/BlogInfo.vue")
            : noopModule,
        "@BlogHome": blogEnabled
            ? path_1.resolve(__dirname, "./components/Blog/BlogHome.vue")
            : noopModule,
        "@BlogPage": blogEnabled
            ? path_1.resolve(__dirname, "./components/Blog/BlogPage.vue")
            : noopModule,
        "@Comment": commentPluginEnabled && commentEnabled
            ? "@mr-hope/vuepress-plugin-comment/lib/client/Comment.vue"
            : noopModule,
        "@PageInfo": commentPluginEnabled
            ? "@mr-hope/vuepress-plugin-comment/lib/client/PageInfo.vue"
            : noopModule,
        "@ThemeColor": themeColorEnabled
            ? path_1.resolve(__dirname, "./components/Theme/ThemeColor.vue")
            : noopModule,
    };
};
// Theme API.
const themeAPI = (themeConfig, ctx) => ({
    alias: getAlias(themeConfig, ctx),
    plugins: plugins_1.getPluginConfig(themeConfig),
    additionalPages: themeConfig.blog === false
        ? []
        : [
            {
                path: "/article/",
                frontmatter: { layout: "Blog" },
            },
            {
                path: "/encrypt/",
                frontmatter: { layout: "Blog" },
            },
            {
                path: "/slide/",
                frontmatter: { layout: "Blog" },
            },
            {
                path: "/timeline/",
                frontmatter: { layout: "Blog" },
            },
        ],
    extendCli: (cli) => {
        cli
            .command("eject-hope [targetDir]", "copy vuepress-theme-hope into .vuepress/theme for customization.")
            .option("--debug", "eject in debug mode")
            .action((dir = ".") => {
            void eject_1.eject(dir);
        });
    },
});
themeAPI.config = config_1.config;
module.exports = themeAPI;
//# sourceMappingURL=index.js.map