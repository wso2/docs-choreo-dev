"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.config = void 0;
const vuepress_shared_1 = require("@mr-hope/vuepress-shared");
const defaultConfig_1 = require("./defaultConfig");
const locales_1 = require("./locales");
const themeConfig_1 = require("./themeConfig");
const config = (config) => {
    // merge default config
    vuepress_shared_1.deepAssignReverse(defaultConfig_1.defaultConfig, config);
    const resolvedConfig = config;
    themeConfig_1.resolveThemeConfig(resolvedConfig.themeConfig);
    locales_1.resolveLocales(resolvedConfig);
    return resolvedConfig;
};
exports.config = config;
//# sourceMappingURL=config.js.map