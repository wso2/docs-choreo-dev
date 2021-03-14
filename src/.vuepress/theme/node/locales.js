"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.resolveLocales = void 0;
const vuepress_shared_1 = require("@mr-hope/vuepress-shared");
const resolveLocales = (config) => {
    // ensure locales config
    if (!config.locales)
        config.locales = {};
    const { baseLang = "en-US" } = config.themeConfig;
    const { locales } = config;
    // set locate for base
    locales["/"] = {
        lang: baseLang,
        ...(locales["/"] || {}),
    };
    // handle other languages
    Object.keys(config.themeConfig.locales).forEach((path) => {
        if (path === "/")
            return;
        locales[path] = { lang: vuepress_shared_1.path2Lang(path), ...(locales[path] || {}) };
    });
};
exports.resolveLocales = resolveLocales;
//# sourceMappingURL=locales.js.map