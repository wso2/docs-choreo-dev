import Vue from "vue";
import Anchor from "@theme/components/Anchor.vue";
import Comment from "@Comment";
import MyTransition from "@theme/components/MyTransition.vue";
import PageEdit from "@theme/components/PageEdit.vue";
import PageInfo from "@PageInfo";
import PageNav from "@theme/components/PageNav.vue";
import Password from "@theme/components/Password.vue";
export default Vue.extend({
    name: "Page",
    components: {
        Anchor,
        Comment,
        MyTransition,
        PageEdit,
        PageInfo,
        PageNav,
        Password,
    },
    props: {
        sidebarItems: {
            type: Array,
            default: () => [],
        },
        headers: {
            type: Array,
            default: () => [],
        },
    },
    data: () => ({
        password: "",
    }),
    computed: {
        commentEnable() {
            return this.$themeConfig.comment !== false;
        },
        pagePassword() {
            const { password } = this.$frontmatter;
            return typeof password === "number"
                ? password.toString()
                : typeof password === "string"
                    ? password
                    : "";
        },
        pageDescrypted() {
            return this.password === this.pagePassword;
        },
    },
});
//# sourceMappingURL=Page.js.map