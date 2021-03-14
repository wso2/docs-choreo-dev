import type { Context, PluginOptionAPI } from "@mr-hope/vuepress-types";
import type { ResolvedHopeThemeConfig } from "./types";
interface ThemeOptionAPI extends PluginOptionAPI {
    extend?: string;
}
declare const themeAPI: {
    (themeConfig: ResolvedHopeThemeConfig, ctx: Context): ThemeOptionAPI;
    config: (config: import("./types").HopeVuePressConfig) => import("./types").ResolvedHopeVuePressConfig;
};
export = themeAPI;
