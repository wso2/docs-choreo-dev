import Vue from "vue";
import DropdownTransition from "@theme/components/DropdownTransition.vue";
import { isActive } from "@theme/util/path";
export default Vue.extend({
    name: "SidebarGroup",
    components: { DropdownTransition },
    props: {
        item: {
            type: Object,
            required: true,
        },
        open: { type: Boolean },
        depth: { type: Number, required: true },
    },
    beforeCreate() {
        // eslint-disable-next-line
        this.$options.components.SidebarLinks = require("@theme/components/SidebarLinks.vue").default;
    },
    methods: {
        getIcon(icon) {
            const { iconPrefix } = this.$themeConfig;
            return this.$themeConfig.sidebarIcon !== false && icon
                ? `${iconPrefix === "" ? "" : iconPrefix || "icon-"}${icon}`
                : "";
        },
        isActive,
    },
});
//# sourceMappingURL=SidebarGroup.js.map