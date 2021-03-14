import Vue from "vue";
import BloggerInfo from "@theme/components/Blog/BloggerInfo.vue";
import NavLinks from "@theme/components/NavLinks.vue";
import SidebarLinks from "@theme/components/SidebarLinks.vue";
export default Vue.extend({
    name: "Sidebar",
    components: { BloggerInfo, SidebarLinks, NavLinks },
    props: {
        items: { type: Array, required: true },
    },
    computed: {
        blogConfig() {
            return this.$themeConfig.blog || {};
        },
        sidebarDisplay() {
            return this.blogConfig.sidebarDisplay || "none";
        },
    },
});
//# sourceMappingURL=Sidebar.js.map